package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TemplateExercise;
import com.example.demo.entity.WorkoutType;

public interface TemplateExerciseRepository extends JpaRepository<TemplateExercise, Long> {
        boolean existsByWorkoutType(WorkoutType type);
}

