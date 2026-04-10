package com.medical.system.dto;

import com.medical.system.model.Prescription;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrescriptionRequest {

    @NotBlank(message = "El ID del paciente es obligatorio")
    private String patientId;

    @NotBlank(message = "El nombre del médico es obligatorio")
    private String doctorName;

    @NotEmpty(message = "Debe incluir al menos un medicamento")
    private List<Prescription.PrescriptionItem> medications;

    private String instructions;
    private LocalDate validUntil;
}
