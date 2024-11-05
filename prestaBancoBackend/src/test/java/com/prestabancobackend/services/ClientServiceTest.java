package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.form.ClientInfoRequiredForm;
import com.prestabancobackend.form.DocumentForm;
import com.prestabancobackend.form.RegisterForm;
import com.prestabancobackend.getForms.ClientGetForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private ClientService clientService;

    private RegisterForm clientForm;
    private ClientEntity clientEntity;
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

        // Setup RegisterForm
        clientForm = new RegisterForm();
        clientForm.setName("John");
        clientForm.setLastName("Doe");
        clientForm.setRut(123456789);
        clientForm.setEmail("john.doe@example.com");
        clientForm.setYears(30);
        clientForm.setContact(123456789);
        clientForm.setJobType("Engineer");
        clientForm.setMensualIncome(5000);
        clientForm.setJobYears(5);
        clientForm.setTotalDebt(10000);
        clientForm.setDocuments(Arrays.asList(documentForm));

        // Setup ClientEntity
        clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        clientEntity.setName("John");
        clientEntity.setLastName("Doe");
        clientEntity.setRut(123456789);
        clientEntity.setEmail("john.doe@example.com");
        clientEntity.setYears(30);
        clientEntity.setContact(123456789);
        clientEntity.setJobType("Engineer");
        clientEntity.setMensualIncome(5000);
        clientEntity.setJobYears(5);
        clientEntity.setTotalDebt(10000);
        clientEntity.setDocuments(Arrays.asList(documentEntity));
        clientEntity.setLoans(new ArrayList<>());
    }

    @Test
    void addClient_ShouldReturnSuccess_WhenValidInput() {
        // Arrange
        when(clientRepository.findByRut(clientForm.getRut())).thenReturn(Optional.empty());
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(documentService.saveDocument(any(DocumentForm.class))).thenReturn(documentEntity);

        // Act
        ResponseEntity<Object> response = clientService.addClient(clientForm);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se ingresó correctamente el Usuario", response.getBody());
        verify(clientRepository).findByRut(clientForm.getRut());
        verify(clientRepository, times(2)).save(any(ClientEntity.class));
        verify(documentService).saveDocument(any(DocumentForm.class));
    }

    @Test
    void addClient_ShouldReturnBadRequest_WhenClientExists() {
        // Arrange
        when(clientRepository.findByRut(clientForm.getRut())).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientService.addClient(clientForm);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ya existe un cliente con el RUT especificado", response.getBody());
        verify(clientRepository).findByRut(clientForm.getRut());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void getAllClients_ShouldReturnAllClients() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(Arrays.asList(clientEntity));
        when(documentService.setDocumentSaveForm(any(DocumentEntity.class)))
                .thenReturn(new DocumentSaveForm());

        // Act
        List<ClientGetForm> result = clientService.getAllClients();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(clientRepository).findAll();
    }

    @Test
    void deleteClient_ShouldReturnSuccess_WhenClientExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));

        // Act
        ResponseEntity<Object> response = clientService.deleteClient(1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Se elimino correctamente el Usuario", response.getBody());
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void deleteClient_ShouldReturnBadRequest_WhenClientNotExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = clientService.deleteClient(1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(clientRepository, never()).deleteById(anyLong());
    }

    @Test
    void getClientById_ShouldReturnClient_WhenExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(documentService.setDocumentSaveForm(any(DocumentEntity.class)))
                .thenReturn(new DocumentSaveForm());

        // Act
        ClientGetForm result = clientService.getClientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(clientEntity.getName(), result.getName());
        verify(clientRepository).findById(1L);
    }

    @Test
    void getClientById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clientService.getClientById(1L)
        );
        verify(clientRepository).findById(1L);
    }

    @Test
    void getClientRequiredInfoByRut_ShouldReturnInfo_WhenClientExists() {
        // Arrange
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.of(clientEntity));

        // Act
        ClientInfoRequiredForm result = clientService.getClientRequiredInfoByRut(123456789);

        // Assert
        assertNotNull(result);
        assertEquals(clientEntity.getYears(), result.getYears());
        assertEquals(clientEntity.getJobYears(), result.getJobYears());
        verify(clientRepository).findByRut(123456789);
    }

    @Test
    void getClientRequiredInfoByRut_ShouldReturnNull_WhenClientNotExists() {
        // Arrange
        when(clientRepository.findByRut(123456789)).thenReturn(Optional.empty());

        // Act
        ClientInfoRequiredForm result = clientService.getClientRequiredInfoByRut(123456789);

        // Assert
        assertNull(result);
        verify(clientRepository).findByRut(123456789);
    }

    @Test
    void getClientDocuments_ShouldReturnDocuments_WhenClientExists() {
        // Arrange
        DocumentSaveForm documentSaveForm = new DocumentSaveForm();
        documentSaveForm.setId(1L);
        documentSaveForm.setName("test.pdf");
        documentSaveForm.setType("application/pdf");
        documentSaveForm.setApproved(true);

        ClientGetForm clientGetForm = new ClientGetForm();
        clientGetForm.setId(1L);
        clientGetForm.setDocuments(Arrays.asList(documentSaveForm));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(documentService.setDocumentSaveForm(any(DocumentEntity.class))).thenReturn(documentSaveForm);

        // Act
        List<DocumentSaveForm> result = clientService.getClientDocuments(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(documentSaveForm.getId(), result.get(0).getId());
        assertEquals(documentSaveForm.getName(), result.get(0).getName());
        assertEquals(documentSaveForm.getType(), result.get(0).getType());
        assertEquals(documentSaveForm.getApproved(), result.get(0).getApproved());

        verify(clientRepository).findById(1L);
        verify(documentService).setDocumentSaveForm(any(DocumentEntity.class));
    }
}
