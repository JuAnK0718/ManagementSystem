package com.medical.system.dto;

import com.medical.system.model.Appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    @NotBlank(message = "El ID del paciente es obligatorio")
    private String patientId;

    @NotBlank(message = "El nombre del médico es obligatorio")
    private String doctorName;

    @NotBlank(message = "La especialidad es obligatoria")
    private String specialty;

    @NotNull(message = "La fecha de la cita es obligatoria")
    @Future(message = "La fecha de la cita debe ser en el futuro")
    private LocalDateTime appointmentDate;

    @NotBlank(message = "El motivo de la consulta es obligatorio")
    private String reason;

    private String notes;
}
