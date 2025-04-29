package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.MuscleGroupTotalDTO;
import com.example.demo.dto.PopularTypesDTO;
import com.example.demo.dto.WeeklyFrequencyDTO;
import com.example.demo.dto.WeeklySummaryDTO;
import com.example.demo.dto.WorkoutProgressDTO;
import com.example.demo.dto.WorkoutSessionDTO;
import com.example.demo.dto.WorkoutSessionRequestDTO;
import com.example.demo.dto.WorkoutSessionSummaryDTO;
import com.example.demo.dto.WorkoutSetRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutTypeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class WorkoutSessionServiceTest {
    
    @InjectMocks
    private WorkoutSessionService workoutSessionService;

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutTypeRepository workoutTypeRepository;
    
    @Test
    void addSession_succeed_test() {
        Long userId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
        
        WorkoutSetRequestDTO setDTO = new WorkoutSetRequestDTO(1L, 10, 80);

        WorkoutSessionRequestDTO requestDTO = new WorkoutSessionRequestDTO("2025-04-28", "testNote", null);
        requestDTO.setSets(List.of(setDTO));

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));

        workoutSessionService.addSession(userId, requestDTO);
        
        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).save(any(WorkoutSession.class));
    }
    
    @Test
    void getAllSession_succeed_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;

        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSet set = new WorkoutSet(1L, 8, 80);
        set.setId(setId);
        
        WorkoutSession session = new WorkoutSession("2025-04-29", "Leg");
        session.setId(sessionId);
        session.setUser(user);
        session.setSets(List.of(set));

        WorkoutType workoutType = new WorkoutType("Chest", "Bench Press");
        workoutType.setId(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(user.getId())).thenReturn(List.of(session));
        when(workoutTypeRepository.findById(set.getTypeId())).thenReturn(Optional.of(workoutType));

        List<WorkoutSessionDTO> result = workoutSessionService.getAllSession(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session.getDate(), result.get(0).getDate());
        assertEquals(session.getNote(), result.get(0).getNote());
        assertEquals(session.getId(), result.get(0).getSessionId());
        assertEquals(workoutType.getName(), result.get(0).getSets().get(0).getType());
        assertEquals(set.getId(), result.get(0).getSets().get(0).getId());
        assertEquals(set.getReps(), result.get(0).getSets().get(0).getReps());
        assertEquals(set.getWeight(), result.get(0).getSets().get(0).getWeight());
        assertEquals(set.getId(), result.get(0).getSets().get(0).getId());

        verify(userRepository,times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).findByUser_Id(userId);
        verify(workoutTypeRepository, times(1)).findById(set.getTypeId());
    }

    @Test
    void deleteSession_succeed_test() {
        User user = new User("testName", 20, "testPassword");
        user.setId(1L);
        
        WorkoutSession session = new WorkoutSession("2025-04-29", "Leg");
        session.setId(1L);
        session.setUser(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByIdAndUser_Id(session.getId(), user.getId())).thenReturn(Optional.of(session));

        workoutSessionService.deleteSession(1L, 1L);

        verify(workoutSessionRepository, times(1)).delete(session);
    }

    @Test
    void getWorkoutSessionSummary_succeed_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSet set1 = new WorkoutSet(1L, 10, 100); // 10 * 100 = 1000
        WorkoutSet set2 = new WorkoutSet(1L, 5, 80);   // 5 * 80 = 400
    
        WorkoutSession session = new WorkoutSession("2025-04-29", "Chest day");
        session.setId(sessionId);
        session.setUser(user);
        session.setSets(List.of(set1, set2)); // 兩組set

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(user.getId())).thenReturn(List.of(session));

        List<WorkoutSessionSummaryDTO> result = workoutSessionService.getWorkoutSessionSummary(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session.getDate(), result.get(0).getDate());
        assertEquals(session.getNote(), result.get(0).getNote());
        assertEquals(session.getId(), result.get(0).getSessionId());
        assertEquals(1400, result.get(0).getTotalWeight());
        verify(userRepository,times(1)).findById(userId);
        verify(workoutSessionRepository,times(1)).findByUser_Id(userId);
    }

    @Test
    void getWeeklySummary_succeed_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSet set1 = new WorkoutSet(1L, 10, 100); // 10 * 100 = 1000
        WorkoutSet set2 = new WorkoutSet(1L, 5, 80);   // 5 * 80 = 400
    
        WorkoutSession session1 = new WorkoutSession("2025-04-29", "Chest day");
        session1.setId(sessionId);
        session1.setUser(user);
        session1.setSets(List.of(set1, set2)); // 兩組set
        
        WorkoutSession session2 = new WorkoutSession("2025-04-30", "Chest day");
        session2.setId(sessionId);
        session2.setUser(user);
        session2.setSets(List.of(set1, set2)); // 兩組set

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(userId)).thenReturn(List.of(session1, session2));
        List<WeeklySummaryDTO> result = workoutSessionService.getWeeklySummary(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2025-W18", result.get(0).getWeek());
        assertEquals(2800, result.get(0).getTotalWeight());
        verify(userRepository,times(1)).findById(userId);
        verify(workoutSessionRepository,times(1)).findByUser_Id(userId);
    }

    @Test
    void getWeeklyFrequency_succeed_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
    
        WorkoutSession session1 = new WorkoutSession("2025-04-29", "Chest day");
        session1.setId(sessionId);
        session1.setUser(user);
        
        WorkoutSession session2 = new WorkoutSession("2025-04-30", "Leg day");
        session2.setId(sessionId);
        session2.setUser(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(userId)).thenReturn(List.of(session1, session2));
        List<WeeklyFrequencyDTO> result = workoutSessionService.getWeeklyFrequency(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2025-W18", result.get(0).getWeekKey());
        assertEquals(2, result.get(0).getFrequency());
        verify(userRepository,times(1)).findById(userId);
        verify(workoutSessionRepository,times(1)).findByUser_Id(userId);
    }

    @Test
    void getTotalWeightsByMuscleGroup_succeed_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
        
        WorkoutSet set1 = new WorkoutSet(1L, 10, 100);
        WorkoutSet set2 = new WorkoutSet(2L, 5, 80);   
        
        WorkoutSession session1 = new WorkoutSession("2025-04-29", "Chest day");
        session1.setId(sessionId);
        session1.setUser(user);
        session1.setSets(List.of(set1));
        
        WorkoutSession session2 = new WorkoutSession("2025-04-30", "shoulder day");
        session2.setId(sessionId);
        session2.setUser(user);
        session2.setSets(List.of(set2));

        user.setWorkoutSessions(new ArrayList<>());
        user.setWorkoutSessions(List.of(session1, session2));
        
        set1.setSession(session1);
        set2.setSession(session2);
        
        WorkoutType chestType = new WorkoutType("胸", "臥推");
        chestType.setId(1L);
        WorkoutType shoulderType = new WorkoutType("肩", "肩推");
        shoulderType.setId(2L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutTypeRepository.findById(set1.getTypeId())).thenReturn(Optional.of(chestType));
        when(workoutTypeRepository.findById(set2.getTypeId())).thenReturn(Optional.of(shoulderType));

        List<MuscleGroupTotalDTO> result = workoutSessionService.getTotalWeightsByMuscleGroup(userId);

        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1000, result.get(0).getTotalWeight());
        assertEquals(chestType.getMuscleGroup(), result.get(0).getMuscleGroup());
        assertEquals(400, result.get(1).getTotalWeight());
        assertEquals(shoulderType.getMuscleGroup(), result.get(1).getMuscleGroup());
        verify(userRepository,times(1)).findById(userId);
        verify(workoutTypeRepository, times(1)).findById(set1.getTypeId());
        verify(workoutTypeRepository, times(1)).findById(set2.getTypeId());
    }

    @Test
    void getPopularWorkoutType_succeed_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
        
        WorkoutSet set1 = new WorkoutSet(1L, 10, 100);
        WorkoutSet set2 = new WorkoutSet(1L, 8, 110);   
        
        WorkoutSession session1 = new WorkoutSession("2025-04-29", "Chest day");
        session1.setId(sessionId);
        session1.setUser(user);
        session1.setSets(List.of(set1));
        
        WorkoutSession session2 = new WorkoutSession("2025-04-30", "Chest Power day");
        session2.setId(sessionId);
        session2.setUser(user);
        session2.setSets(List.of(set2));

        user.setWorkoutSessions(new ArrayList<>());
        user.setWorkoutSessions(List.of(session1, session2));
        
        set1.setSession(session1);
        set2.setSession(session2);
        
        WorkoutType chestType = new WorkoutType("胸", "臥推");
        chestType.setId(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(userId)).thenReturn(List.of(session1,session2));
        when(workoutTypeRepository.findById(set1.getTypeId())).thenReturn(Optional.of(chestType));

        List<PopularTypesDTO> result = workoutSessionService.getPopularWorkoutType(userId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chestType.getName(), result.get(0).getType());
        assertEquals(user.getWorkoutSessions().size(), result.get(0).getCount());
        verify(userRepository,times(1)).findById(userId);
        verify(workoutSessionRepository,times(1)).findByUser_Id(userId);
        verify(workoutTypeRepository, times(2)).findById(set1.getTypeId());
    }
    
    @Test
    void getWorkoutProgressByType_succeed_test() {
        Long userId = 1L;
        Long typeId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
        
        WorkoutSet set1 = new WorkoutSet(1L, 10, 100);
        WorkoutSet set2 = new WorkoutSet(1L, 8, 110);   
        
        //因為直接從session資料庫找對應的session所以不用綁定user，不用寫user.setWorkoutSessions...和session1.setUser(user)...這些建立雙方關聯內容也找得到資料
        WorkoutSession session1 = new WorkoutSession("2025-04-29", "Chest day");
        //但是set因爲是使用getSets所以要建立設定的關係不然找不到資料
        session1.setSets(List.of(set1));
        
        WorkoutSession session2 = new WorkoutSession("2025-04-30", "Chest Power day");
        session2.setSets(List.of(set2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(userId)).thenReturn(List.of(session1,session2));

        List<WorkoutProgressDTO> result = workoutSessionService.getWorkoutProgressByType(userId, typeId);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(session1.getDate(), result.get(0).getDate());
        assertEquals(1000, result.get(0).getTotalWeight());
        verify(userRepository,times(1)).findById(userId);
        verify(workoutSessionRepository,times(1)).findByUser_Id(userId);
    }

    //reverse testing part

    @Test
    void getAllSession_user_not_found_test() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows( ApiException.class, () ->
            workoutSessionService.getAllSession(userId));

        assertEquals(ApiErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(workoutSessionRepository); // user都找不到，就不該去查session
        verifyNoInteractions(workoutTypeRepository);    // 更不會查type
    }
    
    @Test
    void getAllSession_type_not_found_test() {
        Long userId = 1L;
        Long setId = 1L;

        User user = new User("testName", 20, "testPassword");
        user.setId(userId);

        WorkoutSet set = new WorkoutSet(1L, 8, 80);
        set.setId(setId);
        
        WorkoutSession session = new WorkoutSession("2025-04-29", "Leg");
        session.setSets(List.of(set));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(user.getId())).thenReturn(List.of(session));
        when(workoutTypeRepository.findById(set.getTypeId())).thenReturn(Optional.empty());

        ApiException exception = assertThrows( ApiException.class, () ->
            workoutSessionService.getAllSession(userId));

        assertEquals(ApiErrorCode.TYPE_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository,times(1)).findByUser_Id(user.getId());    
        verify(workoutTypeRepository,times(1)).findById(set.getTypeId());    
    }

    @Test
    void getPopularWorkoutType_user_not_found_test() {
        Long userId = 1L;
    
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
    
        ApiException exception = assertThrows(ApiException.class, () -> {
            workoutSessionService.getPopularWorkoutType(userId);
        });
    
        assertEquals(ApiErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(workoutSessionRepository);
        verifyNoInteractions(workoutTypeRepository);
    }

    @Test
    void getPopularWorkoutType_type_not_found_test() {
        Long userId = 1L;
        Long sessionId = 1L;
        Long setId = 1L;
    
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
    
        WorkoutSet set = new WorkoutSet(1L, 10, 100);
        set.setId(setId);
    
        WorkoutSession session = new WorkoutSession("2025-04-29", "Chest day");
        session.setId(sessionId);
        session.setUser(user);
        session.setSets(List.of(set));
    
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUser_Id(userId)).thenReturn(List.of(session));
        when(workoutTypeRepository.findById(set.getTypeId())).thenReturn(Optional.empty());
    
        ApiException exception = assertThrows(ApiException.class, () -> {
            workoutSessionService.getPopularWorkoutType(userId);
        });
    
        assertEquals(ApiErrorCode.TYPE_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    
        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).findByUser_Id(userId);
        verify(workoutTypeRepository, times(1)).findById(set.getTypeId());
    }
    
}
