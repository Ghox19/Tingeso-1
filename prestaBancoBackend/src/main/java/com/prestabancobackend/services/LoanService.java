package com.prestabancobackend.services;

import com.prestabancobackend.entities.LoanEntity;
import com.prestabancobackend.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LoanEntity getLoanByName(String name){
        Optional<LoanEntity> optionalLoan = loanRepository.findByName(name);
        return optionalLoan.orElse(null);
    }
}
