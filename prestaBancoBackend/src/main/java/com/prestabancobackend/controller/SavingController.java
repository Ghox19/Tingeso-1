package com.prestabancobackend.controller;

import com.prestabancobackend.form.SavingForm;
import com.prestabancobackend.form.SavingResultForm;
import com.prestabancobackend.getForms.SavingGetForm;
import com.prestabancobackend.services.SavingService;
import jakarta.persistence.OneToMany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/saving")
public class SavingController {

    private final SavingService savingService;

    @Autowired
    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }

    @PostMapping
    public Long addSaving(@RequestBody SavingForm savingForm) {
        return savingService.addSaving(savingForm);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingGetForm> getSaving(@PathVariable Long id) {
        SavingGetForm savingEntity = savingService.getSavingById(id);
        if (savingEntity != null) {
            return ResponseEntity.ok(savingEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateResultSaving(@RequestBody SavingResultForm form) {
        return savingService.updateStateSaving(form);
    }
}
