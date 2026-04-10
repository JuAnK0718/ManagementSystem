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
public class MedicalExam {
    private String id;
    private String patientId;
    private String examType;
    private String requestedBy;
    private String reason;
    private ExamStatus status;
    private String results;
    private LocalDateTime requestedAt;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedAt;

    public enum ExamStatus {
        SOLICITADO, PROGRAMADO, EN_PROCESO, COMPLETADO, CANCELADO
    }
}
