package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateTemplateRequestDTO;
import com.example.demo.dto.ExerciseDetailDTO;
import com.example.demo.dto.ExerciseSummaryDTO;
import com.example.demo.dto.SetDetailDTO;
import com.example.demo.dto.TemplateDetailDTO;
import com.example.demo.dto.TemplateSummaryDTO;
import com.example.demo.entity.TemplateExercise;
import com.example.demo.entity.TemplateSet;
import com.example.demo.entity.User;
import com.example.demo.entity.TemplateSession;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.TemplateSessionRepository;
import com.example.demo.repository.WorkoutTypeRepository;

@Service
public class TemplateSessionService {

    private final TemplateSessionRepository templateSessionRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final UserRepository userRepository;

    public TemplateSessionService(
        TemplateSessionRepository templateSessionRepository,
        WorkoutTypeRepository workoutTypeRepository,
        UserRepository userRepository) {
        this.workoutTypeRepository = workoutTypeRepository;
        this.templateSessionRepository = templateSessionRepository;
        this.userRepository = userRepository;
    }

    public List<TemplateSummaryDTO> getTemplateSummariesByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "沒有找到使用者", HttpStatus.NOT_FOUND));

        return templateSessionRepository.findByUserIdOrderByPlannedDateDesc(userId).stream().map(template -> {
            List<ExerciseSummaryDTO> exerciseSummaryDTOs = template.getExercises().stream()
                .map(exercise -> new ExerciseSummaryDTO(
                    exercise.getWorkoutType().getId(),
                    exercise.getWorkoutType().getName(),
                    exercise.getSets().size()
                )).collect(Collectors.toList());

            List<String> topTags = template.getExercises().stream()
                .map(ex -> ex.getWorkoutType().getMainTag())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue())) // 出現次數高的排前面
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());

            return new TemplateSummaryDTO(
                template.getId(),
                template.getTitle(),
                template.getPlannedDate(),
                exerciseSummaryDTOs,
                topTags
            );
        }).collect(Collectors.toList());
    }


    public void createTemplate(Long userId, CreateTemplateRequestDTO templateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "沒有找到使用者", HttpStatus.NOT_FOUND));

        TemplateSession template = new TemplateSession(templateDto.getTitle(), templateDto.getPlannedDate());
        template.setUser(user);
        
        List<TemplateExercise> exercises = templateDto.getExercises().stream().map(exerciseDTO -> {
            WorkoutType type = workoutTypeRepository.findById(exerciseDTO.getTypeId()).orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到動作", HttpStatus.NOT_FOUND));

            TemplateExercise exercise = new TemplateExercise(type);
            exercise.setWorkoutTemplate(template);
            List<TemplateSet> sets = exerciseDTO.getSets().stream().map(setDto -> {
                TemplateSet set = new TemplateSet(setDto.getReps(), setDto.getWeight());
                set.setExercise(exercise);
                return set;
            }).collect(Collectors.toList());

            exercise.setSets(sets); 
            return exercise;
        }).collect(Collectors.toList());

        template.setExercises(exercises);   

        // 5. 儲存整張 template（會 cascade 儲存所有 exercise 和 set）
        templateSessionRepository.save(template);
    }

    public TemplateDetailDTO getTemplateDetail(Long templateId) {
        TemplateSession template = templateSessionRepository.findById(templateId)
            .orElseThrow(() -> new ApiException(ApiErrorCode.TEMPLATE_NOT_FOUND, "找不到訓練卡片", HttpStatus.NOT_FOUND));

        List<ExerciseDetailDTO> exercises = template.getExercises().stream().map(ex -> {
            Long typeId = ex.getWorkoutType().getId();
            String typeName = ex.getWorkoutType().getName();
            String mainTag = ex.getWorkoutType().getMainTag();

            List<SetDetailDTO> sets = ex.getSets().stream()
                .map(set -> new SetDetailDTO(set.getReps(), set.getWeight()))
                .collect(Collectors.toList());

            return new ExerciseDetailDTO(typeId, typeName, mainTag, sets);
        }).collect(Collectors.toList());

        return new TemplateDetailDTO(
            template.getId(),
            template.getTitle(),
            template.getPlannedDate(),
            exercises
        );
    }

    public void deleteTemplate(Long id) {
        TemplateSession templateSession = templateSessionRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiErrorCode.TEMPLATE_NOT_FOUND, "找不到該訓練卡片", HttpStatus.NOT_FOUND));
    
        templateSessionRepository.delete(templateSession); // Cascade + OrphanRemoval 將會自動處理下層資料
    }

    public void updateTemplate(Long templateId, CreateTemplateRequestDTO dto) {
        // 1. 找到原本的 template
        TemplateSession template = templateSessionRepository.findById(templateId)
            .orElseThrow(() -> new ApiException(ApiErrorCode.TEMPLATE_NOT_FOUND, "找不到該訓練卡片", HttpStatus.NOT_FOUND));
    
        // 2. 更新基本欄位
        template.setTitle(dto.getTitle());
        template.setPlannedDate(dto.getPlannedDate());
    
        // 3. 先清空舊的 exercise（包含 set）→ 利用 cascade + orphanRemoval 自動刪除
        template.getExercises().clear();
    
        // 4. 重新建立新的 exercise / set 結構
        List<TemplateExercise> newExercises = dto.getExercises().stream().map(exerciseDTO -> {
            WorkoutType type = workoutTypeRepository.findById(exerciseDTO.getTypeId()).orElseThrow(() -> new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到指定動作", HttpStatus.BAD_REQUEST));

            TemplateExercise exercise = new TemplateExercise(type);
            List<TemplateSet> sets = exerciseDTO.getSets().stream().map(setDTO -> {
                TemplateSet set = new TemplateSet(setDTO.getReps(), setDTO.getWeight());
                set.setExercise(exercise);
                return set;
            }).collect(Collectors.toList());
    
            exercise.setSets(sets);
            exercise.setWorkoutTemplate(template);
            return exercise;
        }).collect(Collectors.toList());
    
        for(TemplateExercise exercise:newExercises){
            template.getExercises().add(exercise);
        }
        // template.setExercises(newExercises);
     
        // 5. 儲存（因為已是持久化物件，會自動追蹤變化）
        templateSessionRepository.save(template);
    }
    
}

