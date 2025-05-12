package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.WorkoutSetEditDTO;
import com.example.demo.dto.WorkoutSetRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutSetRepository;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetServiceTest {
    @InjectMocks
    private WorkoutSetService workoutSetService;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock 
    private UserRepository userRepository;

    @Test
    void addSet_succeed_Test() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSession session = new WorkoutSession("2025-05-01", "Chest Day");
        session.setId(sessionId);
        session.setUser(user);

        WorkoutSetRequestDTO setRequestDTO = new WorkoutSetRequestDTO(1L, 8, 80);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByIdAndUser_Id(sessionId, userId)).thenReturn(Optional.of(session));

        workoutSetService.addSet(userId, sessionId, setRequestDTO);

        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).findByIdAndUser_Id(sessionId, userId);
        verify(workoutSetRepository,times(1)).save(any(WorkoutSet.class));
    }

    @Test
    void deleteSet_succeed_test(){
        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSession session = new WorkoutSession("2025-05-01", "Chest Day");
        session.setId(sessionId);
        session.setUser(user);
        
        WorkoutSet set = new WorkoutSet(1L, 8, 80);
        set.setId(setId);
        set.setSession(session);

        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByIdAndUser_Id(sessionId, userId)).thenReturn(Optional.of(session));
        when(workoutSetRepository.findBySessionIdAndId(sessionId, setId)).thenReturn(Optional.of(set));

        workoutSetService.deleteSet(userId, sessionId, setId);

        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).findByIdAndUser_Id(sessionId, userId);
        verify(workoutSetRepository,times(1)).findBySessionIdAndId(sessionId, setId);
        verify(workoutSetRepository,times(1)).delete(set);
    }

    @Test 
    void updateSet_succeed_test(){
        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSession session = new WorkoutSession("2025-05-01", "Chest Day");
        session.setId(sessionId);
        session.setUser(user);
        
        WorkoutSet set = new WorkoutSet(1L, 8, 80);
        set.setId(setId);
        set.setSession(session);

        WorkoutSetEditDTO newSet = new WorkoutSetEditDTO(8, 80);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByIdAndUser_Id(sessionId, userId)).thenReturn(Optional.of(session));
        when(workoutSetRepository.findById(setId)).thenReturn(Optional.of(set));

        workoutSetService.updateSet(userId, sessionId, setId, newSet);

        assertEquals(set.getReps(), newSet.getReps());
        assertEquals(set.getWeight(), newSet.getWeight());

        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).findByIdAndUser_Id(sessionId, userId);
        verify(workoutSetRepository,times(1)).save(set);
    }

    //reverse testing part

    @Test
    void updateSet_user_not_found_test() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> workoutSetService.updateSet(userId, 1L, 1L, null));

        assertEquals(ApiErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        
    } 

    @Test
    void updateSet_session_not_found() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByIdAndUser_Id(userId, sessionId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> workoutSetService.updateSet(userId, sessionId, 1L, null));

        assertEquals(ApiErrorCode.SESSION_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void updateSet_set_not_found_test(){
        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSession session = new WorkoutSession("2025-05-01", "Chest Day");
        session.setId(sessionId);
        session.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByIdAndUser_Id(userId, sessionId)).thenReturn(Optional.of(session));
        when(workoutSetRepository.findById(setId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> workoutSetService.updateSet(userId, sessionId, 1L, null));

        assertEquals(ApiErrorCode.SET_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }



}
