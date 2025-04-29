package com.example.demo.controller;

import com.example.demo.dto.MuscleGroupTotalDTO;
import com.example.demo.dto.PopularTypesDTO;
import com.example.demo.dto.WeeklyFrequencyDTO;
import com.example.demo.dto.WeeklySummaryDTO;
import com.example.demo.dto.WorkoutProgressDTO;
import com.example.demo.dto.WorkoutSessionDTO;
import com.example.demo.dto.WorkoutSessionRequestDTO;
import com.example.demo.dto.WorkoutSessionSummaryDTO;

import com.example.demo.service.WorkoutSessionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        WorkoutSessionRequestDTO requestDTO = new WorkoutSessionRequestDTO();
        // 這邊因為 SessionDTO 有 date / note / sets，你可以簡單設定
        requestDTO.setDate("2025-05-01");
        requestDTO.setNote("Chest day");
        requestDTO.setSets(List.of()); // 簡單給空集合即可（因為現在是測 Controller，不要卡在內容）

        doNothing().when(workoutSessionService).addSession(eq(userId), any(WorkoutSessionRequestDTO.class));

        // Act & Assert
        mockMvc.perform(post("/user/{id}/session", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllSessionTest() throws Exception {
        //arrange
        Long userId = 1L;
        Long sessionId = 1L;
        //init 用 null可能會忽略這個欄位，不會回傳json格式，因此isArray exists這兩個method會找不到對應的.sets然後測試就不會過
        WorkoutSessionDTO workoutSessionDTO = new WorkoutSessionDTO(sessionId, "2025-05-01", "Chest day", null);

        when(workoutSessionService.getAllSession(userId)).thenReturn(List.of(workoutSessionDTO));
        //Act & assert

        mockMvc.perform(get("/user/{id}/sessions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(workoutSessionDTO.getSessionId()))
                .andExpect(jsonPath("$[0].date").value(workoutSessionDTO.getDate()))
                .andExpect(jsonPath("$[0].note").value(workoutSessionDTO.getNote()))
                .andExpect(jsonPath("$[0].sets").isEmpty());
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
    void getWorkoutSessionSummaryTest() throws Exception {
        Long userId = 1L;
        Long sessionId = 1L;

        WorkoutSessionSummaryDTO summaryDTO = new WorkoutSessionSummaryDTO(sessionId, "2025-05-02", "Still chest day", 1600);

        when(workoutSessionService.getWorkoutSessionSummary(eq(userId))).thenReturn(List.of(summaryDTO));

        mockMvc.perform(get("/user/{id}/sessions/summary", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(summaryDTO.getSessionId()))
                .andExpect(jsonPath("$[0].date").value(summaryDTO.getDate()))
                .andExpect(jsonPath("$[0].note").value(summaryDTO.getNote()))
                .andExpect(jsonPath("$[0].totalWeight").value(summaryDTO.getTotalWeight()));
    }

    @Test
    void getWeeklySummaryTest() throws Exception {
        Long userId = 1L;
        WeeklySummaryDTO weeklySummaryDTO = new WeeklySummaryDTO("2025-W20", 1600);

        when(workoutSessionService.getWeeklySummary(eq(userId))).thenReturn(List.of(weeklySummaryDTO));
        mockMvc.perform(get("/user/{id}/sessions/weekly-summary", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].week").value(weeklySummaryDTO.getWeek()))
                .andExpect(jsonPath("$[0].totalWeight").value(weeklySummaryDTO.getTotalWeight()));
    }

    @Test 
    void getWeeklyWorkoutDaysTest() throws Exception {
        Long userId = 1L;
        WeeklyFrequencyDTO weeklyFrequencyDTO = new WeeklyFrequencyDTO("2025-W20", 4);

        when(workoutSessionService.getWeeklyFrequency(eq(userId))).thenReturn(List.of(weeklyFrequencyDTO));

        mockMvc.perform(get("/user/{id}/sessions/weekly-frequency", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weekKey").value(weeklyFrequencyDTO.getWeekKey()))
                .andExpect(jsonPath("$[0].frequency").value(weeklyFrequencyDTO.getFrequency()));
    }

    @Test
    void getTotalWeightsByMuscleGroupTest() throws Exception {
        Long userId = 1L;
        MuscleGroupTotalDTO muscleGroupTotalDTO = new MuscleGroupTotalDTO("Chest", 1600);

        when(workoutSessionService.getTotalWeightsByMuscleGroup(eq(userId))).thenReturn(List.of(muscleGroupTotalDTO));

        mockMvc.perform(get("/user/{id}/muscle-groups/total-weight", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].muscleGroup").value(muscleGroupTotalDTO.getMuscleGroup()))
                .andExpect(jsonPath("$[0].totalWeight").value(muscleGroupTotalDTO.getTotalWeight()));
    }
    
    @Test
    void getPopularWorkoutType() throws Exception {
        Long userId = 1L;
        PopularTypesDTO popularTypesDTO = new PopularTypesDTO("Chest", 3);

        when(workoutSessionService.getPopularWorkoutType(eq(userId))).thenReturn(List.of(popularTypesDTO));

        mockMvc.perform(get("/user/{id}/workouts/popular-type", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(popularTypesDTO.getType()))
                .andExpect(jsonPath("$[0].count").value(popularTypesDTO.getCount()));
    }

    @Test
    void getWorkoutProgressByType() throws Exception {
        Long userId = 1L;
        Long typeId = 1L;

        WorkoutProgressDTO progressDTO = new WorkoutProgressDTO("2025-05-02", 1600);

        when(workoutSessionService.getWorkoutProgressByType(eq(userId), eq(typeId))).thenReturn(List.of(progressDTO));
        mockMvc.perform(get("/user/{id}/workouts/progress", userId)
                        .param("typeId", String.valueOf(typeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(progressDTO.getDate()))
                .andExpect(jsonPath("$[0].totalWeight").value(progressDTO.getTotalWeight()));
    }
}
