package com.prestabancobackend.form;

import lombok.Data;

@Data
public class ClientLoanPreApprovedForm {

    private Long clientLoanId;
    private Integer fireInsurance;
    private Double deduction;
}
