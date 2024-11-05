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
    private Integer propertyValue;
    private Integer loanAmount;
    private Float loanRatio;
    private Integer mensualPay;
    private String fase;
    private String message;
    private Double cuotaIncome;
    private Double debtCuota;
    private ClientEntity client;

    private SavingEntity savings;

    private List<DocumentSaveForm> documents;
}