package com.medical.system.controller;

import com.medical.system.dto.ApiResponse;
import com.medical.system.dto.ExamRequest;
import com.medical.system.model.MedicalExam;
import com.medical.system.service.MedicalExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examenes")
@RequiredArgsConstructor
public class MedicalExamController {

    private final MedicalExamService medicalExamService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicalExam>> requestExam(
            @Valid @RequestBody ExamRequest request) {
        MedicalExam exam = medicalExamService.requestExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Examen solicitado exitosamente", exam));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicalExam>>> getAllExams() {
        List<MedicalExam> exams = medicalExamService.getAll();
        return ResponseEntity.ok(
            ApiResponse.ok("Se encontraron " + exams.size() + " examen(es)", exams));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalExam>> getExamById(@PathVariable String id) {
        MedicalExam exam = medicalExamService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Examen encontrado", exam));
    }

    @GetMapping("/paciente/{patientId}")
    public ResponseEntity<ApiResponse<List<MedicalExam>>> getExamsByPatient(
            @PathVariable String patientId) {
        List<MedicalExam> exams = medicalExamService.getByPatientId(patientId);
        return ResponseEntity.ok(
            ApiResponse.ok("Exámenes del paciente — " + exams.size() + " resultado(s)", exams));
    }

    // PATCH to update status, optionally deliver results
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<MedicalExam>> updateExamStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String rawStatus = body.get("estado");
        String results = body.get("resultados");

        if (rawStatus == null || rawStatus.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("El campo 'estado' es obligatorio"));
        }

        MedicalExam.ExamStatus newStatus;
        try {
            newStatus = MedicalExam.ExamStatus.valueOf(rawStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Estado inválido. Valores permitidos: SOLICITADO, PROGRAMADO, EN_PROCESO, COMPLETADO, CANCELADO"));
        }

        MedicalExam updated = medicalExamService.updateStatus(id, newStatus, results);
        return ResponseEntity.ok(ApiResponse.ok("Estado del examen actualizado", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable String id) {
        medicalExamService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Examen eliminado exitosamente", null));
    }
}
