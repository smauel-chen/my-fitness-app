package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.WorkoutSetRequestDTO;
import com.example.demo.service.WorkoutSetService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(WorkoutSetController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutSetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private WorkoutSetService workoutSetService;

    //addset
    @Test
    void addSetTest() throws Exception {
        //arrange
        Long userId = 1L;
        Long sessionId = 1L;
        WorkoutSetRequestDTO requestDTO = new WorkoutSetRequestDTO();
        requestDTO.setReps(10);
        requestDTO.setWeight(50);
        requestDTO.setTypeId(1L);

        doNothing().when(workoutSetService).addSet(eq(userId), eq(sessionId), any(WorkoutSetRequestDTO.class));

        //act & assert
        mockMvc.perform(post("/user/{id}/session/{sessionId}/set", userId, sessionId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());    
    }

    //deleteset
    @Test
    void deleteSetTest() throws Exception {
        //arrange
        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;
        doNothing().when(workoutSetService).deleteSet(eq(userId), eq(sessionId), eq(setId));
        //act & assert
        mockMvc.perform(delete("/user/{id}/session/{sessionId}/set/{setId}", userId, sessionId, setId))
                .andExpect(status().isOk());
    }


    //updateset
    @Test
    void updateSetTest() throws Exception {
        //arrange
        WorkoutSetRequestDTO requestDTO = new WorkoutSetRequestDTO();
        requestDTO.setReps(10);
        requestDTO.setTypeId(1L);
        requestDTO.setWeight(50);

        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;

        doNothing().when(workoutSetService).updateSet(eq(userId), eq(sessionId), eq(setId), any(WorkoutSetRequestDTO.class));
        //act & assert
        mockMvc.perform(put("/user/{id}/session/{sessionId}/set/{setId}", userId, sessionId, setId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

    }

}
