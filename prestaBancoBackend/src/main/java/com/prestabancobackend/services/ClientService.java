package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.form.*;
import com.prestabancobackend.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    private final DocumentService documentService;

    @Autowired
    public ClientService(ClientRepository clientRepository, DocumentService documentService) {
        this.clientRepository = clientRepository;
        this.documentService = documentService;
    }

    //Function to add a user
    public ResponseEntity<Object> addClient(RegisterForm clientForm) {
        if (this.clientRepository.findByRut(clientForm.getRut()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Ya existe un cliente con el RUT especificado");
        }

        ClientEntity newClient = createClientFromForm(clientForm);
        newClient = this.clientRepository.save(newClient);

        List<DocumentEntity> documents = processDocuments(clientForm.getDocuments(), newClient);
        newClient.setDocuments(documents);

        // Un solo save final que actualiza la entidad con todos sus documentos
        this.clientRepository.save(newClient);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Se ingresó correctamente el Usuario");
    }

    private ClientEntity createClientFromForm(RegisterForm form) {
        ClientEntity client = new ClientEntity();
        client.setName(form.getName());
        client.setLastName(form.getLastName());
        client.setRut(form.getRut());
        client.setEmail(form.getEmail());
        client.setYears(form.getYears());
        client.setContact(form.getContact());
        client.setJobType(form.getJobType());
        client.setMensualIncome(form.getMensualIncome());
        client.setJobYears(form.getJobYears());
        client.setTotalDebt(form.getTotalDebt());
        client.setLoans(new ArrayList<>());
        return client;
    }

    private List<DocumentEntity> processDocuments(List<DocumentForm> documentForms, ClientEntity client) {
        return documentForms.stream()
                .map(docForm -> {
                    DocumentEntity document = this.documentService.saveDocument(docForm);
                    document.setClient(client);
                    return document;
                })
                .collect(Collectors.toList());
    }

    //Function to get all users
    public List<ClientGetForm> getAllClients() {
        List<ClientEntity> clients = this.clientRepository.findAll();

        return clients.stream()
                .map(this::setClientGetForm)
                .collect(Collectors.toList());
    }

    public ClientGetForm setClientGetForm(ClientEntity client){
        ClientGetForm clientGetForm = new ClientGetForm();
        clientGetForm.setId(client.getId());
        clientGetForm.setName(client.getName());
        clientGetForm.setLastName(client.getLastName());
        clientGetForm.setRut(client.getRut());
        clientGetForm.setEmail(client.getEmail());
        clientGetForm.setYears(client.getYears());
        clientGetForm.setContact(client.getContact());
        clientGetForm.setJobType(client.getJobType());
        clientGetForm.setMensualIncome(client.getMensualIncome());
        clientGetForm.setJobYears(client.getJobYears());
        clientGetForm.setTotalDebt(client.getTotalDebt());

        List<DocumentSaveForm> documentForms = client.getDocuments()
                .stream()
                .map(documentService::setDocumentSaveForm)
                .collect(Collectors.toList());

        clientGetForm.setDocuments(documentForms);

        clientGetForm.setLoans(client.getLoans());
        return clientGetForm;
    }

    //Function to delete a user
    public ResponseEntity<Object> deleteClient(long id) {
        Optional<ClientEntity> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()){
            this.clientRepository.deleteById(id);
            return new ResponseEntity<>("Se elimino correctamente el Usuario", HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ClientGetForm getClientById(Long id) {
        Optional<ClientEntity> client = clientRepository.findById(id);

        if (client.isPresent()) {
            return setClientGetForm(client.get());
        } else {
            throw new EntityNotFoundException("Client Loan not found with id: " + id);
        }
    }

    public List<DocumentSaveForm> getClientDocuments(Long id){
        ClientGetForm client = getClientById(id);

        if (client != null){
            return client.getDocuments();
        }
        else {
            return null;
        }
    }

    public ClientInfoRequiredForm getClientRequiredInfoByRut(Integer rut){
        Optional<ClientEntity> optionalClient = clientRepository.findByRut(rut);

        if (optionalClient.isPresent()){
            ClientInfoRequiredForm clientInfoRequiredForm = new ClientInfoRequiredForm();
            clientInfoRequiredForm.setYears(optionalClient.get().getYears());
            clientInfoRequiredForm.setJobYears(optionalClient.get().getJobYears());
            clientInfoRequiredForm.setMensualIncome(optionalClient.get().getMensualIncome());
            clientInfoRequiredForm.setJobType(optionalClient.get().getJobType());
            clientInfoRequiredForm.setTotalDebt(optionalClient.get().getTotalDebt());
            return clientInfoRequiredForm;
        }

        return null;
    }
}
