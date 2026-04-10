package com.medical.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private String id;
    private String patientId;
    private String doctorName;
    private String specialty;
    private LocalDateTime appointmentDate;
    private String reason;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;

    public enum AppointmentStatus {
        PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA
    }
}
