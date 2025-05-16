package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TemplateExercise;

public interface TemplateExerciseRepository extends JpaRepository<TemplateExercise, Long> {}

