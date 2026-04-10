package com.medical.system.repository;

import com.medical.system.model.MedicalExam;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class MedicalExamRepository {

    private final Map<String, MedicalExam> storage = new ConcurrentHashMap<>();

    public MedicalExam save(MedicalExam exam) {
        storage.put(exam.getId(), exam);
        return exam;
    }

    public Optional<MedicalExam> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<MedicalExam> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<MedicalExam> findByPatientId(String patientId) {
        return storage.values().stream()
                .filter(e -> e.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(MedicalExam::getRequestedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<MedicalExam> findByPatientIdAndStatus(String patientId, MedicalExam.ExamStatus status) {
        return storage.values().stream()
                .filter(e -> e.getPatientId().equals(patientId) && e.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
