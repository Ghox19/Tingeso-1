package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.form.ClientInfoRequiredForm;
import com.prestabancobackend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    //Function to add a user
    public ResponseEntity<Object> addClient(ClientEntity client) {
        Optional<ClientEntity> optionalClient = this.clientRepository.findByRut(client.getRut());

        if (optionalClient.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.clientRepository.save(client);

        return new ResponseEntity<>("Se ingreso correctamente el Usuario", HttpStatus.CREATED);
    }

    //Function to get all users
    public List<ClientEntity> getAllClients() {
        return this.clientRepository.findAll();
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

    public ClientEntity getClientById(long id) {
        Optional<ClientEntity> optionalClient = clientRepository.findById(id);
        return optionalClient.orElse(null);
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
