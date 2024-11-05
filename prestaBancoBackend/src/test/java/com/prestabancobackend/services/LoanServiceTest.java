package com.prestabancobackend.services;

import com.prestabancobackend.entities.LoanEntity;
import com.prestabancobackend.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private LoanEntity loan1;
    private LoanEntity loan2;

    private List<LoanEntity> loans;

    @BeforeEach
    void setUp() {
        loan1 = new LoanEntity();
        loan1.setId(1L);
        loan1.setName("Personal Loan");
        loan1.setDescription("Short-term personal loan for small expenses");
        loan1.setMaxYears(5);
        loan1.setMinInterest(8.5f);
        loan1.setMaxInterest(12.0f);
        loan1.setMaxAmount(5000);
        loan1.setRequirements(Arrays.asList("Valid ID", "Proof of Income", "Bank Statements"));

        loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setName("Home Improvement Loan");
        loan2.setDescription("Loan for home repairs and renovations");
        loan2.setMaxYears(10);
        loan2.setMinInterest(6.5f);
        loan2.setMaxInterest(9.0f);
        loan2.setMaxAmount(25000);
        loan2.setRequirements(Arrays.asList("Property Documents", "Income Proof", "Credit Score > 650"));

        loans = Arrays.asList(loan1, loan2);
    }

    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        when(loanRepository.findAll()).thenReturn(loans);

        List<LoanEntity> actualLoans = loanService.getAllLoans();

        assertEquals(loans, actualLoans);
        verify(loanRepository).findAll();
    }
}
