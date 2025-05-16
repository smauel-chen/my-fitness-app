package com.example.demo.service;

import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateSessionRequestDTO;
import com.example.demo.dto.ExerciseDetailDTO;
import com.example.demo.dto.SessionDetailDTO;
import com.example.demo.dto.SetDetailDTO;
import com.example.demo.dto.VolumeProgressDTO;
import com.example.demo.dto.WorkoutAllSessionsDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutExercise;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.entity.TemplateSession;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.TemplateSessionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutTypeRepository;

@Service
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final TemplateSessionRepository templateSessionRepository;

    public WorkoutSessionService(
        WorkoutSessionRepository workoutSessionRepository,
        UserRepository userRepository,
        WorkoutTypeRepository workoutTypeRepository,
        TemplateSessionRepository templateSessionRepository
    ) {
        this.userRepository = userRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutTypeRepository = workoutTypeRepository;
        this.templateSessionRepository = templateSessionRepository;
    }

    public void addSession(Long id, CreateSessionRequestDTO dto){
        
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ id, HttpStatus.NOT_FOUND));

        WorkoutSession session = new WorkoutSession(dto.getDate(), dto.getTitle());
        session.setUser(user);
        
        List<WorkoutExercise> exercises = dto.getExercises().stream().map(exDto -> {
            WorkoutType type = workoutTypeRepository.findById(exDto.getTypeId()).orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND,"沒有這個動作" + exDto.getTypeId(), HttpStatus.NOT_FOUND));
    
            WorkoutExercise ex = new WorkoutExercise(type);
            ex.setSession(session);
    
            List<WorkoutSet> sets = exDto.getSets().stream().map(setDto -> {
                if(setDto.getReps() == null || setDto.getWeight() == null){
                    throw new ApiException(ApiErrorCode.BAD_REQUEST, "缺少訓練組內容",  HttpStatus.BAD_REQUEST);
                }
                WorkoutSet set = new WorkoutSet(setDto.getReps(), setDto.getWeight());
                set.setExercise(ex);
                return set;
            }).collect(Collectors.toList());
    
            ex.setSets(sets);
            return ex;
        }).collect(Collectors.toList());

        session.setExercises(exercises);
        workoutSessionRepository.save(session);
    }

    public List<Long> getAllSessionIds(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者", HttpStatus.NOT_FOUND));
    
        List<Long> sessionIds = workoutSessionRepository.findByUser_Id(userId)
            .stream()
            .map(WorkoutSession::getId)
            .collect(Collectors.toList());
        return sessionIds;
    }


    public List<WorkoutAllSessionsDTO> getAllSession(Long id){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ id, HttpStatus.NOT_FOUND));

        List<WorkoutAllSessionsDTO> sessionDTOs =  workoutSessionRepository.findByUser_Id(id).stream().map(session -> {
            List<String> mainTags = session.getExercises().stream()
                .map(ex ->  ex.getWorkoutType().getMainTag())
                .filter(Objects::nonNull)
                .distinct()
                .limit(3) // 最多顯示三個主 tag
                .collect(Collectors.toList());
    
            return new WorkoutAllSessionsDTO(
                session.getId(),
                session.getTitle(),       
                session.getDate(),
                mainTags
            );
        }).collect(Collectors.toList());

        return sessionDTOs;
    }

    public SessionDetailDTO getSessionDetailById(Long userId, Long sessionId) {
        userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ userId, HttpStatus.NOT_FOUND));

        WorkoutSession session = workoutSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "找不到紀錄", HttpStatus.NOT_FOUND));

            List<ExerciseDetailDTO> exercises = session.getExercises().stream().map(ex -> {
                WorkoutType wt = ex.getWorkoutType();
            
                List<SetDetailDTO> sets = ex.getSets().stream()
                    .map(set -> new SetDetailDTO(set.getReps(), set.getWeight()))
                    .collect(Collectors.toList());
            
                return new ExerciseDetailDTO(
                    wt.getName(),
                    wt.getMainTag(),
                    wt.getSecondaryTags(),  // 直接從 entity 取出 secondaryTags
                    sets
                );
            }).collect(Collectors.toList());
            
        return new SessionDetailDTO(
            session.getId(),
            session.getTitle(),      // 或 session.getTitle() 若你改名過
            session.getDate(),
            exercises
        );
    }

    public void updateSession(Long userId, Long sessionId, CreateSessionRequestDTO dto) {
        userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ userId, HttpStatus.NOT_FOUND));

        WorkoutSession session = workoutSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "找不到紀錄", HttpStatus.NOT_FOUND));
    
        // 更新基本欄位
        session.setDate(dto.getDate());
        session.setNote(dto.getTitle());
    
        // 移除原本的 exercise（包含 set）
        session.getExercises().clear();
    
        List<WorkoutExercise> newExercises = dto.getExercises().stream().map(exDto -> {
            WorkoutType type = workoutTypeRepository.findById(exDto.getTypeId()).orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到動作", HttpStatus.NOT_FOUND));
    
            WorkoutExercise ex = new WorkoutExercise(type);
            ex.setSession(session);
    
            List<WorkoutSet> sets = exDto.getSets().stream().map(setDto -> {
                WorkoutSet set = new WorkoutSet(setDto.getReps(), setDto.getWeight());
                set.setExercise(ex);
                return set;
            }).collect(Collectors.toList());
    
            ex.setSets(sets);
            return ex;
        }).collect(Collectors.toList());
    
        session.setExercises(newExercises);
    
        // 不需要 save，因為 JPA 已追蹤實體
    }
    
    public void deleteSession(Long id, Long sessionId){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, id:" + id, HttpStatus.NOT_FOUND));
        WorkoutSession session = workoutSessionRepository.findByIdAndUser_Id(sessionId, id).orElseThrow(() -> 
            new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "課表不存在 Id:" + sessionId, HttpStatus.NOT_FOUND)
        );
        workoutSessionRepository.delete(session);
    }

    //新的內容
    //處理訓練紀錄頁面重構時回頭來做這個api
    public Long createFromTemplate(Long userId, Long templateId) {
        // 1. 取得 user 與 template
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在", HttpStatus.NOT_FOUND));

        TemplateSession templateSession = templateSessionRepository.findById(templateId).orElseThrow(() -> new ApiException(ApiErrorCode.TEMPLATE_NOT_FOUND, "訓練模板不存在", HttpStatus.NOT_FOUND));

        // 2. 建立 session 並設定使用者與時間（以現在為訓練時間）
        WorkoutSession session = new WorkoutSession(LocalDate.now(), templateSession.getTitle());
        session.setUser(user);

        List<WorkoutExercise> exercises = templateSession.getExercises().stream().map(templateEx -> {
            WorkoutType type = templateEx.getWorkoutType();
    
            WorkoutExercise exercise = new WorkoutExercise(type);
            exercise.setSession(session);
    
            List<WorkoutSet> sets = templateEx.getSets().stream().map(templateSet -> {
                WorkoutSet set = new WorkoutSet(templateSet.getReps(), templateSet.getWeight());
                set.setExercise(exercise);
                return set;
            }).collect(Collectors.toList());
    
            exercise.setSets(sets);
            return exercise;
        }).collect(Collectors.toList());
    
        session.setExercises(exercises);
        workoutSessionRepository.save(session);
    
        return session.getId();
    }

    //chart1
    public Map<String, Long> getTagFrequency(Long userId, String period) {
        LocalDate start = switch (period) {
            case "weekly" -> LocalDate.now().minusDays(6);
            case "two-weeks" -> LocalDate.now().minusDays(13);
            case "monthly" -> LocalDate.now().minusDays(29);
            default -> throw new ApiException(
                ApiErrorCode.INVALID_REQUEST, // ← 如果你已有這類錯誤代碼
                "不合法的期間參數：" + period,
                HttpStatus.BAD_REQUEST
            );
        };
    
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserIdAndDateAfter(userId, start);
    
        return sessions.stream()
            .flatMap(session -> session.getExercises().stream())
            .map(ex -> ex.getWorkoutType().getMainTag())
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    //chart2
    public Map<String, Long> getMuscleGroupBalance(Long userId, String period) {
        LocalDate start = switch (period) {
            case "weekly" -> LocalDate.now().minusDays(6);
            case "two-weeks" -> LocalDate.now().minusDays(13);
            case "monthly" -> LocalDate.now().minusDays(29);
            default -> throw new ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "不合法的時間區間：" + period,
                HttpStatus.BAD_REQUEST
            );
        };
    
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserIdAndDateAfter(userId, start);
    
        return sessions.stream()
            .flatMap(session -> session.getExercises().stream())
            .flatMap(ex -> {
                WorkoutType type = ex.getWorkoutType();
                String mainTag = type.getMainTag();
                return ex.getSets().stream().map(set -> mainTag); // 每組對應一個 mainTag
            })
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    //chart3
    public List<VolumeProgressDTO> getVolumeProgress(Long userId, Long typeId, int count) {
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserIdOrderByDateDesc(userId);

        List<VolumeProgressDTO> all = sessions.stream()
            .map(session -> {
                int volume = session.getExercises().stream()
                    .filter(ex -> ex.getWorkoutType().getId().equals(typeId))
                    .flatMap(ex -> ex.getSets().stream())
                    .mapToInt(set -> set.getReps() * set.getWeight())
                    .sum();
                
                return new VolumeProgressDTO(session.getDate(), volume);
            })
            .filter(dto -> dto.getVolume() > 0)
            .limit(count)
            .toList();

        // 往前抓是由新到舊，但圖表要由舊到新
        Collections.reverse(all);

        return all;
    }
}
