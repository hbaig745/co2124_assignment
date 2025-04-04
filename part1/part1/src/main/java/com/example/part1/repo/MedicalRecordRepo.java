package com.example.part1.repo;

import com.example.part1.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepo extends JpaRepository<MedicalRecord, Long> {
}
