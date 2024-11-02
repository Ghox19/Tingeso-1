package com.prestabancobackend.getForms;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.SavingEntity;
import lombok.Data;

import java.util.List;

@Data
public class ClientLoanGetForm {
    private Long id;

    private String loanName;
    private Integer years;
    private Float interest;
    private Integer loanAmount;
    private Integer mensualPay;
    private String fase;
    private ClientEntity client;

    private SavingEntity savings;

    private List<DocumentSaveForm> documents;
}