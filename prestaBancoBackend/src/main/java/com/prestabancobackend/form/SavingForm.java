package com.prestabancobackend.form;

import lombok.Data;

import java.util.List;

@Data
public class SavingForm {
    private Long clientLoanId;
    private Integer years;
    private Integer actualBalance;
    private List<Integer> balances;
    private List<Integer> deposit;
    private List<Integer> withdraw;
}
