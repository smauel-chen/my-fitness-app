package com.example.demo.controller;


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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.CreateExerciseDTO;
import com.example.demo.dto.CreateSetDTO;
import com.example.demo.dto.CreateTemplateRequestDTO;
import com.example.demo.dto.ExerciseDetailDTO;
import com.example.demo.dto.ExerciseSummaryDTO;
import com.example.demo.dto.SetDetailDTO;
import com.example.demo.dto.TemplateDetailDTO;
import com.example.demo.dto.TemplateSummaryDTO;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.service.TemplateSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TemplateSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TemplateSessionControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    TemplateSessionService templateSessionService;
@Test
void getTemplateSummariesTest() throws Exception {
    Long userId = 1L;

    // 準備 mock 資料
    ExerciseSummaryDTO exercise = new ExerciseSummaryDTO(2L, "啞鈴臥推", 3);
    TemplateSummaryDTO template = new TemplateSummaryDTO(
            1L,
            "胸推模板",
            LocalDate.of(2025, 5, 20),
            List.of(exercise),
            List.of("胸部", "三頭肌")
    );

    when(templateSessionService.getTemplateSummariesByUserId(userId))
            .thenReturn(List.of(template));

    mockMvc.perform(get("/user/{id}/templates", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("胸推模板"))
            .andExpect(jsonPath("$[0].exercises[0].typeName").value("啞鈴臥推"))
            .andExpect(jsonPath("$[0].mainTags[0]").value("胸部"))
            .andExpect(jsonPath("$[0].mainTags[1]").value("三頭肌"));           
    }

    @Test
void createTemplateTest() throws Exception {
    Long userId = 1L;

    // 建立 mock input DTO
    CreateSetDTO set = new CreateSetDTO(8, 60);
    CreateExerciseDTO exercise = new CreateExerciseDTO(2L, List.of(set));
    CreateTemplateRequestDTO dto = new CreateTemplateRequestDTO(
            "胸部進階課表",
            LocalDate.of(2025, 5, 20),
            List.of(exercise)
    );

    // Mock 行為：void 方法
    doNothing().when(templateSessionService).createTemplate(eq(userId), any(CreateTemplateRequestDTO.class));

    mockMvc.perform(post("/user/{id}/template", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(content().string("Template created"));
    }

    @Test
    void getTemplateDetailTest() throws Exception {
        Long userId = 1L;
        Long templateId = 10L;

        SetDetailDTO set = new SetDetailDTO(8, 60);
        ExerciseDetailDTO exercise = new ExerciseDetailDTO(
                2L, "啞鈴臥推", "胸部", List.of(), List.of(set)
        );
        TemplateDetailDTO dto = new TemplateDetailDTO(
                templateId, "胸部訓練模板", LocalDate.of(2025, 5, 20), List.of(exercise)
        );

        when(templateSessionService.getTemplateDetail(templateId)).thenReturn(dto);

        mockMvc.perform(get("/user/{id}/template/{templateId}", userId, templateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("胸部訓練模板"))
                .andExpect(jsonPath("$.exercises.length()").value(1))
                .andExpect(jsonPath("$.exercises[0].typeId").value(2))
                .andExpect(jsonPath("$.exercises[0].mainTag").value("胸部"))
                .andExpect(jsonPath("$.exercises[0].sets[0].reps").value(8))
                .andExpect(jsonPath("$.exercises[0].sets[0].weight").value(60));
    }

    @Test
    void deleteTemplateTest() throws Exception {
        Long userId = 1L;
        Long templateId = 10L;

        doNothing().when(templateSessionService).deleteTemplate(templateId);

        mockMvc.perform(delete("/user/{id}/template/{templateId}", userId, templateId))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateTemplateTest() throws Exception {
        Long userId = 1L;
        Long templateId = 10L;

        // 建立 mock input DTO
        CreateSetDTO set = new CreateSetDTO(10, 70);
        CreateExerciseDTO exercise = new CreateExerciseDTO(3L, List.of(set));
        CreateTemplateRequestDTO dto = new CreateTemplateRequestDTO(
                "更新後的課表",
                LocalDate.of(2025, 5, 25),
                List.of(exercise)
        );

        doNothing().when(templateSessionService).updateTemplate(eq(templateId), any(CreateTemplateRequestDTO.class));

        mockMvc.perform(put("/user/{id}/template/{templateId}", userId, templateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    //negative testing
    @Test
    void getTemplateDetail_shouldReturnNotFound_whenTemplateNotExist() throws Exception {
        Long userId = 1L;
        Long templateId = 999L;

        when(templateSessionService.getTemplateDetail(templateId))
                .thenThrow(new ApiException(ApiErrorCode.TEMPLATE_NOT_FOUND, "找不到訓練卡片", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/user/{id}/template/{templateId}", userId, templateId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTemplate_shouldReturnBadRequest_whenWorkoutTypeNotFound() throws Exception {
        Long userId = 1L;

        // 假設 typeId 99 不存在
        CreateSetDTO set = new CreateSetDTO(10, 70);
        CreateExerciseDTO exercise = new CreateExerciseDTO(99L, List.of(set));
        CreateTemplateRequestDTO dto = new CreateTemplateRequestDTO(
                "無效動作模板",
                LocalDate.of(2025, 5, 25),
                List.of(exercise)
        );

        doThrow(new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到動作", HttpStatus.BAD_REQUEST))
                .when(templateSessionService).createTemplate(eq(userId), any(CreateTemplateRequestDTO.class));

        mockMvc.perform(post("/user/{id}/template", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
