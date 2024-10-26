package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.form.*;
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

    @Autowired
    public ClientLoanService(ClientLoanRepository clientLoanRepository, ClientRepository clientRepository, DocumentService documentService) {
        this.clientLoanRepository = clientLoanRepository;
        this.clientRepository = clientRepository;
        this.documentService = documentService;
    }

    public ResponseEntity<Object> addClientLoan(ClientLoanForm clientLoanForm) {
        Optional<ClientEntity> optionalClient = this.clientRepository.findByRut(clientLoanForm.getRut());

        if (optionalClient.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("No se encontró el cliente con el RUT especificado");
        }

        ClientEntity client = optionalClient.get();
        ClientLoanEntity clientLoan = createAndSaveClientLoan(clientLoanForm, client);
        List<DocumentEntity> documents = processDocuments(clientLoanForm.getDocuments(), clientLoan);

        updateClientWithLoan(client, clientLoan, documents);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Se ingresó correctamente el préstamo del cliente");
    }

    private ClientLoanEntity createAndSaveClientLoan(ClientLoanForm form, ClientEntity client) {
        ClientLoanEntity clientLoan = new ClientLoanEntity();
        clientLoan.setClient(client);
        setClientLoanFields(clientLoan, form);
        return this.clientLoanRepository.save(clientLoan);
    }

    private void setClientLoanFields(ClientLoanEntity clientLoan, ClientLoanForm form) {
        clientLoan.setLoanAmount(form.getLoanAmount());
        clientLoan.setLoanName(form.getLoanName());
        clientLoan.setInterest(form.getInterest());
        clientLoan.setYears(form.getYears());
        clientLoan.setMensualPay(form.getMensualPay());
        clientLoan.setRequirementsApproved(form.getRequirementsApproved());
        clientLoan.setFase(form.getFase());
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

    public List<ClientLoanEntity> getClientLoanByClient(Long id) {
        Optional<ClientEntity> optionalClient = clientRepository.findById(id);
        return optionalClient.map(clientLoanRepository::findByClient).orElse(null);
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

        List<DocumentSaveForm> documentForms = clientLoan.getDocuments()
                .stream()
                .map(documentService::setDocumentSaveForm)
                .collect(Collectors.toList());

        clientLoanGetForm.setDocuments(documentForms);
        clientLoanGetForm.setRequirementsApproved(clientLoan.getRequirementsApproved());

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
