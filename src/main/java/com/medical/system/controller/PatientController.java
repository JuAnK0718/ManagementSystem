package com.medical.system.controller;

import com.medical.system.dto.ApiResponse;
import com.medical.system.dto.LoginRequest;
import com.medical.system.dto.RegisterRequest;
import com.medical.system.model.Patient;
import com.medical.system.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // ─── AUTH ────────────────────────────────────────────────────────────────

    @PostMapping("/auth/registro")
    public ResponseEntity<ApiResponse<Patient>> register(
            @Valid @RequestBody RegisterRequest request) {
        Patient patient = patientService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Paciente registrado exitosamente", patient));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<Patient>> login(
            @Valid @RequestBody LoginRequest request) {
        Patient patient = patientService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso", patient));
    }

    // ─── PATIENT CRUD ────────────────────────────────────────────────────────

    @GetMapping("/pacientes")
    public ResponseEntity<ApiResponse<List<Patient>>> getAllPatients() {
        List<Patient> patients = patientService.getAll();
        return ResponseEntity.ok(
            ApiResponse.ok("Se encontraron " + patients.size() + " paciente(s)", patients));
    }

    @GetMapping("/pacientes/{id}")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(@PathVariable String id) {
        Patient patient = patientService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente encontrado", patient));
    }

    @PutMapping("/pacientes/{id}")
    public ResponseEntity<ApiResponse<Patient>> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody RegisterRequest request) {
        Patient updated = patientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Datos del paciente actualizados", updated));
    }

    @DeleteMapping("/pacientes/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable String id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente eliminado exitosamente", null));
    }
}
