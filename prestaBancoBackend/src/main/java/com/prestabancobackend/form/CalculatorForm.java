package com.prestabancobackend.form;

import lombok.Data;

@Data
public class CalculatorForm {
    private Integer years;
    private Float interest;
    private Integer loanAmount;
}
