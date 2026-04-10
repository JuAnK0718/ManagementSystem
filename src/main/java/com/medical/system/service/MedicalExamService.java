package com.medical.system.service;

import com.medical.system.dto.ExamRequest;
import com.medical.system.exception.BadRequestException;
import com.medical.system.exception.ResourceNotFoundException;
import com.medical.system.model.MedicalExam;
import com.medical.system.repository.MedicalExamRepository;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicalExamService {

    private final MedicalExamRepository medicalExamRepository;
    private final PatientRepository patientRepository;

    public MedicalExam requestExam(ExamRequest request) {
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + request.getPatientId());
        }

        MedicalExam exam = MedicalExam.builder()
                .id(UUID.randomUUID().toString())
                .patientId(request.getPatientId())
                .examType(request.getExamType())
                .requestedBy(request.getRequestedBy())
                .reason(request.getReason())
                .status(MedicalExam.ExamStatus.SOLICITADO)
                .scheduledDate(request.getScheduledDate())
                .requestedAt(LocalDateTime.now())
                .build();

        return medicalExamRepository.save(exam);
    }

    public MedicalExam getById(String id) {
        return medicalExamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Examen no encontrado con ID: " + id));
    }

    public List<MedicalExam> getAll() {
        return medicalExamRepository.findAll();
    }

    public List<MedicalExam> getByPatientId(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + patientId);
        }
        return medicalExamRepository.findByPatientId(patientId);
    }

    public MedicalExam updateStatus(String id, MedicalExam.ExamStatus newStatus, String results) {
        MedicalExam exam = getById(id);

        if (exam.getStatus() == MedicalExam.ExamStatus.CANCELADO) {
            throw new BadRequestException("No se puede actualizar un examen cancelado.");
        }
        if (exam.getStatus() == MedicalExam.ExamStatus.COMPLETADO) {
            throw new BadRequestException("El examen ya está completado.");
        }

        exam.setStatus(newStatus);

        if (newStatus == MedicalExam.ExamStatus.COMPLETADO) {
            exam.setResults(results);
            exam.setCompletedAt(LocalDateTime.now());
        }

        return medicalExamRepository.save(exam);
    }

    public void delete(String id) {
        if (!medicalExamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Examen no encontrado con ID: " + id);
        }
        medicalExamRepository.deleteById(id);
    }
}
