package com.medical.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamRequest {

    @NotBlank(message = "El ID del paciente es obligatorio")
    private String patientId;

    @NotBlank(message = "El tipo de examen es obligatorio")
    private String examType;

    @NotBlank(message = "El médico solicitante es obligatorio")
    private String requestedBy;

    @NotBlank(message = "El motivo del examen es obligatorio")
    private String reason;

    private LocalDateTime scheduledDate;
}
