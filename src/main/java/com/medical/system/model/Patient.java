package com.medical.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String documentId;
    private String phone;
    private LocalDate birthDate;
    private String bloodType;
    private String allergies;
    private LocalDateTime createdAt;
}
