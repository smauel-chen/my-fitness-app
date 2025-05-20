package com.example.demo.service;

import org.checkerframework.checker.units.qual.t;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;


import com.example.demo.dto.WorkoutAllSessionsDTO;
import com.example.demo.dto.CreateExerciseDTO;
import com.example.demo.dto.CreateSessionRequestDTO;
import com.example.demo.dto.CreateSetDTO;
import com.example.demo.dto.SessionDetailDTO;
import com.example.demo.dto.VolumeProgressDTO;
import com.example.demo.dto.WeightTrendDTO;
import com.example.demo.entity.BodyWeightRecord;
import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutExercise;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.BodyWeightRecordRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    @Mock
    private BodyWeightRecordRepository bodyWeightRecordRepository;

    //不應該寫在這裡但是我懶惰了，把bodyweight相關的內容寫在這邊，然後就不寫bodyweight test了嘿嘿
    @InjectMocks
    private BodyWeightRecordService bodyWeightRecordService;
    
    @Test
    void addSession_succeed_test() {
        Long userId = 1L;
        User user = new User("testName", 20, "testPassword");
        user.setId(userId);
        WorkoutType type = new WorkoutType(); type.setId(100L);

        // DTO 設定
        CreateSetDTO setDto = new CreateSetDTO(8, 50);
        CreateExerciseDTO exDto = new CreateExerciseDTO(100L, List.of(setDto));
        CreateSessionRequestDTO dto = new CreateSessionRequestDTO("Test Title", LocalDate.of(2025, 5, 20), List.of(exDto));

        // Repository mock 行為
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutTypeRepository.findById(100L)).thenReturn(Optional.of(type));

        // 執行
        workoutSessionService.addSession(userId, dto);

        // 驗證是否呼叫 save
        verify(workoutSessionRepository, times(1)).save(any(WorkoutSession.class));
        verify(userRepository, times(1)).findById(userId);
        verify(workoutTypeRepository, times(1)).findById(100L);
    }
    
    @Test
    void getAllSession_succeed_test() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);
    
        WorkoutType type1 = new WorkoutType(); type1.setMainTag("胸部");
        WorkoutType type2 = new WorkoutType(); type2.setMainTag("三頭肌");
    
        WorkoutExercise ex1 = new WorkoutExercise(type1);
        WorkoutExercise ex2 = new WorkoutExercise(type2);
    
        WorkoutSession s1 = new WorkoutSession(); 
        s1.setId(100L); 
        s1.setTitle("胸推訓練"); 
        s1.setDate(LocalDate.of(2025, 5, 20)); 
        s1.setExercises(List.of(ex1, ex2));
    
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findByUserIdOrderByDateDesc(userId)).thenReturn(List.of(s1));
    
        List<WorkoutAllSessionsDTO> result = workoutSessionService.getAllSession(userId);
    
        assertEquals(1, result.size());
        assertEquals("胸推訓練", result.get(0).getTitle());
        assertEquals(List.of("胸部", "三頭肌"), result.get(0).getMainTags());
    }

    @Test
    void getWorkoutSessionDetailBySessionId_succeed_test() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);
    
        WorkoutType type = new WorkoutType(); type.setMainTag("胸部");
        WorkoutExercise ex = new WorkoutExercise(type);
        WorkoutSet set = new WorkoutSet(8, 50);

        ex.setSets(List.of(set));
    
        WorkoutSession session = new WorkoutSession(); 
        session.setId(100L); 
        session.setTitle("胸推訓練"); 
        session.setDate(LocalDate.of(2025, 5, 20)); 
        session.setExercises(List.of(ex));

        ex.setSession(session);
    
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        SessionDetailDTO result = workoutSessionService.getSessionDetailById(userId, session.getId());

        assertEquals(session.getId(), result.getId());
        assertEquals(session.getDate(), result.getDate());
        assertEquals(session.getTitle(), result.getTitle());
        assertEquals(type.getMainTag(), result.getExercises().get(0).getMainTag());

        verify(userRepository, times(1)).findById(userId);
        verify(workoutSessionRepository, times(1)).findById(session.getId());


    }

    @Test
    void getTagFrequency_shouldReturnTagCounts_whenPeriodIsWeekly() {
        Long userId = 1L;

        WorkoutType type1 = new WorkoutType(); type1.setMainTag("胸部");
        WorkoutType type2 = new WorkoutType(); type2.setMainTag("背部");

        WorkoutExercise ex1 = new WorkoutExercise(type1);
        WorkoutExercise ex2 = new WorkoutExercise(type2);
        WorkoutExercise ex3 = new WorkoutExercise(type1); // 第二筆胸部

        WorkoutSession s1 = new WorkoutSession(); 
        s1.setDate(LocalDate.now().minusDays(2));
        s1.setExercises(List.of(ex1, ex2));

        WorkoutSession s2 = new WorkoutSession(); 
        s2.setDate(LocalDate.now().minusDays(1));
        s2.setExercises(List.of(ex3));

        when(workoutSessionRepository.findByUserIdAndDateAfter(eq(userId), any(LocalDate.class)))
                .thenReturn(List.of(s1, s2));

        // Act
        Map<String, Long> result = workoutSessionService.getTagFrequency(userId, "weekly");

        // Assert
        assertEquals(2, result.size());
        assertEquals(2L, result.get("胸部"));
        assertEquals(1L, result.get("背部"));
    }

    @Test
    void getVolumeProgress_shouldReturnGroupedVolumeByDate_whenTypeExists() {
        Long userId = 1L;
        Long typeId = 3L;
    
        User user = new User(); user.setId(userId);
    
        WorkoutType type = new WorkoutType(); type.setId(typeId);
    
        WorkoutSet set1 = new WorkoutSet(10, 50); // 500
        WorkoutSet set2 = new WorkoutSet(10, 50); // 500
        WorkoutSet set3 = new WorkoutSet(8, 60);  // 480
    
        WorkoutExercise ex1 = new WorkoutExercise(type); ex1.setSets(List.of(set1, set2));
        WorkoutExercise ex2 = new WorkoutExercise(type); ex2.setSets(List.of(set3));
    
        WorkoutSession s1 = new WorkoutSession(); 
        s1.setDate(LocalDate.of(2025, 5, 18)); 
        s1.setExercises(List.of(ex1));
    
        WorkoutSession s2 = new WorkoutSession(); 
        s2.setDate(LocalDate.of(2025, 5, 19)); 
        s2.setExercises(List.of(ex2));
    
        // 注意！要用降序（新到舊）
        when(workoutSessionRepository.findByUserIdOrderByDateDesc(userId))
                .thenReturn(List.of(s2, s1));
        when(workoutTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
    
        List<VolumeProgressDTO> result = workoutSessionService.getVolumeProgress(userId, typeId, 5);
    
        // 應該被 reverse → 回傳順序：s1 (5/18), s2 (5/19)
        assertNotNull(result);
        assertEquals(2, result.size());
    
        assertEquals(s1.getDate(), result.get(0).getDate());
        assertEquals(1000, result.get(0).getVolume());
    
        assertEquals(s2.getDate(), result.get(1).getDate());
        assertEquals(480, result.get(1).getVolume());
    }
    
    @Test
    void getMuscleBalanceRadarData_shouldReturnGroupedVolumeByTag() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        WorkoutType type1 = new WorkoutType(); type1.setMainTag("胸部");
        WorkoutType type2 = new WorkoutType(); type2.setMainTag("背部");

        WorkoutSet set1 = new WorkoutSet();
        WorkoutSet set2 = new WorkoutSet();  

        WorkoutExercise ex1 = new WorkoutExercise(type1); ex1.setSets(List.of(set1));
        WorkoutExercise ex2 = new WorkoutExercise(type2); ex2.setSets(List.of(set2));

        WorkoutSession s1 = new WorkoutSession();
        s1.setDate(LocalDate.now().minusDays(1));
        s1.setExercises(List.of(ex1, ex2));

        when(workoutSessionRepository.findByUserIdAndDateAfter(eq(userId), any(LocalDate.class)))
                .thenReturn(List.of(s1));

        Map<String, Long> result = workoutSessionService.getMuscleGroupBalance(userId, "weekly");

        assertEquals(2, result.size());
        assertEquals(1, result.get("胸部"));
        assertEquals(1, result.get("背部"));
    }


    @Test
    void getBodyWeightTrend_shouldReturnWeightsGroupedByDate() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        BodyWeightRecord w1 = new BodyWeightRecord(LocalDate.of(2025, 5, 18), 71.5, user);
        BodyWeightRecord w2 = new BodyWeightRecord(LocalDate.of(2025, 5, 19), 71.2, user);

        when(bodyWeightRecordRepository.findByUserIdAndDateAfterOrderByDateAsc(eq(userId),any(LocalDate.class))).thenReturn(List.of(w1, w2));

        List<WeightTrendDTO> result = bodyWeightRecordService.getWeightTrend(userId, "weekly");

        assertEquals(2, result.size());
        assertEquals(w1.getDate(), result.get(0).getDate());
        assertEquals(71.5, result.get(0).getWeight());
        assertEquals(w2.getWeight(), result.get(1).getWeight());
    }

    // //reverse testing part
    @Test
    void addSession_shouldThrow_whenUserNotFound() {
        Long userId = 1L;
        CreateSessionRequestDTO dto = new CreateSessionRequestDTO("Test", LocalDate.now(), List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> workoutSessionService.addSession(userId, dto));
    }

    @Test
    void addSession_shouldThrow_whenWorkoutTypeNotFound() {
        Long userId = 1L;
        CreateSetDTO setDto = new CreateSetDTO(8, 50);
        CreateExerciseDTO exDto = new CreateExerciseDTO(999L, List.of(setDto));
        CreateSessionRequestDTO dto = new CreateSessionRequestDTO("Test", LocalDate.now(), List.of(exDto));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(workoutTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> workoutSessionService.addSession(userId, dto));
    }

    @Test
    void addSession_shouldThrow_whenSetMissingFields() {
        Long userId = 1L;
        CreateSetDTO invalidSet = new CreateSetDTO(null, 50);
        CreateExerciseDTO exDto = new CreateExerciseDTO(100L, List.of(invalidSet));
        CreateSessionRequestDTO dto = new CreateSessionRequestDTO("Test", LocalDate.now(), List.of(exDto));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(workoutTypeRepository.findById(100L)).thenReturn(Optional.of(new WorkoutType()));

        assertThrows(ApiException.class, () -> workoutSessionService.addSession(userId, dto));
        verifyNoInteractions(workoutSessionRepository);
    }

    @Test
    void getSessionDetail_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> workoutSessionService.getSessionDetailById(1L, 100L));
        verifyNoInteractions(workoutSessionRepository);
    }

    @Test
    void getTagFrequency_shouldThrow_whenPeriodInvalid() {
        Long userId = 1L;
    
        ApiException ex = assertThrows(ApiException.class, () ->
                workoutSessionService.getTagFrequency(userId, "invalid-period"));
    
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(ApiErrorCode.INVALID_REQUEST, ex.getErrorCode());
    }
    
}
