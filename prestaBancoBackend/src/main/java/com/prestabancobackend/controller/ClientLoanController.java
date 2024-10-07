package com.prestabancobackend.controller;

import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.LoanEntity;
import com.prestabancobackend.services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/clientLoan")
public class ClientLoanController {
    private final ClientLoanService clientLoanService;

    @Autowired
    public ClientLoanController(ClientLoanService clientLoanService) {
        this.clientLoanService = clientLoanService;
    }

    @GetMapping("/{id}")
    public List<ClientLoanEntity> getClientLoansByClient(@PathVariable Long id) {
        return this.clientLoanService.getClientLoanByClient(id);
    }

    @GetMapping
    public List<ClientLoanEntity> getAllClientLoan(){
        return this.clientLoanService.getAllClientLoan();
    }
}
