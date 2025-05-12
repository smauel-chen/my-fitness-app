// WorkoutSetRepository.java
package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkoutSet;

import java.util.List;
import java.util.Optional;


public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
    List<WorkoutSet> findBySessionId(Long sessionId);
    Optional<WorkoutSet> findBySessionIdAndId(Long sessionId, Long id);
}
