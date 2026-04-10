package com.medical.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicalRecordRequest {

    @NotBlank(message = "El ID del paciente es obligatorio")
    private String patientId;

    @NotBlank(message = "El nombre del médico es obligatorio")
    private String doctorName;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnosis;

    @NotBlank(message = "El tratamiento es obligatorio")
    private String treatment;

    private String symptoms;
    private List<String> attachments;
    private LocalDateTime visitDate;
}
