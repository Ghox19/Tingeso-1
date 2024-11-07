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
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private LoanEntity loan1;
    private LoanEntity loan2;

    @BeforeEach
    void setUp() {
        // Setup Loan 1
        loan1 = new LoanEntity();
        loan1.setId(1L);
        loan1.setName("Personal Loan");
        loan1.setDescription("Short-term personal loan");
        loan1.setMaxYears(5);
        loan1.setMinInterest(8.5f);
        loan1.setMaxInterest(12.0f);
        loan1.setMaxAmount(5000);
        loan1.setRequirements(Arrays.asList("Valid ID", "Proof of Income"));

        // Setup Loan 2
        loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setName("Home Loan");
        loan2.setDescription("Long-term home loan");
        loan2.setMaxYears(30);
        loan2.setMinInterest(6.5f);
        loan2.setMaxInterest(9.0f);
        loan2.setMaxAmount(300000);
        loan2.setRequirements(Arrays.asList("Property Documents", "Income Proof"));
    }

    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        // Arrange
        List<LoanEntity> expectedLoans = Arrays.asList(loan1, loan2);
        when(loanRepository.findAll()).thenReturn(expectedLoans);

        // Act
        List<LoanEntity> actualLoans = loanService.getAllLoans();

        // Assert
        assertEquals(expectedLoans.size(), actualLoans.size());
        assertEquals(expectedLoans, actualLoans);
        verify(loanRepository).findAll();
    }

    @Test
    void getLoanByName_ShouldReturnLoan_WhenExists() {
        // Arrange
        when(loanRepository.findByName("Personal Loan")).thenReturn(loan1);

        // Act
        LoanEntity result = loanService.getLoanByName("Personal Loan");

        // Assert
        assertNotNull(result);
        assertEquals("Personal Loan", result.getName());
        assertEquals(loan1.getDescription(), result.getDescription());
        assertEquals(loan1.getMaxYears(), result.getMaxYears());
        verify(loanRepository).findByName("Personal Loan");
    }

    @Test
    void getLoanByName_ShouldReturnNull_WhenNotExists() {
        // Arrange
        when(loanRepository.findByName("Non Existent Loan")).thenReturn(null);

        // Act
        LoanEntity result = loanService.getLoanByName("Non Existent Loan");

        // Assert
        assertNull(result);
        verify(loanRepository).findByName("Non Existent Loan");
    }
}