package com.medical.system.controller;

import com.medical.system.dto.ApiResponse;
import com.medical.system.dto.MedicalRecordRequest;
import com.medical.system.model.MedicalRecord;
import com.medical.system.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historia-clinica")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicalRecord>> createRecord(
            @Valid @RequestBody MedicalRecordRequest request) {
        MedicalRecord record = medicalRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registro clínico creado exitosamente", record));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getAllRecords() {
        List<MedicalRecord> records = medicalRecordService.getAll();
        return ResponseEntity.ok(
            ApiResponse.ok("Se encontraron " + records.size() + " registro(s)", records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecord>> getRecordById(@PathVariable String id) {
        MedicalRecord record = medicalRecordService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Registro clínico encontrado", record));
    }

    @GetMapping("/paciente/{patientId}")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getRecordsByPatient(
            @PathVariable String patientId) {
        List<MedicalRecord> records = medicalRecordService.getByPatientId(patientId);
        return ResponseEntity.ok(
            ApiResponse.ok("Historia clínica del paciente — " + records.size() + " registro(s)", records));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecord>> updateRecord(
            @PathVariable String id,
            @RequestBody MedicalRecordRequest request) {
        MedicalRecord updated = medicalRecordService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Registro clínico actualizado", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable String id) {
        medicalRecordService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registro clínico eliminado", null));
    }
}
