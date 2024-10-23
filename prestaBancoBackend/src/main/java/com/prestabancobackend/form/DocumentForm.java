package com.prestabancobackend.form;

import lombok.Data;

@Data
public class DocumentForm {
    private String name;
    private String content;
    private String type;
}