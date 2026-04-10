package com.medical.system.config;

import com.medical.system.model.*;
import com.medical.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Seeds the in-memory store with sample data so the API is usable
 * immediately after startup without manual POST requests.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalExamRepository medicalExamRepository;

    @Override
    public void run(String... args) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  Inicializando datos de ejemplo en memoria...");
        log.info("═══════════════════════════════════════════════════════════");

        // ─── Patients ────────────────────────────────────────────────────────
        String p1Id = UUID.randomUUID().toString();
        String p2Id = UUID.randomUUID().toString();

        Patient patient1 = Patient.builder()
                .id(p1Id)
                .fullName("María García López")
                .email("maria.garcia@email.com")
                .password("123456")
                .documentId("1234567890")
                .phone("3001234567")
                .birthDate(LocalDate.of(1990, 5, 15))
                .bloodType("O+")
                .allergies("Penicilina")
                .createdAt(LocalDateTime.now())
                .build();

        Patient patient2 = Patient.builder()
                .id(p2Id)
                .fullName("Carlos Rodríguez Martínez")
                .email("carlos.rodriguez@email.com")
                .password("123456")
                .documentId("9876543210")
                .phone("3109876543")
                .birthDate(LocalDate.of(1985, 11, 3))
                .bloodType("A-")
                .allergies("Ninguna")
                .createdAt(LocalDateTime.now())
                .build();

        patientRepository.save(patient1);
        patientRepository.save(patient2);

        // ─── Appointments ────────────────────────────────────────────────────
        Appointment appointment1 = Appointment.builder()
                .id(UUID.randomUUID().toString())
                .patientId(p1Id)
                .doctorName("Dr. Alejandro Pérez")
                .specialty("Medicina General")
                .appointmentDate(LocalDateTime.now().plusDays(3))
                .reason("Control rutinario de presión arterial")
                .status(Appointment.AppointmentStatus.CONFIRMADA)
                .createdAt(LocalDateTime.now())
                .build();

        Appointment appointment2 = Appointment.builder()
                .id(UUID.randomUUID().toString())
                .patientId(p2Id)
                .doctorName("Dra. Laura Sánchez")
                .specialty("Cardiología")
                .appointmentDate(LocalDateTime.now().plusDays(7))
                .reason("Evaluación cardiovascular")
                .status(Appointment.AppointmentStatus.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);

        // ─── Medical Records ─────────────────────────────────────────────────
        MedicalRecord record1 = MedicalRecord.builder()
                .id(UUID.randomUUID().toString())
                .patientId(p1Id)
                .doctorName("Dr. Alejandro Pérez")
                .diagnosis("Hipertensión arterial leve")
                .treatment("Dieta baja en sodio y ejercicio moderado")
                .symptoms("Dolor de cabeza, mareos ocasionales")
                .visitDate(LocalDateTime.now().minusDays(30))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();

        medicalRecordRepository.save(record1);

        // ─── Prescriptions ───────────────────────────────────────────────────
        Prescription prescription1 = Prescription.builder()
                .id(UUID.randomUUID().toString())
                .patientId(p1Id)
                .doctorName("Dr. Alejandro Pérez")
                .medications(List.of(
                    Prescription.PrescriptionItem.builder()
                        .medicationName("Losartán 50mg")
                        .dosage("50mg")
                        .frequency("1 vez al día")
                        .duration("3 meses")
                        .build(),
                    Prescription.PrescriptionItem.builder()
                        .medicationName("Hidroclorotiazida 12.5mg")
                        .dosage("12.5mg")
                        .frequency("1 vez al día en la mañana")
                        .duration("3 meses")
                        .build()
                ))
                .instructions("Tomar con abundante agua. Evitar el consumo de sal.")
                .validUntil(LocalDate.now().plusMonths(3))
                .issuedAt(LocalDateTime.now().minusDays(30))
                .build();

        prescriptionRepository.save(prescription1);

        // ─── Medical Exams ───────────────────────────────────────────────────
        MedicalExam exam1 = MedicalExam.builder()
                .id(UUID.randomUUID().toString())
                .patientId(p1Id)
                .examType("Electrocardiograma")
                .requestedBy("Dr. Alejandro Pérez")
                .reason("Monitoreo cardíaco por hipertensión")
                .status(MedicalExam.ExamStatus.PROGRAMADO)
                .scheduledDate(LocalDateTime.now().plusDays(5))
                .requestedAt(LocalDateTime.now().minusDays(2))
                .build();

        medicalExamRepository.save(exam1);

        log.info("  ✔ 2 pacientes creados");
        log.info("  ✔ 2 citas creadas");
        log.info("  ✔ 1 historia clínica creada");
        log.info("  ✔ 1 prescripción creada");
        log.info("  ✔ 1 examen creado");
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  Paciente 1 → ID: {}", p1Id);
        log.info("  Correo: maria.garcia@email.com  |  Contraseña: 123456");
        log.info("  Paciente 2 → ID: {}", p2Id);
        log.info("  Correo: carlos.rodriguez@email.com  |  Contraseña: 123456");
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  API disponible en: http://localhost:8080/api");
        log.info("═══════════════════════════════════════════════════════════");
    }
}
