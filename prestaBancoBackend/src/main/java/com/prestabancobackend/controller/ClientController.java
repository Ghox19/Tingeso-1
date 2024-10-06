package com.prestabancobackend.controller;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    //Function to add a user
    @PostMapping
    public ResponseEntity<Object> addClient(@RequestBody ClientEntity client) {
        return this.clientService.addClient(client);
    }

    //Function to get all users
    @GetMapping
    public List<ClientEntity> getAllClients() {
        return this.clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ClientEntity getClientById(@PathVariable Long id) {  return this.clientService.getClientById(id);}

    //Function to delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClient(@PathVariable Long id) { return this.clientService.deleteClient(id);}
}
