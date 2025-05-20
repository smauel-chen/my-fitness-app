package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.CreateTypeRequestDTO;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.TemplateExerciseRepository;
import com.example.demo.repository.WorkoutTypeRepository;

@Service
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;
    private final TemplateExerciseRepository templateExerciseRepository;

    public WorkoutTypeService( WorkoutTypeRepository workoutTypeRepository,
    TemplateExerciseRepository templateExerciseRepository){
        this.workoutTypeRepository = workoutTypeRepository;
        this.templateExerciseRepository = templateExerciseRepository;
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

    public void deleteCustomedWorkoutType(Long id){
        WorkoutType type = workoutTypeRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到動作", HttpStatus.NOT_FOUND));
        if (!type.getIsCusetom()) {
            throw new ApiException(ApiErrorCode.FORBIDDEN, "無法刪除預設動作", HttpStatus.FORBIDDEN);
        }
        workoutTypeRepository.delete(type);
    }

    public WorkoutType temp(Long id){
        WorkoutType type = workoutTypeRepository.findById(id)
        .orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到動作", HttpStatus.NOT_FOUND));
        type.setIsCustom(true);
        type.setName("農夫走路");
        type.setMainTag("Core");
        type.getSecondaryTags().clear();
        type.getSecondaryTags().add("Leg");
        workoutTypeRepository.save(type);
        return type;
    }

    public int deleteFromAdmin(){
        List<WorkoutType> allTypes = workoutTypeRepository.findAll();

        int deleteCount = 0;
        for (WorkoutType type : allTypes) {
            boolean isUsed = templateExerciseRepository.existsByWorkoutType(type);
            if (!isUsed && type.getIsCusetom()) {  // 只刪使用者建立的
                workoutTypeRepository.delete(type);
                deleteCount++;
            }
        }
        return deleteCount;
    }

}
