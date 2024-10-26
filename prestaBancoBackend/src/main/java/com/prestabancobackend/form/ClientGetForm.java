package com.prestabancobackend.form;

import com.prestabancobackend.entities.ClientLoanEntity;
import lombok.Data;

import java.util.List;

@Data
public class ClientGetForm {
    private long id;

    private String name;
    private String lastName;
    private Integer rut;
    private String email;
    private Integer years;
    private Integer contact;
    private String jobType;
    private Integer mensualIncome;
    private Integer jobYears;
    private Integer totalDebt;

    private List<DocumentSaveForm> documents;

    private List<ClientLoanEntity> loans;
}
