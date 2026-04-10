package com.medical.system.controller;

import com.medical.system.dto.ApiResponse;
import com.medical.system.dto.PrescriptionRequest;
import com.medical.system.model.Prescription;
import com.medical.system.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescripciones")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Prescription>> createPrescription(
            @Valid @RequestBody PrescriptionRequest request) {
        Prescription prescription = prescriptionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Prescripción creada exitosamente", prescription));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Prescription>>> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getAll();
        return ResponseEntity.ok(
            ApiResponse.ok("Se encontraron " + prescriptions.size() + " prescripción(es)", prescriptions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Prescription>> getPrescriptionById(@PathVariable String id) {
        Prescription prescription = prescriptionService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Prescripción encontrada", prescription));
    }

    @GetMapping("/paciente/{patientId}")
    public ResponseEntity<ApiResponse<List<Prescription>>> getPrescriptionsByPatient(
            @PathVariable String patientId) {
        List<Prescription> prescriptions = prescriptionService.getByPatientId(patientId);
        return ResponseEntity.ok(
            ApiResponse.ok("Prescripciones del paciente — " + prescriptions.size() + " resultado(s)", prescriptions));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePrescription(@PathVariable String id) {
        prescriptionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Prescripción eliminada exitosamente", null));
    }
}
