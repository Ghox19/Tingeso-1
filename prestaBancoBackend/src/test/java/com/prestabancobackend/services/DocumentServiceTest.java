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

    private DocumentForm documentForm;
    private DocumentEntity documentEntity;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        // Setup DocumentForm
        documentForm = new DocumentForm();
        documentForm.setName("test.pdf");
        documentForm.setContent("dGVzdCBjb250ZW50"); // "test content" in Base64
        documentForm.setType("application/pdf");
        documentForm.setApproved(true);

        // Setup DocumentEntity
        documentEntity = new DocumentEntity();
        documentEntity.setId(1L);
        documentEntity.setName("test.pdf");
        documentEntity.setContent("test content".getBytes());
        documentEntity.setType("application/pdf");
        documentEntity.setApproved(true);

        // Setup MultipartFile mock
        multipartFile = mock(MultipartFile.class);
    }

    @Test
    void saveDocument_ShouldSaveAndReturnDocument() {
        // Arrange
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentEntity);

        // Act
        DocumentEntity savedDocument = documentService.saveDocument(documentForm);

        // Assert
        assertNotNull(savedDocument);
        assertEquals(documentEntity.getName(), savedDocument.getName());
        assertEquals(documentEntity.getType(), savedDocument.getType());
        assertEquals(documentEntity.getApproved(), savedDocument.getApproved());
        verify(documentRepository).save(any(DocumentEntity.class));
    }

    @Test
    void getAll_ShouldReturnAllDocuments() {
        // Arrange
        List<DocumentEntity> expectedDocuments = Arrays.asList(documentEntity);
        when(documentRepository.findAll()).thenReturn(expectedDocuments);

        // Act
        List<DocumentEntity> actualDocuments = documentService.getAll();

        // Assert
        assertEquals(expectedDocuments, actualDocuments);
        verify(documentRepository).findAll();
    }

    @Test
    void getDocument_ShouldReturnDocument_WhenExists() {
        // Arrange
        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));

        // Act
        DocumentEntity result = documentService.getDocument(1L);

        // Assert
        assertNotNull(result);
        assertEquals(documentEntity, result);
        verify(documentRepository).findById(1L);
    }

    @Test
    void getDocument_ShouldReturnNull_WhenNotExists() {
        // Arrange
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        DocumentEntity result = documentService.getDocument(1L);

        // Assert
        assertNull(result);
        verify(documentRepository).findById(1L);
    }

    @Test
    void returnJSON_ShouldReturnDocumentEntity() throws IOException {
        // Arrange
        String fileName = "test.pdf";
        byte[] content = "test content".getBytes();
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getBytes()).thenReturn(content);

        // Act
        DocumentEntity result = documentService.returnJSON(multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getName());
        assertArrayEquals(content, result.getContent());
    }

    @Test
    void setDocumentSaveForm_ShouldReturnDocumentSaveForm() {
        // Act
        DocumentSaveForm result = documentService.setDocumentSaveForm(documentEntity);

        // Assert
        assertNotNull(result);
        assertEquals(documentEntity.getId(), result.getId());
        assertEquals(documentEntity.getName(), result.getName());
        assertEquals(documentEntity.getType(), result.getType());
        assertEquals(documentEntity.getApproved(), result.getApproved());
    }

    @Test
    void updateDocument_ShouldUpdateAndReturnDocument_WhenExists() {
        // Arrange
        DocumentForm updateForm = new DocumentForm();
        updateForm.setName("updated.pdf");
        updateForm.setContent("dXBkYXRlZCBjb250ZW50"); // "updated content" in Base64
        updateForm.setType("application/pdf");
        updateForm.setApproved(false);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentEntity);

        // Act
        DocumentEntity updatedDocument = documentService.updateDocument(1L, updateForm);

        // Assert
        assertNotNull(updatedDocument);
        assertEquals(updateForm.getName(), updatedDocument.getName());
        assertEquals(updateForm.getType(), updatedDocument.getType());
        assertEquals(updateForm.getApproved(), updatedDocument.getApproved());
        verify(documentRepository).findById(1L);
        verify(documentRepository).save(any(DocumentEntity.class));
    }

    @Test
    void updateDocument_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                documentService.updateDocument(1L, documentForm)
        );
        verify(documentRepository).findById(1L);
        verify(documentRepository, never()).save(any(DocumentEntity.class));
    }

    @Test
    void updateDocument_ShouldNotUpdateContent_WhenContentIsNull() {
        // Arrange
        DocumentForm updateForm = new DocumentForm();
        updateForm.setName("updated.pdf");
        updateForm.setContent(null);
        updateForm.setType("application/pdf");
        updateForm.setApproved(false);

        DocumentEntity existingDocument = new DocumentEntity();
        existingDocument.setContent("original content".getBytes());

        when(documentRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(existingDocument);

        // Act
        DocumentEntity updatedDocument = documentService.updateDocument(1L, updateForm);

        // Assert
        assertNotNull(updatedDocument);
        assertArrayEquals("original content".getBytes(), updatedDocument.getContent());
        verify(documentRepository).save(any(DocumentEntity.class));
    }
}