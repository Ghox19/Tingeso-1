package com.prestabancobackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;

    private String name;
    private String description;
    private Integer maxYears;
    private Float minInterest;
    private Float maxInterest;
    private Integer maxAmount;

    @ElementCollection
    @CollectionTable(name = "loan_requirements", joinColumns = @JoinColumn(name = "loan_id"))
    @Column(name = "requirement")
    private List<String> requirements;
}
