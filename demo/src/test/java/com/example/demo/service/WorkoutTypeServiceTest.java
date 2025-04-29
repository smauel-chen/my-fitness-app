package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.WorkoutTypeRequestDTO;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.WorkoutTypeRepository;


@ExtendWith(MockitoExtension.class)
public class WorkoutTypeServiceTest {
    @InjectMocks
    private WorkoutTypeService workoutTypeService;

    @Mock
    private WorkoutTypeRepository workoutTypeRepository;

    @Test
    void getAllTypes_succeed_test(){
        WorkoutType workoutType = new WorkoutType("臥推", "胸");
        workoutType.setId(1L);

        when(workoutTypeRepository.findAll()).thenReturn(List.of(workoutType));

        List<WorkoutTypeDTO> result = workoutTypeService.getAllTypes();
    
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(workoutType.getMuscleGroup(), result.get(0).getMuscleGroup());
        assertEquals(workoutType.getName(), result.get(0).getName());
        assertEquals(workoutType.getId(), result.get(0).getId());

        verify(workoutTypeRepository,times(1)).findAll();
    }

    @Test 
    void createWorkoutType_succeed_test(){
        WorkoutTypeRequestDTO workoutTypeRequestDTO = new WorkoutTypeRequestDTO("Squat", "Leg");
        

        when(workoutTypeRepository.save(any(WorkoutType.class)))
            .thenAnswer(invocation -> {
                WorkoutType saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
        });

        WorkoutType result =  workoutTypeService.createWorkoutType(workoutTypeRequestDTO);

        assertNotNull(result);
        assertEquals(workoutTypeRequestDTO.getMuscleGroup(), result.getMuscleGroup());
        assertEquals(workoutTypeRequestDTO.getName(), result.getName());
        verify(workoutTypeRepository,times(1)).save(any(WorkoutType.class));
    }
    
    @Test
    void deleteWorkoutType_succeed_test(){
        Long typeId = 1L;
        WorkoutType workoutType = new WorkoutType("臥推", "胸");

        when(workoutTypeRepository.findById(typeId)).thenReturn(Optional.of(workoutType));

        workoutTypeService.deleteWorkoutType(typeId);

        verify(workoutTypeRepository, times(1)).findById(typeId);
        verify(workoutTypeRepository, times(1)).deleteById(typeId);
    }

    //reverse testing part
    @Test
    void deleteWorkoutType_type_not_found_test(){
        Long typeId = 1L;

        when(workoutTypeRepository.findById(typeId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> workoutTypeService.deleteWorkoutType(typeId));

        assertEquals(ApiErrorCode.TYPE_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}
