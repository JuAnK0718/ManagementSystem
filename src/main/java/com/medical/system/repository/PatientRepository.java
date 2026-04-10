package com.medical.system.repository;

import com.medical.system.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PatientRepository {

    // In-memory storage using ConcurrentHashMap for thread safety
    private final Map<String, Patient> storage = new ConcurrentHashMap<>();

    public Patient save(Patient patient) {
        storage.put(patient.getId(), patient);
        return patient;
    }

    public Optional<Patient> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Patient> findByEmail(String email) {
        return storage.values().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Patient> findByDocumentId(String documentId) {
        return storage.values().stream()
                .filter(p -> p.getDocumentId().equals(documentId))
                .findFirst();
    }

    public List<Patient> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean existsByEmail(String email) {
        return storage.values().stream()
                .anyMatch(p -> p.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByDocumentId(String documentId) {
        return storage.values().stream()
                .anyMatch(p -> p.getDocumentId().equals(documentId));
    }

    public void deleteById(String id) {
        storage.remove(id);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
