package com.prestabancobackend.controller;

import com.prestabancobackend.form.*;
import com.prestabancobackend.getForms.ClientLoanGetForm;
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
    public ClientLoanGetForm getClientLoansById(@PathVariable Long id) {
        return this.clientLoanService.getClientLoanById(id);
    }

    @GetMapping
    public List<ClientLoanGetForm> getAllClientLoan(){
        return this.clientLoanService.getAllClientLoan();
    }

    @PostMapping("/calculator")
    public Integer getMonthlyPay(@RequestBody CalculatorForm calculatorForm) {
        return this.clientLoanService.calculateMensualPay(calculatorForm);
    }

    @PutMapping("/preApproved")
    public ResponseEntity<Object> updateClientLoanPreApproved(@RequestBody ClientLoanPreApprovedForm form) {
        return this.clientLoanService.updateClientLoanPreApproved(form);
    }

    @PutMapping("/final")
    public ResponseEntity<Object> updateClientLoanFinal(@RequestBody ClientLoanFinalApprovedForm form) {
        return this.clientLoanService.updateFinalApproved(form);
    }

    @PutMapping("/reject")
    public ResponseEntity<Object> updateReject(@RequestBody ClientLoanRejectForm form) {
        return this.clientLoanService.updateReject(form);
    }
}
