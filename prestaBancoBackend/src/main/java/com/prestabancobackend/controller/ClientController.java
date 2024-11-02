package com.prestabancobackend.controller;

import com.prestabancobackend.getForms.ClientGetForm;
import com.prestabancobackend.form.ClientInfoRequiredForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
import com.prestabancobackend.form.RegisterForm;
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
    public ResponseEntity<Object> addClient(@RequestBody RegisterForm client) {
        return this.clientService.addClient(client);
    }

    //Function to get all users
    @GetMapping
    public List<ClientGetForm> getAllClients() {
        return this.clientService.getAllClients();
    }

    @GetMapping("/documents/{id}")
    public List<DocumentSaveForm> getClientDocuments(@PathVariable Long id){return this.clientService.getClientDocuments(id);}

    @GetMapping("/{id}")
    public ClientGetForm getClientById(@PathVariable Long id) {  return this.clientService.getClientById(id);}

    @GetMapping("/rinfo/{rut}")
    public ClientInfoRequiredForm getClienRequiredInfoByRut(@PathVariable Integer rut) {
        return this.clientService.getClientRequiredInfoByRut(rut);
    }

    //Function to delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClient(@PathVariable Long id) { return this.clientService.deleteClient(id);}
}
