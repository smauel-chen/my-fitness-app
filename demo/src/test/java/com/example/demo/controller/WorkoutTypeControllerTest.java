package com.example.demo.controller;

import com.example.demo.entity.WorkoutType;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutSetRepository;
import com.example.demo.repository.WorkoutTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class WorkoutTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutTypeRepository workoutTypeRepository;
    @MockBean
    private  PasswordEncoder passwordEncoder;
    @MockBean
    private  AuthenticationManager authenticationManager;
    @MockBean
    private  UserRepository userRepository;
    @MockBean
    private  WorkoutSessionRepository workoutSessionRepository;
    @MockBean
    private  WorkoutSetRepository workoutSetRepository;



    @Test   
    void getWorkoutTypes_shouldReturnList() throws Exception {
        WorkoutType type = new WorkoutType("胸", "臥推");
        type.setId(1L);
        when(workoutTypeRepository.findAll()).thenReturn(List.of(type));

        mockMvc.perform(get("/workout-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("臥推"))
                .andExpect(jsonPath("$[0].muscleGroup").value("胸"));
    }
}
