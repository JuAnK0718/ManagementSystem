package com.medical.system.repository;

import com.medical.system.model.Prescription;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class PrescriptionRepository {

    private final Map<String, Prescription> storage = new ConcurrentHashMap<>();

    public Prescription save(Prescription prescription) {
        storage.put(prescription.getId(), prescription);
        return prescription;
    }

    public Optional<Prescription> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Prescription> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Prescription> findByPatientId(String patientId) {
        return storage.values().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(Prescription::getIssuedAt).reversed())
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
