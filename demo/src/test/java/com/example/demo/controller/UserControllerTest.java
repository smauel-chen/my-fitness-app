package com.example.demo.controller;

import com.example.demo.dto.EnrollRequestDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdatedRequestDTO;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private UserService userService;

    // --- 下面可以開始放各個測試 ---
    @Test
    void enrollTest() throws Exception {
        EnrollRequestDTO enrollRequestDTO = new EnrollRequestDTO("testName","testPassword",20);
        doNothing().when(userService).enrollUser(any(EnrollRequestDTO.class));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("註冊成功！"));
    }

    @Test
    void updateUserTest() throws Exception {
        Long userId = 1L;
        UserUpdatedRequestDTO newUser = new UserUpdatedRequestDTO("john", 32);
        UserDTO updated = new UserDTO(userId, newUser.getName(), newUser.getAge());
        //這裡的any(UserUpdatedRequestDTO.class)不能用eq(newUser)替換，因為如果mockMvc去模擬的回傳json格式會經過＠RequestBody的
        //一項反序列化deserialize操作，成爲新的java物件，所以在when規定裡的eq(newUser)比較地址時就會用測試裡宣告的newUser
        //比較模擬時新的物件地址，導致測試失敗
        when(userService.updateUser(eq(userId), any(UserUpdatedRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
                // .andExpect(jsonPath("$.id").value(updated.getId()))
                // .andExpect(jsonPath("$.name").value(updated.getName()))
                // .andExpect(jsonPath("$.age").value(updated.getAge()));
    }

    @Test
    void deleteUserTest() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(eq(userId));

        mockMvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test 
    void getAllUsersTest() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "john", 33);

        when(userService.getAllUsers()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDTO))));
    }

    @Test 
    void getUserByIdTest() throws Exception {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO(userId, "john", 32);

        when(userService.getUserById(userId)).thenReturn(userDTO);

        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    void loginTest() throws Exception {
        LoginRequestDTO credential = new LoginRequestDTO("testName", "testPassword");
        LoginResponseDTO responseDTO = new LoginResponseDTO("fake-jwt-token", 1L);
        when(userService.login(any(LoginRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credential)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDTO)));
    }           //直接用物件去比對，注意content.contentType(objectMapper...)這是ＭＩＭＥtype -> application/json 這種
                //.andExpect(jsonPath("$.userId").value(responseDTO.getUserId()));
                //.andExpect(jsonPath("$.token").value(responseDTO.getToken()))
}
