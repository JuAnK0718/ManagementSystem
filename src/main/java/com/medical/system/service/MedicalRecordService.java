package com.medical.system.service;

import com.medical.system.dto.MedicalRecordRequest;
import com.medical.system.exception.ResourceNotFoundException;
import com.medical.system.model.MedicalRecord;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    public MedicalRecord create(MedicalRecordRequest request) {
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + request.getPatientId());
        }

        LocalDateTime visitDate = request.getVisitDate() != null
                ? request.getVisitDate()
                : LocalDateTime.now();

        MedicalRecord record = MedicalRecord.builder()
                .id(UUID.randomUUID().toString())
                .patientId(request.getPatientId())
                .doctorName(request.getDoctorName())
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .symptoms(request.getSymptoms())
                .attachments(request.getAttachments())
                .visitDate(visitDate)
                .createdAt(LocalDateTime.now())
                .build();

        return medicalRecordRepository.save(record);
    }

    public MedicalRecord getById(String id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Historia clínica no encontrada con ID: " + id));
    }

    public List<MedicalRecord> getAll() {
        return medicalRecordRepository.findAll();
    }

    public List<MedicalRecord> getByPatientId(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + patientId);
        }
        return medicalRecordRepository.findByPatientId(patientId);
    }

    public MedicalRecord update(String id, MedicalRecordRequest request) {
        MedicalRecord record = getById(id);

        if (request.getDoctorName() != null) record.setDoctorName(request.getDoctorName());
        if (request.getDiagnosis() != null) record.setDiagnosis(request.getDiagnosis());
        if (request.getTreatment() != null) record.setTreatment(request.getTreatment());
        if (request.getSymptoms() != null) record.setSymptoms(request.getSymptoms());
        if (request.getAttachments() != null) record.setAttachments(request.getAttachments());
        if (request.getVisitDate() != null) record.setVisitDate(request.getVisitDate());

        return medicalRecordRepository.save(record);
    }

    public void delete(String id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Historia clínica no encontrada con ID: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }
}
