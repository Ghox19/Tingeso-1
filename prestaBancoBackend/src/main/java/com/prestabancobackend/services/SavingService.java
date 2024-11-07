package com.prestabancobackend.services;

import com.prestabancobackend.entities.ClientLoanEntity;
import com.prestabancobackend.entities.SavingEntity;
import com.prestabancobackend.form.SavingForm;
import com.prestabancobackend.form.SavingResultForm;
import com.prestabancobackend.getForms.ClientLoanGetForm;
import com.prestabancobackend.getForms.SavingGetForm;
import com.prestabancobackend.repositories.ClientLoanRepository;
import com.prestabancobackend.repositories.SavingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SavingService {
    private final SavingRepository savingRepository;
    private final ClientLoanRepository clientLoanRepository;

    private final ClientLoanService clientLoanService;

    @Autowired
    public SavingService(SavingRepository savingRepository, ClientLoanRepository clientLoanRepository, ClientLoanService clientLoanService) {
        this.savingRepository = savingRepository;
        this.clientLoanRepository = clientLoanRepository;
        this.clientLoanService = clientLoanService;
    }

    public Long addSaving(SavingForm savingForm){
        Optional<ClientLoanEntity> optionalClientLoan = this.clientLoanRepository.findById(savingForm.getClientLoanId());

        if (optionalClientLoan.isEmpty()){
            return null;
        }

        ClientLoanEntity clientLoan = optionalClientLoan.get();
        SavingEntity savingEntity = createSavingFromForm(savingForm, clientLoan);
        List<String> reasons = verifyConditions(savingEntity);
        savingEntity.setReasons(reasons);

        int count = reasons.size();
        if (count > 3){
            savingEntity.setResult("Rechazado");
            clientLoan.setFase("Rechazado");
            clientLoan.setMessage("El Prestamo fue Rechazado por no cumplir correctamente con la Cuenta de Ahorros");
        } else if (count < 3 && count >= 1){
            savingEntity.setResult("Revision Adicional");
        } else {
            savingEntity.setResult("Aprobado");
        }

        this.savingRepository.save(savingEntity);

        clientLoan.setSaving(savingEntity);

        this.clientLoanRepository.save(clientLoan);

        return savingEntity.getId();
    }

    private SavingEntity createSavingFromForm(SavingForm form, ClientLoanEntity clientLoan) {
        SavingEntity saving = new SavingEntity();
        saving.setActualBalance(form.getActualBalance());
        saving.setBalances(form.getBalances());
        saving.setDeposit(form.getDeposit());
        saving.setYears(form.getYears());
        saving.setWithdraw(form.getWithdraw());
        saving.setClientLoan(clientLoan);
        return saving;
    }

    List<String> verifyConditions(SavingEntity saving){
        List<String> reasons = new ArrayList<>();

        ClientLoanEntity clientLoan = saving.getClientLoan();

        double cuota = clientLoan.getLoanAmount() * 0.10;
        if (saving.getActualBalance() < cuota){
            reasons.add("R71: No cumple con el saldo mínimo requerido (10% del préstamo).");
        }

        Integer[] withdrawsArray =  saving.getWithdraw().toArray(new Integer[0]);
        Integer[] balancesArray =  saving.getBalances().toArray(new Integer[0]);
        boolean history = verificarHistorialAhorro(balancesArray, withdrawsArray);
        if (!history){
            reasons.add("R72: No mantiene un historial de ahorro consistente.");
        }
        boolean regularIncome =  verificarDepositosPeriodicos(saving.getDeposit(), saving.getClientLoan().getClient().getMensualIncome());
        if (!regularIncome){
            reasons.add("R73: No cumple con los depósitos periódicos requeridos.");
        }

        if (saving.getYears() < 2){
            cuota = clientLoan.getLoanAmount() * 0.20;
            if (saving.getActualBalance() < cuota){
                reasons.add("R74: No cumple con la relación saldo/antigüedad requerida.");
            }
        }

        boolean recentWithdraw = verificarRetirosRecientes(withdrawsArray, saving.getActualBalance());
        if (!recentWithdraw){
            reasons.add("R75: Ha realizado retiros significativos recientemente.");
        }

        return reasons;
    }

    boolean verificarHistorialAhorro(Integer[] balances, Integer[] withdraws) {
        for (int i = 0; i < 12; i++) {
            if (balances[i] <= 0) {
                return false;
            }
            if (i > 0 && withdraws[i] > (balances[i-1] * 0.50)) {
                return false;
            }
        }
        return true;
    }

    boolean verificarDepositosPeriodicos(List<Integer> deposits, Integer ingresosMensuales) {
        int mesesConsecutivosSinDeposito = 0;
        double minimoMensual = ingresosMensuales * 0.05;

        // Recorremos los últimos 12 meses
        for (Integer deposito : deposits) {
            if (deposito < minimoMensual) {
                mesesConsecutivosSinDeposito++;

                // Si hay más de 3 meses consecutivos sin depósito, falla inmediatamente
                if (mesesConsecutivosSinDeposito > 3) {
                    return false;
                }
            } else {
                // Reinicia el contador de meses consecutivos si hay un depósito válido
                mesesConsecutivosSinDeposito = 0;
            }
        }

        // Verifica que no haya más de 3 meses en total sin depósitos
        return true;
    }

    private boolean verificarRetirosRecientes(Integer[] withdraws, Integer actualBalance) {
        // Verifica los últimos 6 meses
        for (int i = 6; i < 12; i++) {
            if (withdraws[i] > (actualBalance * 0.30)) {
                return false;
            }
        }
        return true;
    }

    private SavingGetForm setSavingGetForm(SavingEntity savingEntity){
        SavingGetForm saving = new SavingGetForm();
        saving.setId(savingEntity.getId());
        saving.setReasons(savingEntity.getReasons());
        saving.setResult(savingEntity.getResult());
        saving.setBalances(savingEntity.getBalances());
        saving.setDeposit(savingEntity.getDeposit());
        saving.setWithdraw(savingEntity.getWithdraw());
        ClientLoanGetForm clientLoanGetForm = clientLoanService.setClientLoanGetForm(savingEntity.getClientLoan());
        saving.setClientLoanEntity(clientLoanGetForm);
        saving.setYears(savingEntity.getYears());
        saving.setActualBalance(savingEntity.getActualBalance());
        return saving;
    }

    public SavingGetForm getSavingById(Long id) {
        Optional<SavingEntity> savingEntity = savingRepository.findById(id);

        if (savingEntity.isPresent()) {
            return setSavingGetForm(savingEntity.get());
        } else {
            throw new EntityNotFoundException("Saving not found with id: " + id);
        }
    }

    public ResponseEntity<Object>  updateStateSaving(SavingResultForm form){
        Optional<SavingEntity> savingEntity = savingRepository.findById(form.getId());

        if(savingEntity.isPresent()){
            if(Objects.equals(form.getResult(), "Aprobado")){
                savingEntity.get().setResult(form.getResult());
                savingRepository.save(savingEntity.get());
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("Se Aprobo correctamente la cuenta");
            } else {
                savingEntity.get().setResult(form.getResult());
                savingRepository.save(savingEntity.get());
                ClientLoanEntity clientLoan = savingEntity.get().getClientLoan();
                clientLoan.setFase("Rechazado");
                clientLoan.setMessage("El Prestamo fue Rechazado por no cumplir correctamente con la Cuenta de Ahorros");
                clientLoanRepository.save(clientLoan);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("Se Rechazo correctamente la cuenta");
            }

        } else {
            return ResponseEntity
                    .badRequest()
                    .body("No se encontro la Cuenta de Ahorros");
        }
    }
}
