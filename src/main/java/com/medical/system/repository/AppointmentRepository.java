package com.medical.system.repository;

import com.medical.system.model.Appointment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AppointmentRepository {

    private final Map<String, Appointment> storage = new ConcurrentHashMap<>();

    public Appointment save(Appointment appointment) {
        storage.put(appointment.getId(), appointment);
        return appointment;
    }

    public Optional<Appointment> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Appointment> findByPatientId(String patientId) {
        return storage.values().stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Appointment> findByPatientIdAndStatus(String patientId, Appointment.AppointmentStatus status) {
        return storage.values().stream()
                .filter(a -> a.getPatientId().equals(patientId) && a.getStatus() == status)
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
