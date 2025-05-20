package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.SubmitWeightDTO;
import com.example.demo.entity.BodyWeightRecord;
import com.example.demo.entity.User;
import com.example.demo.service.BodyWeightRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BodyWeightRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BodyWeightRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    BodyWeightRecordService bodyWeightRecordService;

    @Test
    void addBodyWeightTest() throws Exception {
        Long userId = 1L;
        SubmitWeightDTO dto = new SubmitWeightDTO(LocalDate.of(2025, 5, 20), 72.3);

        doNothing().when(bodyWeightRecordService).saveOrUpdateWeight(eq(userId), any(SubmitWeightDTO.class));

        mockMvc.perform(post("/user/{id}/weight", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllWeightRecords() throws Exception {
        Long userId = 1L;
        User user = new User();
        BodyWeightRecord record = new BodyWeightRecord(LocalDate.of(2025, 8, 21), 81.4, user);
        when(bodyWeightRecordService.getAllRecords(userId)).thenReturn(List.of(record));

        mockMvc.perform(get("/user/{id}/records", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].date").value("2025-08-21"))
                .andExpect(jsonPath("$[0].weight").value(record.getWeight()));
    }


}
