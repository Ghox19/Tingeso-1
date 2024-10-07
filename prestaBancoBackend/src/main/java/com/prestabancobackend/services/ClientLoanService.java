package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<ClientLoanEntity> getClientLoanByClient(Long id) {
        Optional<ClientEntity> optionalClient = clientRepository.findById(id);
        return optionalClient.map(clientLoanRepository::findByClient).orElse(null);
    }

    public List<ClientLoanEntity> getAllClientLoan (){
        return clientLoanRepository.findAll();
    }

}
