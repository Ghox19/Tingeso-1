package com.prestabancobackend.form;

import lombok.Data;

@Data
public class DocumentSaveForm {
    private Long id;
    private String name;
    private String type;
}
