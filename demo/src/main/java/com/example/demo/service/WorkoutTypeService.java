package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.WorkoutTypeRequestDTO;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.WorkoutTypeRepository;

@Service
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;

    WorkoutTypeService( WorkoutTypeRepository workoutTypeRepository){
        this.workoutTypeRepository = workoutTypeRepository;
    }

    public List<WorkoutTypeDTO> getAllTypes(){
        List<WorkoutTypeDTO> workoutTypeDTOs = workoutTypeRepository.findAll().stream()
            .map(workoutType -> new WorkoutTypeDTO(workoutType.getId(), workoutType.getName(), workoutType.getMuscleGroup()))
            .collect(Collectors.toList());
    
        return workoutTypeDTOs;
    }

    public WorkoutType createWorkoutType(WorkoutTypeRequestDTO workoutTypeRequestDTO){
        WorkoutType newType =  new WorkoutType(
            workoutTypeRequestDTO.getName(),
            workoutTypeRequestDTO.getMuscleGroup()
        );
        return workoutTypeRepository.save(newType);
    }

    public void deleteWorkoutType(Long id){
        workoutTypeRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "No such a workout type", HttpStatus.NOT_FOUND));
        workoutTypeRepository.deleteById(id);
    }

}
