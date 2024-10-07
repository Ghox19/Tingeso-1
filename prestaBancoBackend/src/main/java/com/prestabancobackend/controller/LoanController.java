package com.prestabancobackend.controller;

import com.prestabancobackend.entities.LoanEntity;
import com.prestabancobackend.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/loan")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/{name}")
    public LoanEntity getLoanByName(@PathVariable String name) {  return this.loanService.getLoanByName(name);}
}
