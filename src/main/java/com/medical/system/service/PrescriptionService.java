package com.medical.system.service;

import com.medical.system.dto.PrescriptionRequest;
import com.medical.system.exception.ResourceNotFoundException;
import com.medical.system.model.Prescription;
import com.medical.system.repository.PatientRepository;
import com.medical.system.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;

    public Prescription create(PrescriptionRequest request) {
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + request.getPatientId());
        }

        Prescription prescription = Prescription.builder()
                .id(UUID.randomUUID().toString())
                .patientId(request.getPatientId())
                .doctorName(request.getDoctorName())
                .medications(request.getMedications())
                .instructions(request.getInstructions())
                .validUntil(request.getValidUntil())
                .issuedAt(LocalDateTime.now())
                .build();

        return prescriptionRepository.save(prescription);
    }

    public Prescription getById(String id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Prescripción no encontrada con ID: " + id));
    }

    public List<Prescription> getAll() {
        return prescriptionRepository.findAll();
    }

    public List<Prescription> getByPatientId(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + patientId);
        }
        return prescriptionRepository.findByPatientId(patientId);
    }

    public void delete(String id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prescripción no encontrada con ID: " + id);
        }
        prescriptionRepository.deleteById(id);
    }
}
