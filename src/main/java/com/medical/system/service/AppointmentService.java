package com.medical.system.service;

import com.medical.system.dto.AppointmentRequest;
import com.medical.system.dto.AppointmentUpdateRequest;
import com.medical.system.exception.BadRequestException;
import com.medical.system.exception.ResourceNotFoundException;
import com.medical.system.model.Appointment;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public Appointment create(AppointmentRequest request) {
        // Validate patient exists
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + request.getPatientId());
        }

        Appointment appointment = Appointment.builder()
                .id(UUID.randomUUID().toString())
                .patientId(request.getPatientId())
                .doctorName(request.getDoctorName())
                .specialty(request.getSpecialty())
                .appointmentDate(request.getAppointmentDate())
                .reason(request.getReason())
                .status(Appointment.AppointmentStatus.PENDIENTE)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        return appointmentRepository.save(appointment);
    }

    public Appointment getById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Cita no encontrada con ID: " + id));
    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getByPatientId(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + patientId);
        }
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getByPatientIdAndStatus(String patientId,
                                                      Appointment.AppointmentStatus status) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Paciente no encontrado con ID: " + patientId);
        }
        return appointmentRepository.findByPatientIdAndStatus(patientId, status);
    }

    public Appointment update(String id, AppointmentUpdateRequest request) {
        Appointment appointment = getById(id);

        // Cannot modify a cancelled or completed appointment
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELADA ||
            appointment.getStatus() == Appointment.AppointmentStatus.COMPLETADA) {
            throw new BadRequestException(
                "No se puede modificar una cita con estado: " + appointment.getStatus());
        }

        if (request.getDoctorName() != null) appointment.setDoctorName(request.getDoctorName());
        if (request.getSpecialty() != null) appointment.setSpecialty(request.getSpecialty());
        if (request.getAppointmentDate() != null) appointment.setAppointmentDate(request.getAppointmentDate());
        if (request.getReason() != null) appointment.setReason(request.getReason());
        if (request.getStatus() != null) appointment.setStatus(request.getStatus());
        if (request.getNotes() != null) appointment.setNotes(request.getNotes());

        return appointmentRepository.save(appointment);
    }

    public void cancel(String id) {
        Appointment appointment = getById(id);

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETADA) {
            throw new BadRequestException("No se puede cancelar una cita ya completada.");
        }
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELADA) {
            throw new BadRequestException("La cita ya se encuentra cancelada.");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELADA);
        appointmentRepository.save(appointment);
    }

    public void delete(String id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita no encontrada con ID: " + id);
        }
        appointmentRepository.deleteById(id);
    }
}
