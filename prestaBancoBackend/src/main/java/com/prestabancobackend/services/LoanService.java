package com.prestabancobackend.services;

import com.prestabancobackend.entities.LoanEntity;
import com.prestabancobackend.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public List<LoanEntity> getAllLoans(){
        return this.loanRepository.findAll();
    }

    public LoanEntity getLoanByName(String name){return this.loanRepository.findByName(name);}
}
