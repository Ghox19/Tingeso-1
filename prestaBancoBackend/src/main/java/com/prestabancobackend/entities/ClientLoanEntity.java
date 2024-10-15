package com.prestabancobackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "client_loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;

    private String loanName;
    private Integer years;
    private Float interest;
    private Integer loanAmount;
    private Integer mensualPay;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ElementCollection
    private List<Boolean> requirementsApproved;
}
