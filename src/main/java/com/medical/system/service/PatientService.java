package com.medical.system.service;

import com.medical.system.dto.LoginRequest;
import com.medical.system.dto.RegisterRequest;
import com.medical.system.exception.BadRequestException;
import com.medical.system.exception.DuplicateResourceException;
import com.medical.system.exception.ResourceNotFoundException;
import com.medical.system.model.Patient;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient register(RegisterRequest request) {
        // Validate uniqueness
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "Ya existe un paciente registrado con el correo: " + request.getEmail());
        }
        if (patientRepository.existsByDocumentId(request.getDocumentId())) {
            throw new DuplicateResourceException(
                "Ya existe un paciente registrado con el documento: " + request.getDocumentId());
        }

        // Build and persist patient (password stored as plain text — use BCrypt in production)
        Patient patient = Patient.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(request.getPassword())
                .documentId(request.getDocumentId())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .bloodType(request.getBloodType())
                .allergies(request.getAllergies())
                .createdAt(LocalDateTime.now())
                .build();

        return patientRepository.save(patient);
    }

    public Patient login(LoginRequest request) {
        Patient patient = patientRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(
                    "Credenciales incorrectas. Verifique su correo y contraseña."));

        if (!patient.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Credenciales incorrectas. Verifique su correo y contraseña.");
        }

        return patient;
    }

    public Patient getById(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Paciente no encontrado con ID: " + id));
    }

    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    public Patient update(String id, RegisterRequest request) {
        Patient existing = getById(id);

        // Check email conflict with OTHER patients
        patientRepository.findByEmail(request.getEmail())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> { throw new DuplicateResourceException(
                    "El correo ya está registrado por otro paciente."); });

        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail().toLowerCase());
        existing.setPhone(request.getPhone());
        existing.setBirthDate(request.getBirthDate());
        existing.setBloodType(request.getBloodType());
        existing.setAllergies(request.getAllergies());

        return patientRepository.save(existing);
    }

    public void delete(String id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}
