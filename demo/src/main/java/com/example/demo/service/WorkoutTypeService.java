package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.CreateTypeRequestDTO;
import com.example.demo.entity.WorkoutType;
import com.example.demo.repository.WorkoutTypeRepository;

@Service
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutTypeService( WorkoutTypeRepository workoutTypeRepository){
        this.workoutTypeRepository = workoutTypeRepository;
    }

    public List<WorkoutTypeDTO> getAllTypes(){
        List<WorkoutTypeDTO> workoutTypeDTOs = workoutTypeRepository.findAll().stream()
            .map(workoutType -> new WorkoutTypeDTO(workoutType.getId(), workoutType.getName(), workoutType.getMainTag(), workoutType.getSecondaryTags()))
            .collect(Collectors.toList());
    
        return workoutTypeDTOs;
    }

    public WorkoutType createWorkoutType(CreateTypeRequestDTO dto){
        WorkoutType newType =  new WorkoutType(
            dto.getName(),
            dto.getMainTag(),
            dto.getSecondaryTags()
        );
        return workoutTypeRepository.save(newType);
    }

}
