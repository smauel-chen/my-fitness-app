package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.BodyWeightRecord;

public interface BodyWeightRecordRepository extends JpaRepository<BodyWeightRecord, Long> {
    List<BodyWeightRecord> findByUserIdAndDateAfterOrderByDateAsc(Long userId, LocalDate date);
    Optional<BodyWeightRecord> findByUserIdAndDate(Long userId, LocalDate date);
    List<BodyWeightRecord> findByUserIdOrderByDateAsc(Long userId);
}
