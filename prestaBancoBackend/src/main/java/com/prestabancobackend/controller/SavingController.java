package com.prestabancobackend.controller;

import com.prestabancobackend.form.SavingForm;
import com.prestabancobackend.getForms.SavingGetForm;
import com.prestabancobackend.services.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/saving")
public class SavingController {

    private final SavingService savingService;

    @Autowired
    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }

    @PostMapping
    public ResponseEntity<String> addSaving(@RequestBody SavingForm savingForm) {
        savingService.addSaving(savingForm);
        return ResponseEntity.ok("Saving subido exitosamente");
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
}