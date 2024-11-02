package com.prestabancobackend.services;

import com.prestabancobackend.entities.DocumentEntity;
import com.prestabancobackend.form.DocumentForm;
import com.prestabancobackend.getForms.DocumentSaveForm;
import com.prestabancobackend.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public DocumentEntity saveDocument(DocumentForm documentForm) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(documentForm.getName());

        // Decode Base64 content
        byte[] contentBytes = Base64.getDecoder().decode(documentForm.getContent());
        documentEntity.setContent(contentBytes);

        documentEntity.setType(documentForm.getType());
        documentEntity.setApproved(documentForm.getApproved());

        documentRepository.save(documentEntity);
        // Save the document entity
        return documentEntity;
    }

    public List<DocumentEntity> getAll() {
        return documentRepository.findAll();
    }

    public DocumentEntity getDocument(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    public DocumentEntity returnJSON(MultipartFile file) throws IOException {
        DocumentEntity document = new DocumentEntity();
        document.setName(file.getOriginalFilename());
        document.setContent(file.getBytes());
        return document;
    }

    public DocumentSaveForm setDocumentSaveForm(DocumentEntity document){
        DocumentSaveForm documentSaveForm = new DocumentSaveForm();
        documentSaveForm.setId(document.getId());
        documentSaveForm.setName(document.getName());
        documentSaveForm.setType(document.getType());
        documentSaveForm.setApproved(document.getApproved());
        return documentSaveForm;
    }
}
