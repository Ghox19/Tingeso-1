package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.form.ClientLoanForm;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ClientLoanService {

    private final ClientLoanRepository clientLoanRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientLoanService(ClientLoanRepository clientLoanRepository, ClientRepository clientRepository) {
        this.clientLoanRepository = clientLoanRepository;
        this.clientRepository = clientRepository;
    }

    public ResponseEntity<Object> addClientLoan(ClientLoanForm clientLoanForm) {
        Optional<ClientEntity> optionalClient = this.clientRepository.findByRut(clientLoanForm.getRut());

        if (optionalClient.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ClientEntity client = optionalClient.get();

        ClientLoanEntity clientLoan = new ClientLoanEntity();
        clientLoan.setClient(client);
        clientLoan.setLoanAmount(clientLoanForm.getLoanAmount());
        clientLoan.setLoanName(clientLoanForm.getLoanName());
        clientLoan.setInterest(clientLoanForm.getInterest());
        clientLoan.setYears(clientLoanForm.getYears());
        clientLoan.setMensualPay(clientLoanForm.getMensualPay());
        clientLoan.setRequirementsApproved(clientLoanForm.getRequirementsApproved());

        client.getLoans().add(clientLoan);

        this.clientLoanRepository.save(clientLoan);

        return new ResponseEntity<>("Se ingreso correctamente el Usuario", HttpStatus.CREATED);
    }

    public List<ClientLoanEntity> getClientLoanByClient(Long id) {
        Optional<ClientEntity> optionalClient = clientRepository.findById(id);
        return optionalClient.map(clientLoanRepository::findByClient).orElse(null);
    }

    public List<ClientLoanEntity> getAllClientLoan (){
        return clientLoanRepository.findAll();
    }

    public Integer calculateMensualPay (ClientLoanEntity clientLoan){
        BigDecimal monthlyInterest = BigDecimal.valueOf(clientLoan.getInterest())
                .divide(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int totalMonths = clientLoan.getYears() * 12;

        BigDecimal numerator = monthlyInterest.multiply(
                BigDecimal.ONE.add(monthlyInterest).pow(totalMonths)
        );
        BigDecimal denominator = BigDecimal.ONE.add(monthlyInterest).pow(totalMonths)
                .subtract(BigDecimal.ONE);

        BigDecimal monthlyPayment = BigDecimal.valueOf(clientLoan.getLoanAmount())
                .multiply(numerator)
                .divide(denominator, 0, RoundingMode.HALF_UP);

        return monthlyPayment.intValue();
    }
}
