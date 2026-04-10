package com.medical.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    private String id;
    private String patientId;
    private String doctorName;
    private List<PrescriptionItem> medications;
    private String instructions;
    private LocalDate validUntil;
    private LocalDateTime issuedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItem {
        private String medicationName;
        private String dosage;
        private String frequency;
        private String duration;
    }
}
