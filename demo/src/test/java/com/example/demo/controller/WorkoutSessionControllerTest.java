package com.example.demo.controller;


import com.example.demo.dto.WorkoutAllSessionsDTO;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.dto.CreateExerciseDTO;
import com.example.demo.dto.CreateSessionRequestDTO;
import com.example.demo.dto.CreateSetDTO;
import com.example.demo.dto.ExerciseDetailDTO;
import com.example.demo.dto.SessionDetailDTO;
import com.example.demo.dto.SetDetailDTO;
import com.example.demo.service.WorkoutSessionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutSessionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private WorkoutSessionService workoutSessionService;


    // --- 下面開始放測試案例 ---

    @Test
    void addWorkoutSessionTest() throws Exception {
        // Arrange
        Long userId = 1L;
        CreateSessionRequestDTO requestDTO = new CreateSessionRequestDTO();
        // 這邊因為 SessionDTO 有 date / note / sets，你可以簡單設定
        requestDTO.setTitle("test title");
        requestDTO.setDate(LocalDate.parse("2025-05-16"));
        requestDTO.setExercises(List.of(new CreateExerciseDTO(1L, List.of(new CreateSetDTO(8,50)))));

        doNothing().when(workoutSessionService).addSession(eq(userId), any(CreateSessionRequestDTO.class));

        // Act & Assert
        mockMvc.perform(post("/user/{id}/session", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.title").value("test title"))
                .andExpect(status().isOk());
                
    }

    @Test
    void getAllSessionTest() throws Exception {
        //arrange
        Long userId = 1L;
        WorkoutAllSessionsDTO session1 = new WorkoutAllSessionsDTO(
            100L,
            "Push Day",
            LocalDate.of(2025, 5, 16),
            List.of("Chest", "Shoulder")
        );

        WorkoutAllSessionsDTO session2 = new WorkoutAllSessionsDTO(
            101L,
            "Pull Day",
            LocalDate.of(2025, 5, 17),
            List.of("Back", "Biceps")
        );

        List<WorkoutAllSessionsDTO> workoutSessionDTO = List.of(session1, session2);

        when(workoutSessionService.getAllSession(userId)).thenReturn(workoutSessionDTO);
        //Act & assert

        mockMvc.perform(get("/user/{id}/sessions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sessionId").value(session1.getSessionId()))
                .andExpect(jsonPath("$[1].title").value(session2.getTitle()))
                .andExpect(jsonPath("$[1].mainTags[1]").value(session2.getMainTags().get(1)));
    }

    @Test
    void deleteSessionTest() throws Exception {
        Long userId = 1L;
        Long sessionId = 1L;

        doNothing().when(workoutSessionService).deleteSession(eq(userId), eq(sessionId));

        mockMvc.perform(delete("/user/{id}/session/{sessionId}", userId, sessionId))
                .andExpect(status().isOk());
    }

    @Test
    void updateWorkoutSessionTest() throws Exception {
        // Arrange
        Long userId = 1L;
        Long sessionId = 100L;
    
        CreateSetDTO set = new CreateSetDTO(10, 60);
        CreateExerciseDTO exercise = new CreateExerciseDTO(2L, List.of(set));
        CreateSessionRequestDTO dto = new CreateSessionRequestDTO(
                "Updated Title",
                LocalDate.of(2025, 5, 20),
                List.of(exercise)
        );
    
        doNothing().when(workoutSessionService).updateSession(eq(userId), eq(sessionId), any(CreateSessionRequestDTO.class));
    
        // Act & Assert
        mockMvc.perform(put("/user/{id}/session/{sessionId}", userId, sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.exercises[0].typeId").value(2))
                .andExpect(jsonPath("$.exercises[0].sets[0].reps").value(10))
                .andExpect(jsonPath("$.exercises[0].sets[0].weight").value(60));
    }

    @Test
    void getSesionBySessionIdTest() throws Exception {
        Long userId = 1L;
        Long sessionId = 100L;
        Long typeId = 2L;

        SetDetailDTO set = new SetDetailDTO(8, 50);
        ExerciseDetailDTO ex = new ExerciseDetailDTO(typeId, "胸推", "Chest", List.of(), List.of(set));
        SessionDetailDTO dto = new SessionDetailDTO(sessionId, "Selected title", LocalDate.of(2025, 5, 18), List.of(ex));

        when(workoutSessionService.getSessionDetailById(userId, sessionId)).thenReturn(dto);

        mockMvc.perform(get("/user/{id}/session/{sessionId}", userId, sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andExpect(jsonPath("$.exercises[0].typeId").value(ex.getTypeId()))
                .andExpect(jsonPath("$.exercises[0].sets[0].reps").value(set.getReps()));
    }

    @Test
    void createSessionFromTemplateTest() throws Exception {
        Long userId = 1L;
        Long templateId = 10L;

        doNothing().when(workoutSessionService).createFromTemplate(eq(userId), eq(templateId));

        mockMvc.perform(post("/user/{id}/session/from-template/{templateId}", userId, templateId))
                .andExpect(status().isCreated())
                .andExpect(content().string("建立成功"));
    }

    @Test
    void getWorkoutTagFrequencyTest() throws Exception {
        Long userId = 1L;
        String period = "weekly";

        Map<String, Long> mockResult = new HashMap<>();
        mockResult.put("Chest", 3L);
        mockResult.put("Back", 2L);

        when(workoutSessionService.getTagFrequency(userId, period)).thenReturn(mockResult);

        mockMvc.perform(get("/user/{id}/charts/tag-frequency", userId)
                        .param("period", period))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Chest").value(3))
                .andExpect(jsonPath("$.Back").value(2));
    }

    @Test
    void getSessionDetail_shouldReturnNotFound_whenSessionIdInvalid() throws Exception {
        Long userId = 1L;
        Long sessionId = 999L;

        when(workoutSessionService.getSessionDetailById(userId, sessionId))
                .thenThrow(new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "找不到紀錄", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/user/{id}/session/{sessionId}", userId, sessionId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSession_shouldReturnBadRequest_whenTypeNotFound() throws Exception {
        Long userId = 1L;
        CreateSetDTO set = new CreateSetDTO(8, 50);
        CreateExerciseDTO ex = new CreateExerciseDTO(999L, List.of(set)); // 無效 typeId
        CreateSessionRequestDTO dto = new CreateSessionRequestDTO(
                "錯誤動作",
                LocalDate.of(2025, 5, 20),
                List.of(ex)
        );

        doThrow(new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到動作", HttpStatus.BAD_REQUEST))
                .when(workoutSessionService).addSession(eq(userId), any(CreateSessionRequestDTO.class));

        mockMvc.perform(post("/user/{id}/session", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTagFrequency_shouldReturnBadRequest_whenPeriodInvalid() throws Exception {
        Long userId = 1L;
        String invalidPeriod = "yearly";

        when(workoutSessionService.getTagFrequency(userId, invalidPeriod))
                .thenThrow(new ApiException(ApiErrorCode.INVALID_REQUEST, "不合法的期間參數", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/user/{id}/charts/tag-frequency", userId)
                        .param("period", invalidPeriod))
                .andExpect(status().isBadRequest());
    }

}
