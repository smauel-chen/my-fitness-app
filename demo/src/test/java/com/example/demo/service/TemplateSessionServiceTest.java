package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.CreateExerciseDTO;
import com.example.demo.dto.CreateSetDTO;
import com.example.demo.dto.CreateTemplateRequestDTO;
import com.example.demo.dto.ExerciseDetailDTO;
import com.example.demo.dto.SetDetailDTO;
import com.example.demo.dto.TemplateDetailDTO;
import com.example.demo.dto.TemplateSummaryDTO;
import com.example.demo.entity.TemplateExercise;
import com.example.demo.entity.TemplateSession;
import com.example.demo.entity.TemplateSet;
import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutType;
import com.example.demo.repository.TemplateSessionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutTypeRepository;

@ExtendWith(MockitoExtension.class)
class TemplateSessionServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private WorkoutTypeRepository workoutTypeRepository;
    @Mock private TemplateSessionRepository templateSessionRepository;

    @InjectMocks private TemplateSessionService templateSessionService;

    @Test
    void createTemplate_shouldSaveTemplateSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long typeId = 100L;

        CreateSetDTO setDto = new CreateSetDTO(10, 60);
        CreateExerciseDTO exerciseDto = new CreateExerciseDTO(typeId, List.of(setDto));
        CreateTemplateRequestDTO dto = new CreateTemplateRequestDTO(
                "胸部訓練模板",
                LocalDate.of(2025, 5, 20),
                List.of(exerciseDto)
        );

        User mockUser = new User(); mockUser.setId(userId);
        WorkoutType mockType = new WorkoutType(); mockType.setId(typeId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(workoutTypeRepository.findById(typeId)).thenReturn(Optional.of(mockType));

        // Act
        templateSessionService.createTemplate(userId, dto);

        // Assert
        verify(templateSessionRepository,times(1)).save(any(TemplateSession.class));
    }

    @Test
    void getTemplateSummaries_shouldReturnDTOList_whenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        WorkoutType type1 = new WorkoutType(); type1.setId(101L); type1.setName("啞鈴臥推"); type1.setMainTag("胸部");
        WorkoutType type2 = new WorkoutType(); type2.setId(102L); type2.setName("上斜胸推"); type2.setMainTag("胸部");
        WorkoutType type3 = new WorkoutType(); type3.setId(103L); type3.setName("三頭伸展"); type3.setMainTag("三頭肌");

        TemplateSet set1 = new TemplateSet(8, 50);
        TemplateSet set2 = new TemplateSet(10, 60);

        TemplateExercise ex1 = new TemplateExercise(type1); ex1.setSets(List.of(set1, set2)); // 2 sets
        TemplateExercise ex2 = new TemplateExercise(type2); ex2.setSets(List.of(set1));       // 1 set
        TemplateExercise ex3 = new TemplateExercise(type3); ex3.setSets(List.of(set1));       // 1 set

        TemplateSession session = new TemplateSession(); 
        session.setId(200L);
        session.setTitle("胸部課表");
        session.setPlannedDate(LocalDate.of(2025, 5, 20));
        session.setExercises(List.of(ex1, ex2, ex3));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(templateSessionRepository.findByUserIdOrderByPlannedDateDesc(userId)).thenReturn(List.of(session));

        // Act
        List<TemplateSummaryDTO> result = templateSessionService.getTemplateSummariesByUserId(userId);

        // Assert
        assertEquals(1, result.size());
        TemplateSummaryDTO dto = result.get(0);

        assertEquals("胸部課表", dto.getTitle());
        assertEquals(LocalDate.of(2025, 5, 20), dto.getPlannedDate());

        assertEquals(3, dto.getExercises().size());
        assertEquals("啞鈴臥推", dto.getExercises().get(0).getTypeName());
        assertEquals(2, dto.getExercises().get(0).getSets());

        assertEquals(List.of("胸部", "三頭肌"), dto.getMainTags()); // 排序後前 3 tag
    }

    @Test
    void getTemplateDetail_shouldReturnFullDetailDTO_whenTemplateExists() {
        // Arrange
        Long templateId = 1L;

        WorkoutType type = new WorkoutType();
        type.setId(100L);
        type.setName("啞鈴臥推");
        type.setMainTag("胸部");
        // type.setSecondaryTags(List.of("三頭肌", "胸鎖肌"));

        TemplateSet set1 = new TemplateSet(8, 50);
        TemplateSet set2 = new TemplateSet(10, 55);

        TemplateExercise exercise = new TemplateExercise(type);
        exercise.setSets(List.of(set1, set2));

        TemplateSession template = new TemplateSession();
        template.setId(templateId);
        template.setTitle("胸部課表");
        template.setPlannedDate(LocalDate.of(2025, 5, 20));
        template.setExercises(List.of(exercise));

        when(templateSessionRepository.findById(templateId)).thenReturn(Optional.of(template));

        // Act
        TemplateDetailDTO result = templateSessionService.getTemplateDetail(templateId);

        // Assert
        assertEquals("胸部課表", result.getTitle());
        assertEquals(LocalDate.of(2025, 5, 20), result.getPlannedDate());
        assertEquals(1, result.getExercises().size());

        ExerciseDetailDTO exDto = result.getExercises().get(0);
        assertEquals("啞鈴臥推", exDto.getTypeName());
        assertEquals("胸部", exDto.getMainTag());
        // assertEquals("三頭肌", exDto.getSecondaryTags().get(0));

        SetDetailDTO setDto1 = exDto.getSets().get(0);
        assertEquals(8, setDto1.getReps());
        assertEquals(50, setDto1.getWeight());
    }

    @Test
    void updateTemplate_shouldUpdateSuccessfully_whenTemplateExists() {
        Long templateId = 1L;
        Long typeId = 100L;

        // 模擬 DTO
        CreateSetDTO setDto = new CreateSetDTO(8, 50);
        CreateExerciseDTO exDto = new CreateExerciseDTO(typeId, List.of(setDto));
        CreateTemplateRequestDTO dto = new CreateTemplateRequestDTO(
                "更新課表",
                LocalDate.of(2025, 5, 21),
                List.of(exDto)
        );

        // 原本 template
        TemplateSession template = new TemplateSession();
        template.setId(templateId);
        template.setTitle("舊課表");
        template.setPlannedDate(LocalDate.of(2025, 5, 10));
        template.setExercises(new ArrayList<>()); // 假設原本也有資料，這裡就留空簡化

        WorkoutType type = new WorkoutType();
        type.setId(typeId);
        type.setName("啞鈴臥推");

        when(templateSessionRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(workoutTypeRepository.findById(typeId)).thenReturn(Optional.of(type));

        // Act
        templateSessionService.updateTemplate(templateId, dto);

        // Assert：驗證資料有儲存
        verify(templateSessionRepository, times(1)).save(any(TemplateSession.class));
    }

}
