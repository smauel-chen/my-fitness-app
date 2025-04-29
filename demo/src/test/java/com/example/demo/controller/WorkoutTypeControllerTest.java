package com.example.demo.controller;

import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.WorkoutTypeRequestDTO;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.service.WorkoutTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutTypeController.class) // 告訴 Spring 只加載 WorkoutTypeController
@AutoConfigureMockMvc(addFilters = false) //專案有開啟 Spring Security，而 @WebMvcTest 預設會開啟基本的 Security Filter。
public class WorkoutTypeControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc; // 模擬 HTTP 請求用的

    @SuppressWarnings("removal")
    @MockBean
    private WorkoutTypeService workoutTypeService; // Mock 掉 Service，不要真的連資料庫


    //或是這裡加入 @WithMockUser 會預設一個已登入的身份
    @Test
    void getWorkoutTypes_shouldReturnList() throws Exception {
        // Arrange：準備資料
        WorkoutTypeDTO mockType = new WorkoutTypeDTO(1L, "臥推", "胸");
        when(workoutTypeService.getAllTypes()).thenReturn(List.of(mockType));

        // Act + Assert：執行請求並驗證結果
        mockMvc.perform(get("/workout-types"))
                .andExpect(status().isOk()) // 預期 200 OK
                .andExpect(jsonPath("$[0].id").value(1L)) // 預期回傳第一筆 id 是 1
                .andExpect(jsonPath("$[0].name").value("臥推")) // 預期回傳 name
                .andExpect(jsonPath("$[0].muscleGroup").value("胸")); // 預期回傳 muscleGroup
    }

    @Test
    void createWorkoutType_shouldSucceed() throws Exception {
        // Arrange
        WorkoutTypeRequestDTO requestDTO = new WorkoutTypeRequestDTO("引體向上", "背");
        WorkoutType mockResponse = new WorkoutType("引體向上", "背");
        mockResponse.setId(1L);

        when(workoutTypeService.createWorkoutType(any(WorkoutTypeRequestDTO.class)))
            .thenReturn(mockResponse);

        // Act & Assert
        //controller測試時是模擬一個HTTP請求
        // //objectMapper ->  自動生成這段       
        //         .content("""
        //             {
        //                 "muscleGroup": "背",
        //                 "name": "引體向上"
        //             }
        //         """))
        mockMvc.perform(post("/workout-types")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.muscleGroup").value("背"))
                .andExpect(jsonPath("$.name").value("引體向上"));
    }

    @Test
    void deleteWorkoutTypeTestReverse() throws Exception{
        //Arrange
        Long workoutTypeId = 122L;

        // 模擬資料庫查不到資料
        doThrow(new ApiException(ApiErrorCode.TYPE_NOT_FOUND, null, HttpStatus.NOT_FOUND)).when(workoutTypeService).deleteWorkoutType(workoutTypeId);
        //Act & Assert
        mockMvc.perform(delete("/workout-types/{id}", workoutTypeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteWorkoutTypeTest() throws Exception{
        //Arrange
        Long workoutTypeId = 1L;

        //Act & Assert
        mockMvc.perform(delete("/workout-types/{id}", workoutTypeId))
                .andExpect(status().isOk());
    }
}
