package com.prestabancobackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private byte[] content;

    private String type;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "client_id", nullable = true)
    private ClientEntity client;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "clientLoan_id", nullable = true)
    private ClientLoanEntity clientLoan;
}
