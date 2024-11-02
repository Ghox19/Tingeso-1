package com.prestabancobackend.getForms;

import lombok.Data;

import java.util.List;

@Data
public class SavingGetForm {
    private long id;

    private Integer years;
    private Integer actualBalance;
    private String result;
    private List<Integer> balances;
    private List<Integer> deposit;
    private List<Integer> withdraw;
    private List<String> reasons;
    private ClientLoanGetForm clientLoanEntity;
}
