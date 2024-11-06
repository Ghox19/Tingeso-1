package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.SavingEntity;
import com.prestabancobackend.form.SavingForm;
import com.prestabancobackend.getForms.ClientLoanGetForm;
import com.prestabancobackend.getForms.SavingGetForm;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.SavingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingServiceTest {

    @Mock
    private ClientLoanRepository clientLoanRepository;

    @Mock
    private SavingRepository savingRepository;

    @Mock
    private ClientLoanService clientLoanService;

    @InjectMocks
    private SavingService savingService;

    private SavingForm savingForm;

    private SavingForm savingForm2;
    private ClientLoanEntity clientLoan;
    private ClientLoanEntity clientLoan2;
    private SavingEntity savingEntity;
    private SavingEntity savingEntity2;
    private ClientEntity client;

    @BeforeEach
    void setUp() {
        // Setup Client
        client = new ClientEntity();
        client.setMensualIncome(5000);

        // Setup ClientLoan
        clientLoan = new ClientLoanEntity();
        clientLoan.setId(1L);
        clientLoan.setLoanAmount(10000);
        clientLoan.setClient(client);

        clientLoan2 = new ClientLoanEntity();
        clientLoan2.setId(2L);
        clientLoan2.setLoanAmount(10000);
        clientLoan2.setClient(client);

        // Setup SavingForm
        savingForm = new SavingForm();
        savingForm.setClientLoanId(1L);
        savingForm.setActualBalance(2000);
        savingForm.setYears(3);

        // Setup balances and withdraws for 12 months
        List<Integer> balances = Arrays.asList(1000, 1200, 1400, 1600, 1800, 2000,
                2200, 2400, 2600, 2800, 3000, 3200);
        List<Integer> withdraws = Arrays.asList(100, 150, 200, 250, 300, 350,
                400, 450, 500, 550, 600, 650);
        List<Integer> deposits = Arrays.asList(300, 350, 400, 450, 500, 550,
                600, 650, 700, 750, 800, 850);

        savingForm.setBalances(balances);
        savingForm.setWithdraw(withdraws);
        savingForm.setDeposit(deposits);

        savingForm2 = new SavingForm();
        savingForm2.setClientLoanId(2L);
        savingForm2.setActualBalance(2000);
        savingForm2.setYears(1);

        List<Integer> balances2 = Arrays.asList(0, 1200, 1400, 1600, 1800, 2000,
                2200, 2400, 2600, 2800, 3000, 3200);
        List<Integer> withdraws2 = Arrays.asList(100, 150, 200, 250, 300, 350,
                100, 200, 150, 100, 100, 200);
        List<Integer> deposits2 = Arrays.asList(300, 0, 0, 0, 0, 550,
                600, 650, 700, 750, 800, 850);

        savingForm2.setBalances(balances2);
        savingForm2.setWithdraw(withdraws2);
        savingForm2.setDeposit(deposits2);

        // Setup SavingEntity
        savingEntity = new SavingEntity();
        savingEntity.setId(1L);
        savingEntity.setActualBalance(2000);
        savingEntity.setYears(3);
        savingEntity.setBalances(balances);
        savingEntity.setWithdraw(withdraws);
        savingEntity.setDeposit(deposits);
        savingEntity.setClientLoan(clientLoan);

        savingEntity2 = new SavingEntity();
        savingEntity2.setId(2L);
        savingEntity2.setActualBalance(2000);
        savingEntity2.setYears(1);
        savingEntity2.setBalances(balances2);
        savingEntity2.setWithdraw(withdraws2);
        savingEntity2.setDeposit(deposits2);
        savingEntity2.setClientLoan(clientLoan2);
    }

    @Test
    void getSavingById_ShouldReturnSaving_WhenExists() {
        // Arrange
        when(savingRepository.findById(1L)).thenReturn(Optional.of(savingEntity));
        when(clientLoanService.setClientLoanGetForm(any())).thenReturn(new ClientLoanGetForm());

        // Act
        SavingGetForm result = savingService.getSavingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(savingEntity.getId(), result.getId());
        assertEquals(savingEntity.getActualBalance(), result.getActualBalance());
        verify(savingRepository).findById(1L);
    }

    @Test
    void getSavingById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(savingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                savingService.getSavingById(1L)
        );
        verify(savingRepository).findById(1L);
    }

    @Test
    void verifyConditions_ShouldReturnAppropriateReasons() {
        // Arrange
        savingEntity.setActualBalance(500); // Less than 10% of loan amount

        // Act
        List<String> reasons = savingService.verifyConditions(savingEntity);

        // Assert
        assertTrue(reasons.contains("R71: No cumple con el saldo mínimo requerido (10% del préstamo)."));
    }

    @Test
    void verificarHistorialAhorro_ShouldReturnTrue_WhenValidHistory() {
        // Arrange
        Integer[] balances = {1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 3200};
        Integer[] withdraws = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650};

        // Act
        boolean result = savingService.verificarHistorialAhorro(balances, withdraws);

        // Assert
        assertTrue(result);
    }

    @Test
    void verificarDepositosPeriodicos_ShouldReturnTrue_WhenValidDeposits() {
        // Arrange
        List<Integer> deposits = Arrays.asList(300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850);
        Integer mensualIncome = 5000;

        // Act
        boolean result = savingService.verificarDepositosPeriodicos(deposits, mensualIncome);

        // Assert
        assertTrue(result);
    }


}