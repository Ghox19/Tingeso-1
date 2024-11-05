package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.entities.LoanEntity;
import com.prestabancobackend.form.*;
import com.prestabancobackend.getForms.ClientLoanGetForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientLoanService {

    private final ClientLoanRepository clientLoanRepository;
    private final ClientRepository clientRepository;
    private final DocumentService documentService;

    private final LoanService loanService;

    @Autowired
    public ClientLoanService(ClientLoanRepository clientLoanRepository, ClientRepository clientRepository, DocumentService documentService, LoanService loanService) {
        this.clientLoanRepository = clientLoanRepository;
        this.clientRepository = clientRepository;
        this.documentService = documentService;
        this.loanService = loanService;
    }

    public ResponseEntity<Object> addClientLoan(ClientLoanForm clientLoanForm) {
        Optional<ClientEntity> optionalClient = this.clientRepository.findByRut(clientLoanForm.getRut());

        if (optionalClient.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("No se encontró el cliente con el RUT especificado");
        }

        ClientEntity client = optionalClient.get();

        if (clientLoanForm.getRut() == null || clientLoanForm.getYears() == null ||
                clientLoanForm.getInterest() == null || clientLoanForm.getLoanAmount() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Faltan datos requeridos");
        }

        // Validate monthly payment calculation
        double mensualPay = clientLoanForm.getMensualPay();
        if (mensualPay == 0) { // Assuming 0 represents "Valor obtenido"
            return ResponseEntity
                    .badRequest()
                    .body("Falta calcular el valor de la cuota mensual");
        }

        LoanEntity loantype = loanService.getLoanByName(clientLoanForm.getLoanName());
        if (clientLoanForm.getInterest() < loantype.getMinInterest() || clientLoanForm.getInterest() > loantype.getMaxInterest()){
            return ResponseEntity
                    .badRequest()
                    .body("El Interes del prestamo esta fuera de los limites");
        }

        if (clientLoanForm.getYears() > loantype.getMaxYears()){
            return ResponseEntity
                    .badRequest()
                    .body("Los años del prestamo esta fuera de los limites");
        }

        float loanRatio = (float) (clientLoanForm.getLoanAmount() * 100) / clientLoanForm.getPropertyValue();
        if (loanRatio > loantype.getMaxAmount()){
            return ResponseEntity
                    .badRequest()
                    .body("La cantidad del prestamo esta fuera de los limites");
        }
        // Check monthly payment to income ratio
        double cuotaIncome = (mensualPay / client.getMensualIncome()) * 100;
        if (cuotaIncome > 35) {
            return ResponseEntity
                    .badRequest()
                    .body("La cuota mensual excede el 35% del sueldo");
        }

        // Validate job years
        if (client.getJobYears() < 1) {
            return ResponseEntity
                    .badRequest()
                    .body("El cliente no tiene la antigüedad laboral mínima requerida (1 año)");
        }

        // Calculate and validate total debt ratio
        double totalDebt = client.getTotalDebt() + mensualPay;
        double debtCuota = (totalDebt / client.getMensualIncome()) * 100;
        if (debtCuota > 50) {
            return ResponseEntity
                    .badRequest()
                    .body("La deuda total excede el 50% del sueldo mensual");
        }


        // Validate age limit
        int totalYears = client.getYears() + clientLoanForm.getYears();
        if (totalYears > 70) {
            return ResponseEntity
                    .badRequest()
                    .body("El cliente excede el límite de edad permitido (70 años)");
        }


        ClientLoanEntity clientLoan = createAndSaveClientLoan(clientLoanForm, client, cuotaIncome, debtCuota, loanRatio);
        List<DocumentEntity> documents = processDocuments(clientLoanForm.getDocuments(), clientLoan);

        updateClientWithLoan(client, clientLoan, documents);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Se ingresó correctamente el préstamo del cliente");
    }

    private ClientLoanEntity createAndSaveClientLoan(ClientLoanForm form, ClientEntity client,Double cuotaIncome,Double debtCuota,Float loanRatio) {
        ClientLoanEntity clientLoan = new ClientLoanEntity();
        clientLoan.setClient(client);
        setClientLoanFields(clientLoan, form);
        clientLoan.setCuotaIncome(cuotaIncome);
        clientLoan.setDebtCuota(debtCuota);
        clientLoan.setLoanRatio(loanRatio);
        return this.clientLoanRepository.save(clientLoan);
    }

    private void setClientLoanFields(ClientLoanEntity clientLoan, ClientLoanForm form) {
        clientLoan.setLoanAmount(form.getLoanAmount());
        clientLoan.setLoanName(form.getLoanName());
        clientLoan.setInterest(form.getInterest());
        clientLoan.setYears(form.getYears());
        clientLoan.setMensualPay(form.getMensualPay());
        clientLoan.setFase(form.getFase());
        clientLoan.setPropertyValue(form.getPropertyValue());
    }

    private List<DocumentEntity> processDocuments(List<DocumentForm> documentForms, ClientLoanEntity clientLoan) {
        return documentForms.stream()
                .map(docForm -> {
                    DocumentEntity document = this.documentService.saveDocument(docForm);
                    document.setClientLoan(clientLoan);
                    return document;
                })
                .collect(Collectors.toList());
    }

    private void updateClientWithLoan(ClientEntity client, ClientLoanEntity clientLoan,
                                      List<DocumentEntity> documents) {
        clientLoan.setDocuments(documents);
        client.getLoans().add(clientLoan);
        this.clientRepository.save(client);
    }

    public ClientLoanGetForm getClientLoanById(Long id) {
        Optional<ClientLoanEntity> clientLoan = clientLoanRepository.findById(id);

        if (clientLoan.isPresent()) {
            return setClientLoanGetForm(clientLoan.get());
        } else {
            throw new EntityNotFoundException("Client Loan not found with id: " + id);
        }
    }

    public List<ClientLoanGetForm> getAllClientLoan() {
        List<ClientLoanEntity> clientLoans = this.clientLoanRepository.findAll();

        return clientLoans.stream()
                .map(this::setClientLoanGetForm)
                .collect(Collectors.toList());
    }

    public ClientLoanGetForm setClientLoanGetForm(ClientLoanEntity clientLoan){
        ClientLoanGetForm clientLoanGetForm = new ClientLoanGetForm();
        clientLoanGetForm.setId(clientLoan.getId());
        clientLoanGetForm.setInterest(clientLoan.getInterest());
        clientLoanGetForm.setLoanName(clientLoan.getLoanName());
        clientLoanGetForm.setYears(clientLoan.getYears());
        clientLoanGetForm.setLoanAmount(clientLoan.getLoanAmount());
        clientLoanGetForm.setMensualPay(clientLoan.getMensualPay());
        clientLoanGetForm.setFase(clientLoan.getFase());
        clientLoanGetForm.setClient(clientLoan.getClient());
        clientLoanGetForm.setSavings(clientLoan.getSaving());
        clientLoanGetForm.setPropertyValue(clientLoan.getPropertyValue());
        clientLoanGetForm.setCuotaIncome(clientLoan.getCuotaIncome());
        clientLoanGetForm.setDebtCuota(clientLoan.getDebtCuota());
        clientLoanGetForm.setMessage(clientLoan.getMessage());
        clientLoanGetForm.setLoanRatio(clientLoan.getLoanRatio());

        List<DocumentSaveForm> documentForms = clientLoan.getDocuments()
                .stream()
                .map(documentService::setDocumentSaveForm)
                .collect(Collectors.toList());

        clientLoanGetForm.setDocuments(documentForms);

        return clientLoanGetForm;
    }

    public Integer calculateMensualPay (CalculatorForm calculatorForm){
        BigDecimal monthlyInterest = BigDecimal.valueOf(calculatorForm.getInterest())
                .divide(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int totalMonths = calculatorForm.getYears() * 12;

        BigDecimal numerator = monthlyInterest.multiply(
                BigDecimal.ONE.add(monthlyInterest).pow(totalMonths)
        );
        BigDecimal denominator = BigDecimal.ONE.add(monthlyInterest).pow(totalMonths)
                .subtract(BigDecimal.ONE);

        BigDecimal monthlyPayment = BigDecimal.valueOf(calculatorForm.getLoanAmount())
                .multiply(numerator)
                .divide(denominator, 0, RoundingMode.HALF_UP);

        return monthlyPayment.intValue();
    }
}
