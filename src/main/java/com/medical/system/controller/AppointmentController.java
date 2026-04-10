package com.medical.system.controller;

import com.medical.system.dto.ApiResponse;
import com.medical.system.dto.AppointmentRequest;
import com.medical.system.dto.AppointmentUpdateRequest;
import com.medical.system.model.Appointment;
import com.medical.system.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Appointment>> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cita agendada exitosamente", appointment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAll();
        return ResponseEntity.ok(
            ApiResponse.ok("Se encontraron " + appointments.size() + " cita(s)", appointments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable String id) {
        Appointment appointment = appointmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Cita encontrada", appointment));
    }

    @GetMapping("/paciente/{patientId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAppointmentsByPatient(
            @PathVariable String patientId,
            @RequestParam(required = false) Appointment.AppointmentStatus estado) {

        List<Appointment> appointments = (estado != null)
                ? appointmentService.getByPatientIdAndStatus(patientId, estado)
                : appointmentService.getByPatientId(patientId);

        return ResponseEntity.ok(
            ApiResponse.ok("Citas del paciente encontradas: " + appointments.size(), appointments));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointment(
            @PathVariable String id,
            @RequestBody AppointmentUpdateRequest request) {
        Appointment updated = appointmentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cita actualizada exitosamente", updated));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable String id) {
        appointmentService.cancel(id);
        return ResponseEntity.ok(ApiResponse.ok("Cita cancelada exitosamente", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable String id) {
        appointmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Cita eliminada exitosamente", null));
    }
}
