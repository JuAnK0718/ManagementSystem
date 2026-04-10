package com.medical.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    private String id;
    private String patientId;
    private String doctorName;
    private String diagnosis;
    private String treatment;
    private String symptoms;
    private List<String> attachments;
    private LocalDateTime visitDate;
    private LocalDateTime createdAt;
}
