// WorkoutSetRepository.java
package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkoutSet;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

}
