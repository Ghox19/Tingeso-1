package com.prestabancobackend.controller;

import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.form.CalculatorForm;
import com.prestabancobackend.form.ClientLoanForm;
import com.prestabancobackend.services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/clientLoan")
public class ClientLoanController {
    private final ClientLoanService clientLoanService;

    @Autowired
    public ClientLoanController(ClientLoanService clientLoanService) {
        this.clientLoanService = clientLoanService;
    }

    @PostMapping
    public ResponseEntity<Object> addClientLoan(@RequestBody ClientLoanForm clientLoanForm) {
        return this.clientLoanService.addClientLoan(clientLoanForm);
    }

    @GetMapping("/{id}")
    public ClientLoanEntity getClientLoansById(@PathVariable Long id) {
        return this.clientLoanService.getClientLoanById(id);
    }

    @GetMapping("/client/{id}")
    public List<ClientLoanEntity> getClientLoansByClient(@PathVariable Long id) {
        return this.clientLoanService.getClientLoanByClient(id);
    }

    @GetMapping
    public List<ClientLoanEntity> getAllClientLoan(){
        return this.clientLoanService.getAllClientLoan();
    }

    @PostMapping("/calculator")
    public Integer getMonthlyPay(@RequestBody CalculatorForm calculatorForm) {
        return this.clientLoanService.calculateMensualPay(calculatorForm);
    }
}
