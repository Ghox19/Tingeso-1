package com.prestabancobackend.services;

import com.prestabancobackend.entities.*;
import com.prestabancobackend.form.*;
import com.prestabancobackend.getForms.ClientLoanGetForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientLoanServiceTest {

    @Mock
    private ClientLoanRepository clientLoanRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private DocumentService documentService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private ClientLoanService clientLoanService;

    private ClientLoanForm clientLoanForm;
    private ClientEntity clientEntity;
    private LoanEntity loanEntity;
    private DocumentForm documentForm;
    private DocumentEntity documentEntity;
    private ClientLoanEntity clientLoanEntity;

    @BeforeEach
    void setUp() {
        // Setup Client
        clientEntity = new ClientEntity();
        clientEntity.setRut(123456789);
        clientEntity.setMensualIncome(5000);
        clientEntity.setJobYears(2);
        clientEntity.setYears(30);
        clientEntity.setTotalDebt(1000);
        clientEntity.setLoans(new ArrayList<>());

        // Setup Loan
        loanEntity = new LoanEntity();
        loanEntity.setName("Hipotecario");
        loanEntity.setMinInterest(5.0f);
        loanEntity.setMaxInterest(15.0f);
        loanEntity.setMaxYears(30);
        loanEntity.setMaxAmount(80);
        loanEntity.setRequirements(Arrays.asList("Doc1", "Doc2"));

        // Setup DocumentForm
        documentForm = new DocumentForm();
        documentForm.setName("test.pdf");
        documentForm.setContent("test content");
        documentForm.setType("application/pdf");

        documentEntity = new DocumentEntity();
        documentEntity.setId(1L);
        documentEntity.setName("test.pdf");
        documentEntity.setContent("test content".getBytes());
        documentEntity.setType("application/pdf");
        documentEntity.setApproved(true);

        // Setup ClientLoanForm
        clientLoanForm = new ClientLoanForm();
        clientLoanForm.setRut(123456789);
        clientLoanForm.setLoanAmount(100000);
        clientLoanForm.setPropertyValue(200000);
        clientLoanForm.setLoanName("Hipotecario");
        clientLoanForm.setInterest(10.0F);
        clientLoanForm.setYears(20);
        clientLoanForm.setMensualPay(1000);
        clientLoanForm.setFase("Inicial");
        clientLoanForm.setDocuments(Arrays.asList(documentForm, documentForm));

        // Setup ClientLoanEntity
        clientLoanEntity = new ClientLoanEntity();
        clientLoanEntity.setId(1L);
        clientLoanEntity.setClient(clientEntity);
        clientLoanEntity.setLoanAmount(100000);
        clientLoanEntity.setLoanName("Hipotecario");
        clientLoanEntity.setInterest(10.0F);
        clientLoanEntity.setYears(20);
        clientLoanEntity.setMensualPay(1000);
        clientLoanEntity.setFase("Inicial");
    }

    @Test
    void addClientLoan_ShouldReturnSuccess_WhenValidInput() {
        // Arrange
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));
        when(loanService.getLoanByName("Hipotecario")).thenReturn(loanEntity);
        when(clientLoanRepository.save(any(ClientLoanEntity.class))).thenReturn(clientLoanEntity);
        when(documentService.saveDocument(any(DocumentForm.class))).thenReturn(new DocumentEntity());

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se ingresó correctamente el préstamo", response.getBody());
        verify(clientRepository).findByRut(123456789);
        verify(clientLoanRepository).save(any(ClientLoanEntity.class));
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenClientNotFound() {
        // Arrange
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No se encontró el cliente con el RUT especificado", response.getBody());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing() {
        // Arrange
        ClientLoanForm invalidForm = new ClientLoanForm();
        invalidForm.setRut(123456789); // Mantener RUT válido para pasar primera validación
        // Dejar otros campos requeridos como null
        invalidForm.setYears(null);
        invalidForm.setInterest(null);
        invalidForm.setLoanAmount(null);
        invalidForm.setLoanName("Hipotecario");
        invalidForm.setPropertyValue(200000);
        invalidForm.setMensualPay(1000);
        invalidForm.setFase("Inicial");
        invalidForm.setDocuments(Arrays.asList(documentForm));

        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(invalidForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Faltan datos requeridos", response.getBody());
        verify(clientRepository).findByRut(123456789);
        verify(loanService, never()).getLoanByName(any());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenMensualPayIsZero() {
        // Arrange
        clientLoanForm.setMensualPay(0);
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Falta calcular el valor de la cuota mensual", response.getBody());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenInterestOutOfRange() {
        // Arrange
        clientLoanForm.setInterest(20.0F);
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));
        when(loanService.getLoanByName("Hipotecario")).thenReturn(loanEntity);

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El Interes del prestamo esta fuera de los limites", response.getBody());
    }

    @Test
    void updateClientLoanPreApproved_ShouldReturnSuccess_WhenValidInput() {
        // Arrange
        ClientLoanPreApprovedForm form = new ClientLoanPreApprovedForm();
        form.setClientLoanId(1L);
        form.setDeduction(0.05);
        form.setFireInsurance(100);

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoanEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.updateClientLoanPreApproved(form);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se PreAprobo Correctamente el Prestamo", response.getBody());
        verify(clientLoanRepository).save(any(ClientLoanEntity.class));
    }

    @Test
    void updateFinalApproved_ShouldReturnSuccess_WhenDesembolso() {
        // Arrange
        ClientLoanFinalApprovedForm form = new ClientLoanFinalApprovedForm();
        form.setId(1L);
        form.setFase("Desembolso");

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoanEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.updateFinalApproved(form);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se Desembolso Correctamente el Prestamo", response.getBody());
        verify(clientLoanRepository).save(any(ClientLoanEntity.class));
    }

    @Test
    void calculateMensualPay_ShouldReturnCorrectAmount() {
        // Arrange
        CalculatorForm calculatorForm = new CalculatorForm();
        calculatorForm.setLoanAmount(100000);
        calculatorForm.setInterest(10.0F);
        calculatorForm.setYears(20);

        // Act
        Integer result = clientLoanService.calculateMensualPay(calculatorForm);

        // Assert
        assertNotNull(result);
        assertEquals(965, result); // Valor calculado para este caso específico
    }

    @Test
    void updateReject_ShouldReturnSuccess_WhenValidInput() {
        // Arrange
        ClientLoanRejectForm form = new ClientLoanRejectForm();
        form.setId(1L);
        form.setMessage("Motivo del rechazo");

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoanEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.updateReject(form);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se Rechazo Correctamente el Prestamo", response.getBody());

        verify(clientLoanRepository).save(argThat(loan ->
                loan.getFase().equals("Rechazado") &&
                        loan.getMessage().equals("Motivo del rechazo")
        ));
    }

    @Test
    void updateReject_ShouldReturnBadRequest_WhenClientLoanNotFound() {
        // Arrange
        ClientLoanRejectForm form = new ClientLoanRejectForm();
        form.setId(1L);
        form.setMessage("Motivo del rechazo");

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = clientLoanService.updateReject(form);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No se encontro la Solicitud", response.getBody());
        verify(clientLoanRepository, never()).save(any());
    }

    @Test
    void setClientLoanGetForm_ShouldMapAllFields() {
        // Arrange
        ClientLoanEntity clientLoan = new ClientLoanEntity();
        clientLoan.setId(1L);
        clientLoan.setInterest(10.0F);
        clientLoan.setLoanName("Hipotecario");
        clientLoan.setYears(20);
        clientLoan.setLoanAmount(100000);
        clientLoan.setMensualPay(965);
        clientLoan.setFase("Inicial");
        clientLoan.setClient(new ClientEntity());
        clientLoan.setSaving(new SavingEntity());
        clientLoan.setPropertyValue(200000);
        clientLoan.setCuotaIncome(35.0);
        clientLoan.setDebtCuota(45.0);
        clientLoan.setMessage("Mensaje de prueba");
        clientLoan.setLoanRatio(80.0f);
        clientLoan.setFireInsurance(100);
        clientLoan.setDeduction(0.05);
        clientLoan.setTotalCost(150000);
        clientLoan.setDocuments(Arrays.asList(new DocumentEntity()));

        DocumentSaveForm documentSaveForm = new DocumentSaveForm();
        when(documentService.setDocumentSaveForm(any(DocumentEntity.class))).thenReturn(documentSaveForm);

        // Act
        ClientLoanGetForm result = clientLoanService.setClientLoanGetForm(clientLoan);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10.0F, result.getInterest());
        assertEquals("Hipotecario", result.getLoanName());
        assertEquals(20, result.getYears());
        assertEquals(100000, result.getLoanAmount());
        assertEquals(965, result.getMensualPay());
        assertEquals("Inicial", result.getFase());
        assertNotNull(result.getClient());
        assertNotNull(result.getSavings());
        assertEquals(200000, result.getPropertyValue());
        assertEquals(35.0, result.getCuotaIncome());
        assertEquals(45.0, result.getDebtCuota());
        assertEquals("Mensaje de prueba", result.getMessage());
        assertEquals(80.0f, result.getLoanRatio());
        assertEquals(100, result.getFireInsurance());
        assertEquals(0.05, result.getDeduction());
        assertEquals(150000, result.getTotalCost());
        assertNotNull(result.getDocuments());
        assertEquals(1, result.getDocuments().size());

        verify(documentService).setDocumentSaveForm(any(DocumentEntity.class));
    }

    @Test
    void updateFinalApproved_ShouldUpdateAndReturn_WhenNotDesembolso() {
        // Arrange
        ClientLoanFinalApprovedForm form = new ClientLoanFinalApprovedForm();
        form.setId(1L);
        form.setFase("Rechazado"); // Cualquier fase diferente a "Desembolso"

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoanEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.updateFinalApproved(form);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se Desembolso Correctamente el Prestamo", response.getBody());

        verify(clientLoanRepository).save(argThat(loan ->
                loan.getFase().equals("Rechazado") &&
                        loan.getMessage().equals("Rechazado porque el cliente no acepto las condiciones")
        ));
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenYearsExceedLimit() {
        // Arrange
        clientLoanForm.setYears(35); // Excede el máximo de años
        LoanEntity loanType = new LoanEntity();
        loanType.setMaxYears(30);
        loanType.setMinInterest(5.0f);
        loanType.setMaxInterest(15.0f);

        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));
        when(loanService.getLoanByName(clientLoanForm.getLoanName())).thenReturn(loanType);

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Los años del prestamo esta fuera de los limites", response.getBody());
        verify(clientLoanRepository, never()).save(any());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenLoanRatioExceedsLimit() {
        // Arrange
        clientLoanForm.setLoanAmount(90000); // 90% del valor de la propiedad
        clientLoanForm.setPropertyValue(100000);

        LoanEntity loanType = new LoanEntity();
        loanType.setMaxYears(30);
        loanType.setMinInterest(5.0f);
        loanType.setMaxInterest(15.0f);
        loanType.setMaxAmount(80); // Máximo 80% del valor de la propiedad

        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));
        when(loanService.getLoanByName(clientLoanForm.getLoanName())).thenReturn(loanType);

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("La cantidad del prestamo esta fuera de los limites", response.getBody());
        verify(clientLoanRepository, never()).save(any());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenInsufficientDocuments() {
        // Arrange
        List<DocumentForm> documents = Arrays.asList(new DocumentForm()); // Solo un documento
        clientLoanForm.setDocuments(documents);

        LoanEntity loanType = new LoanEntity();
        loanType.setMaxYears(30);
        loanType.setMinInterest(5.0f);
        loanType.setMaxInterest(15.0f);
        loanType.setMaxAmount(80);
        loanType.setRequirements(Arrays.asList("Doc1", "Doc2", "Doc3")); // Requiere 3 documentos

        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));
        when(loanService.getLoanByName(clientLoanForm.getLoanName())).thenReturn(loanType);

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Faltan Documentos", response.getBody());
        verify(clientLoanRepository, never()).save(any());
    }

    @Test
    void getClientLoanById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(clientLoanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clientLoanService.getClientLoanById(1L)
        );

        assertEquals("Client Loan not found with id: 1", exception.getMessage());
        verify(clientLoanRepository).findById(1L);
    }

    @Test
    void updateClientLoanPreApproved_ShouldReturnBadRequest_WhenLoanNotFound() {
        // Arrange
        ClientLoanPreApprovedForm form = new ClientLoanPreApprovedForm();
        form.setClientLoanId(1L);
        form.setDeduction(0.05);
        form.setFireInsurance(100);

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = clientLoanService.updateClientLoanPreApproved(form);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No se encontro la Solicitud", response.getBody());
        verify(clientLoanRepository).findById(1L);
        verify(clientLoanRepository, never()).save(any(ClientLoanEntity.class));
    }

    @Test
    void updateFinalApproved_ShouldReturnBadRequest_WhenLoanNotFound() {
        // Arrange
        ClientLoanFinalApprovedForm form = new ClientLoanFinalApprovedForm();
        form.setId(1L);
        form.setFase("Desembolso");

        when(clientLoanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = clientLoanService.updateFinalApproved(form);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No se encontro la Solicitud", response.getBody());
        verify(clientLoanRepository).findById(1L);
        verify(clientLoanRepository, never()).save(any(ClientLoanEntity.class));
    }

    @Test
    void getAllClientLoan_ShouldReturnEmptyList_WhenNoLoansExist() {
        // Arrange
        when(clientLoanRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<ClientLoanGetForm> result = clientLoanService.getAllClientLoan();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clientLoanRepository).findAll();
    }
}
