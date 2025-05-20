package com.example.demo.service;

import com.example.demo.dto.EnrollRequestDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdatedRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ApiErrorCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // ⭐ 模式A核心，啟動Mockito環境，不用SpringBoot
class UserServiceTest {

    @InjectMocks
    private UserService userService; // ⭐ 自動注入Mocks進UserService裡

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    //這裡等下會補上測試方法
    @Test
    void enrollUser_shouldSucceed_whenUserNameIsUnique() {
        // Arrange
        EnrollRequestDTO requestDTO = new EnrollRequestDTO("testUser", "testPassword", 20);

        // 模擬查詢不到同名帳號（代表可以註冊）
        when(userRepository.findByName(requestDTO.getName())).thenReturn(Optional.empty());

        // 模擬密碼加密成功
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedPassword");

        // Act
        userService.enrollUser(requestDTO);

        // Assert
        verify(userRepository, times(1)).save(any(User.class)); // 確認有呼叫 save 存進去
    }

    @Test
    void login_succeed() {
        //arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testUser", "testPassword");
        User user = new User("testUser", 20, "encodedPassword");
        user.setId(1L);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);

        when(userRepository.findByName(eq(loginRequestDTO.getName()))).thenReturn(Optional.of(user));
        
        //act
        LoginResponseDTO responseDTO = userService.login(loginRequestDTO);

        //assert
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getToken()); // 因為 JwtUtil.generateToken() 會產生一個字串，不需要真的驗證 token
        assertEquals(1L, responseDTO.getUserId());
    }

    @Test 
    void getAllUsers_succeed_test() {
        User user = new User("testName", 20, "testPassword");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDTO> result = userService.getAllUsers();
        
        //這個方法有回傳，所以重點在驗證回傳的正確性
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testName", result.get(0).getName());
        //如果沒有回傳，或是有外部依賴關係像是repository delete save重點就在驗證行為
        verify(userRepository, times(1)).findAll();
        
    }

    @Test
    void updateUser_succeed_test() {
        Long userId = 1L;
        UserUpdatedRequestDTO update = new UserUpdatedRequestDTO("testName", 20);
        User user = new User("userName", 19, "testPassword");
        user.setId(userId);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));

        UserDTO result = userService.updateUser(userId, update);
        
        verify(userRepository, times(1)).save(any(User.class));
        //檢查行為
        assertEquals(update.getName(), user.getName()); 
        assertEquals(update.getAge(), user.getAge());
        //檢查回傳
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(update.getName(), result.getName());
        assertEquals(update.getAge(), result.getAge());
    }

    @Test
    void deleteUser_succeed_test() {
        Long userId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void getUserById_succeed_test() {
        Long userId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(user.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getAge(), result.getAge());
        assertEquals(user.getId(), result.getId());
    }
    
    //reverse testing part
    @Test
    void updateUser_reverse_test() {
        Long userId = 1L;
        UserUpdatedRequestDTO update = new UserUpdatedRequestDTO("testName", 20);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.updateUser(userId, update);
        });

        assertEquals(ApiErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void enroll_reverse_test() {
        EnrollRequestDTO requestDTO = new EnrollRequestDTO("testUser", "testPassword", 20);
        //when().thenTrow()有回傳並且處理外部依賴的例外 <-> doThrow則是當回傳是void但又觸發例外時
        //when(userRepository.findById(eq(1L))).thenThrow(new DatabaseConnectionException());
        when(userRepository.findByName(requestDTO.getName())).thenReturn(Optional.of(new User(null, null, null)));

        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.enrollUser(requestDTO);
        });

        assertEquals(ApiErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }
}
