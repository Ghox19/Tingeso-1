package com.prestabancobackend.services;

import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.form.DocumentForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
import com.prestabancobackend.repositories.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    private DocumentEntity document1;
    private DocumentForm documentForm;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        // Setup DocumentEntity
        document1 = new DocumentEntity();
        document1.setId(1L);
        document1.setName("test.pdf");
        document1.setContent("test content".getBytes());
        document1.setType("application/pdf");
        document1.setApproved(true);

        // Setup DocumentForm
        documentForm = new DocumentForm();
        documentForm.setName("test.pdf");
        documentForm.setContent(Base64.getEncoder().encodeToString("test content".getBytes()));
        documentForm.setType("application/pdf");
        documentForm.setApproved(true);

        // Setup MultipartFile mock
        multipartFile = mock(MultipartFile.class);
    }

    @Test
    void saveDocument_ShouldSaveAndReturnDocument() {
        // Arrange
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(document1);

        // Act
        DocumentEntity savedDocument = documentService.saveDocument(documentForm);

        // Assert
        assertNotNull(savedDocument);
        assertEquals(document1.getName(), savedDocument.getName());
        assertEquals(document1.getType(), savedDocument.getType());
        assertEquals(document1.getApproved(), savedDocument.getApproved());
        verify(documentRepository).save(any(DocumentEntity.class));
    }

    @Test
    void getAll_ShouldReturnAllDocuments() {
        // Arrange
        List<DocumentEntity> expectedDocuments = Arrays.asList(document1);
        when(documentRepository.findAll()).thenReturn(expectedDocuments);

        // Act
        List<DocumentEntity> actualDocuments = documentService.getAll();

        // Assert
        assertEquals(expectedDocuments, actualDocuments);
        verify(documentRepository).findAll();
    }

    @Test
    void getDocument_ShouldReturnDocument() {
        // Arrange
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document1));

        // Act
        DocumentEntity foundDocument = documentService.getDocument(1L);

        // Assert
        assertNotNull(foundDocument);
        assertEquals(document1, foundDocument);
        verify(documentRepository).findById(1L);
    }

    @Test
    void getDocument_ShouldReturnNull_WhenDocumentNotFound() {
        // Arrange
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        DocumentEntity foundDocument = documentService.getDocument(1L);

        // Assert
        assertNull(foundDocument);
        verify(documentRepository).findById(1L);
    }

    @Test
    void returnJSON_ShouldReturnDocumentEntity() throws IOException {
        // Arrange
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getBytes()).thenReturn("test content".getBytes());

        // Act
        DocumentEntity result = documentService.returnJSON(multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals("test.pdf", result.getName());
        assertArrayEquals("test content".getBytes(), result.getContent());
    }

    @Test
    void setDocumentSaveForm_ShouldReturnDocumentSaveForm() {
        // Act
        DocumentSaveForm result = documentService.setDocumentSaveForm(document1);

        // Assert
        assertNotNull(result);
        assertEquals(document1.getId(), result.getId());
        assertEquals(document1.getName(), result.getName());
        assertEquals(document1.getType(), result.getType());
        assertEquals(document1.getApproved(), result.getApproved());
    }

    @Test
    void name() {
    }
}