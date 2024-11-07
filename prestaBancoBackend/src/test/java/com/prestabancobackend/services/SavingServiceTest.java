package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.SavingEntity;
import com.prestabancobackend.form.SavingForm;
import com.prestabancobackend.form.SavingResultForm;
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
    private SavingRepository savingRepository;

    @Mock
    private ClientLoanRepository clientLoanRepository;

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
    private SavingResultForm savingResultForm;

    @BeforeEach
    void setUp() {
        // Setup Client
        client = new ClientEntity();
        client.setMensualIncome(5000);

        // Setup ClientLoan - Corregir la asignación directa
        clientLoan = new ClientLoanEntity();
        clientLoan.setId(1L);
        clientLoan.setLoanAmount(10000);
        clientLoan.setClient(client);

        clientLoan2 = new ClientLoanEntity();
        clientLoan2.setId(2L);
        clientLoan2.setLoanAmount(10000);
        clientLoan2.setClient(client);

        // Setup SavingForm - Corregir la asignación directa
        savingForm = new SavingForm();
        savingForm.setClientLoanId(1L);
        savingForm.setActualBalance(2000);
        savingForm.setYears(3);

        List<Integer> balances = Arrays.asList(1000, 1200, 1400, 1600, 1800, 2000,
                2200, 2400, 2600, 2800, 3000, 3200);
        List<Integer> withdraws = Arrays.asList(100, 150, 200, 250, 300, 350,
                400, 450, 500, 550, 600, 650);
        List<Integer> deposits = Arrays.asList(300, 350, 400, 450, 500, 550,
                600, 650, 700, 750, 800, 850);

        savingForm.setBalances(balances);
        savingForm.setWithdraw(withdraws);
        savingForm.setDeposit(deposits);

        // Setup SavingEntity - Corregir la asignación directa
        savingEntity = new SavingEntity();
        savingEntity.setId(1L);
        savingEntity.setActualBalance(2000);
        savingEntity.setYears(3);
        savingEntity.setBalances(balances);
        savingEntity.setWithdraw(withdraws);
        savingEntity.setDeposit(deposits);
        savingEntity.setClientLoan(clientLoan);

        savingResultForm = new SavingResultForm();
        savingResultForm.setId(1L);
        savingResultForm.setResult("Aprobado");
    }
    @Test
    void addSaving_ShouldReturnSavingId_WhenValidInput() {
        // Arrange
        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoan));
        // Es importante que el save retorne el savingEntity con ID
        when(savingRepository.save(any(SavingEntity.class))).thenAnswer(invocation -> {
            SavingEntity savedEntity = invocation.getArgument(0);
            savedEntity.setId(1L); // Asignar ID al entity que se está guardando
            return savedEntity;
        });

        // Act
        Long result = savingService.addSaving(savingForm);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);
        verify(clientLoanRepository).findById(1L);
        verify(savingRepository).save(any(SavingEntity.class));
        verify(clientLoanRepository).save(any(ClientLoanEntity.class));
    }

    @Test
    void addSaving_ShouldReturnNull_WhenClientLoanNotFound() {
        // Arrange
        when(clientLoanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Long result = savingService.addSaving(savingForm);

        // Assert
        assertNull(result);
        verify(clientLoanRepository).findById(1L);
        verify(savingRepository, never()).save(any(SavingEntity.class));
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
    void updateStateSaving_ShouldApprove_WhenValidApprovalRequest() {
        // Arrange
        when(savingRepository.findById(1L)).thenReturn(Optional.of(savingEntity));

        // Act
        ResponseEntity<Object> response = savingService.updateStateSaving(savingResultForm);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se Aprobo correctamente la cuenta", response.getBody());
        verify(savingRepository).save(argThat(saving ->
                saving.getResult().equals("Aprobado")
        ));
    }

    @Test
    void updateStateSaving_ShouldReject_WhenRejectionRequest() {
        // Arrange
        savingResultForm.setResult("Rechazado");
        when(savingRepository.findById(1L)).thenReturn(Optional.of(savingEntity));

        // Act
        ResponseEntity<Object> response = savingService.updateStateSaving(savingResultForm);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se Rechazo correctamente la cuenta", response.getBody());
        verify(savingRepository).save(argThat(saving ->
                saving.getResult().equals("Rechazado")
        ));
        verify(clientLoanRepository).save(argThat(loan ->
                loan.getFase().equals("Rechazado")
        ));
    }

    @Test
    void updateStateSaving_ShouldReturnBadRequest_WhenSavingNotFound() {
        // Arrange
        when(savingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = savingService.updateStateSaving(savingResultForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No se encontro la Cuenta de Ahorros", response.getBody());
        verify(savingRepository, never()).save(any());
    }

    @Test
    void verifyConditions_ShouldReturnAllReasons_WhenAllConditionsFail() {
        // Arrange
        savingEntity.setActualBalance(500); // Less than 10% of loan amount
        savingEntity.setYears(1); // Will trigger additional validation

        // Act
        List<String> reasons = savingService.verifyConditions(savingEntity);

        // Assert
        assertTrue(reasons.contains("R71: No cumple con el saldo mínimo requerido (10% del préstamo)."));
        assertTrue(reasons.contains("R74: No cumple con la relación saldo/antigüedad requerida."));
    }

    @Test
    void verificarHistorialAhorro_ShouldReturnFalse_WhenNegativeBalance() {
        // Arrange
        Integer[] balances = {-1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 3200};
        Integer[] withdraws = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650};

        // Act
        boolean result = savingService.verificarHistorialAhorro(balances, withdraws);

        // Assert
        assertFalse(result);
    }

    @Test
    void addSaving_ShouldSetRejectedStatus_WhenMoreThanThreeReasons() {
        // Arrange
        // Configurar savingForm para que falle múltiples validaciones
        savingForm.setActualBalance(500); // Para fallar R71 (saldo mínimo)
        savingForm.setYears(1); // Para fallar R74 (relación saldo/antigüedad)

        // Configurar depósitos irregulares para fallar R73
        List<Integer> lowDeposits = Arrays.asList(100, 0, 0, 0, 0, 100, 100, 100, 100, 100, 100, 100);
        savingForm.setDeposit(lowDeposits);

        // Configurar retiros altos para fallar R75
        List<Integer> highWithdraws = Arrays.asList(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000);
        savingForm.setWithdraw(highWithdraws);

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoan));
        when(savingRepository.save(any(SavingEntity.class))).thenAnswer(invocation -> {
            SavingEntity savedEntity = invocation.getArgument(0);
            savedEntity.setId(1L);
            return savedEntity;
        });

        // Act
        Long result = savingService.addSaving(savingForm);

        // Assert
        assertNotNull(result);
        verify(savingRepository).save(argThat(saving -> {
            boolean hasCorrectResult = saving.getResult().equals("Rechazado");
            boolean hasEnoughReasons = saving.getReasons().size() > 3;
            return hasCorrectResult && hasEnoughReasons;
        }));

        verify(clientLoanRepository).save(argThat(loan ->
                loan.getFase().equals("Rechazado") &&
                        loan.getMessage().equals("El Prestamo fue Rechazado por no cumplir correctamente con la Cuenta de Ahorros")
        ));
    }
}