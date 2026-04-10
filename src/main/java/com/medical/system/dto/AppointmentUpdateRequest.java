package com.medical.system.dto;

import com.medical.system.model.Appointment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentUpdateRequest {
    private String doctorName;
    private String specialty;
    private LocalDateTime appointmentDate;
    private String reason;
    private Appointment.AppointmentStatus status;
    private String notes;
}
