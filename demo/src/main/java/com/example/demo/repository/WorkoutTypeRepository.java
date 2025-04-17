package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkoutType;


public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Long> {
}
