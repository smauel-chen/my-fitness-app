// WorkoutSessionRepository.java
package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkoutSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    Optional<WorkoutSession> findByIdAndUser_Id(Long id, Long userId);
    List<WorkoutSession> findByUser_Id(Long userId);
    List<WorkoutSession> findByUserIdOrderByDateDesc(Long userId);
    List<WorkoutSession> findByUserIdAndDateAfter(Long userId, LocalDate date);

}
