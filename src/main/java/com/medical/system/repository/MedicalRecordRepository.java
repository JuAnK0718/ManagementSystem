package com.medical.system.repository;

import com.medical.system.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class MedicalRecordRepository {

    private final Map<String, MedicalRecord> storage = new ConcurrentHashMap<>();

    public MedicalRecord save(MedicalRecord record) {
        storage.put(record.getId(), record);
        return record;
    }

    public Optional<MedicalRecord> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<MedicalRecord> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<MedicalRecord> findByPatientId(String patientId) {
        return storage.values().stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
