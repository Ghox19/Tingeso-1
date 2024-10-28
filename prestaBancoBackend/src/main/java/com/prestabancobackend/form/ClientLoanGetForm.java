package com.prestabancobackend.form;

import com.prestabancobackend.entities.ClientEntity;
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

    private List<DocumentSaveForm> documents;
}