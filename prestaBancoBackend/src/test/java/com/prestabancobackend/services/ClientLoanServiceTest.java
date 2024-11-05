package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.form.CalculatorForm;
import com.prestabancobackend.form.ClientLoanForm;
import com.prestabancobackend.form.DocumentForm;
import com.prestabancobackend.getForms.ClientLoanGetForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.ClientRepository;
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

    @InjectMocks
    private ClientLoanService clientLoanService;

    private ClientLoanForm clientLoanForm;
    private ClientLoanForm invalidForm;
    private ClientEntity clientEntity;
    private ClientEntity clientEntity2;
    private ClientLoanEntity clientLoanEntity;
    private DocumentForm documentForm;
    private DocumentEntity documentEntity;

    @BeforeEach
    void setUp() {
        // Setup DocumentForm
        documentForm = new DocumentForm();
        documentForm.setName("test.pdf");
        documentForm.setContent("test content");
        documentForm.setType("application/pdf");
        documentForm.setApproved(true);

        // Setup DocumentEntity
        documentEntity = new DocumentEntity();
        documentEntity.setId(1L);
        documentEntity.setName("test.pdf");
        documentEntity.setContent("test content".getBytes());
        documentEntity.setType("application/pdf");
        documentEntity.setApproved(true);

        // Setup ClientEntity
        clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        clientEntity.setRut(123456789);
        clientEntity.setMensualIncome(5000);
        clientEntity.setJobYears(2);
        clientEntity.setYears(30);
        clientEntity.setTotalDebt(1000);
        clientEntity.setLoans(new ArrayList<>());

        // Setup ClientLoanForm
        clientLoanForm = new ClientLoanForm();
        clientLoanForm.setRut(123456789);
        clientLoanForm.setLoanAmount(10000);
        clientLoanForm.setLoanName("Personal Loan");
        clientLoanForm.setInterest(4.5F);
        clientLoanForm.setYears(5);
        clientLoanForm.setMensualPay(500);
        clientLoanForm.setFase("Inicial");
        clientLoanForm.setDocuments(Arrays.asList(documentForm));

        // Arrange
        invalidForm = new ClientLoanForm();
        // Establecer algunos valores pero dejar otros null
        invalidForm.setRut(123456788);
        invalidForm.setMensualPay(500);
        invalidForm.setFase("Inicial");
        // Dejamos estos null
        invalidForm.setYears(null);
        invalidForm.setInterest(null);
        invalidForm.setLoanAmount(null);

        clientEntity2 = new ClientEntity();
        clientEntity2.setRut(123456788);
        clientEntity2.setMensualIncome(5000);
        clientEntity2.setJobYears(2);

        // Setup ClientLoanEntity
        clientLoanEntity = new ClientLoanEntity();
        clientLoanEntity.setId(1L);
        clientLoanEntity.setClient(clientEntity);
        clientLoanEntity.setLoanAmount(10000);
        clientLoanEntity.setLoanName("Personal Loan");
        clientLoanEntity.setInterest(4.5F);
        clientLoanEntity.setYears(5);
        clientLoanEntity.setMensualPay(500);
        clientLoanEntity.setFase("Inicial");
        clientLoanEntity.setDocuments(Arrays.asList(documentEntity));
    }

    @Test
    void addClientLoan_ShouldReturnSuccess_WhenValidInput() {
        // Arrange
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));
        when(clientLoanRepository.save(any(ClientLoanEntity.class))).thenReturn(clientLoanEntity);
        when(documentService.saveDocument(any(DocumentForm.class))).thenReturn(documentEntity);

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se ingresó correctamente el préstamo del cliente", response.getBody());
        verify(clientRepository).findByRut(123456789);
        verify(clientLoanRepository).save(any(ClientLoanEntity.class));
        verify(clientRepository).save(clientEntity);
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
        verify(clientRepository).findByRut(123456789);
        verify(clientLoanRepository, never()).save(any(ClientLoanEntity.class));
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenMensualPayExceedsLimit() {
        // Arrange
        clientLoanForm.setMensualPay(2000); // 40% of mensual income
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("La cuota mensual excede el 35% del sueldo", response.getBody());
    }

    @Test
    void getClientLoanByClient_ShouldReturnLoans_WhenClientExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(clientLoanRepository.findByClient(clientEntity)).thenReturn(Arrays.asList(clientLoanEntity));

        // Act
        List<ClientLoanEntity> result = clientLoanService.getClientLoanByClient(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(clientRepository).findById(1L);
        verify(clientLoanRepository).findByClient(clientEntity);
    }

    @Test
    void getClientLoanById_ShouldReturnLoan_WhenExists() {
        // Arrange
        when(clientLoanRepository.findById(1L)).thenReturn(Optional.of(clientLoanEntity));
        when(documentService.setDocumentSaveForm(any(DocumentEntity.class)))
                .thenReturn(new DocumentSaveForm());

        // Act
        ClientLoanGetForm result = clientLoanService.getClientLoanById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(clientLoanEntity.getId(), result.getId());
        assertEquals(clientLoanEntity.getLoanAmount(), result.getLoanAmount());
        verify(clientLoanRepository).findById(1L);
    }

    @Test
    void getAllClientLoan_ShouldReturnAllLoans() {
        // Arrange
        when(clientLoanRepository.findAll()).thenReturn(Collections.singletonList(clientLoanEntity));
        when(documentService.setDocumentSaveForm(any(DocumentEntity.class)))
                .thenReturn(new DocumentSaveForm());

        // Act
        List<ClientLoanGetForm> result = clientLoanService.getAllClientLoan();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(clientLoanRepository).findAll();
    }

    @Test
    void calculateMensualPay_ShouldReturnCorrectAmount() {
        // Arrange
        CalculatorForm calculatorForm = new CalculatorForm();
        calculatorForm.setLoanAmount(10000);
        calculatorForm.setInterest(4.5F);
        calculatorForm.setYears(5);

        // Act
        Integer result = clientLoanService.calculateMensualPay(calculatorForm);

        // Assert
        assertNotNull(result);
        assertTrue(result > 0);
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing() {
        when(clientRepository.findByRut(123456788)).thenReturn(Optional.of(clientEntity2));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(invalidForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Faltan datos requeridos", response.getBody());
        verify(clientRepository).findByRut(123456788);
        verify(clientLoanRepository, never()).save(any());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenMensualPayIsZero() {
        // Arrange
        clientLoanForm.setMensualPay(0);
        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Falta calcular el valor de la cuota mensual", response.getBody());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenJobYearsLessThanOne() {
        // Arrange
        clientEntity.setJobYears(0);
        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El cliente no tiene la antigüedad laboral mínima requerida (1 año)", response.getBody());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenTotalDebtExceedsLimit() {
        // Arrange
        clientEntity.setTotalDebt(2000);
        clientLoanForm.setMensualPay(1000); // Total debt ratio will exceed 50%
        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("La deuda total excede el 50% del sueldo mensual", response.getBody());
    }

    @Test
    void addClientLoan_ShouldReturnBadRequest_WhenTotalAgeExceedsLimit() {
        // Arrange
        clientEntity.setYears(65);
        clientLoanForm.setYears(10); // Total age will be 75
        when(clientRepository.findByRut(clientLoanForm.getRut())).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientLoanService.addClientLoan(clientLoanForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El cliente excede el límite de edad permitido (70 años)", response.getBody());
    }
}
