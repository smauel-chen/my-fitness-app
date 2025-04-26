package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WorkoutSetRequestDTO;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutSetRepository;

@Service
public class WorkoutSetService {

    private final UserRepository userRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionRepository workoutSessionRepository;

    WorkoutSetService(  
                        WorkoutSetRepository workoutSetRepository,
                        WorkoutSessionRepository workoutSessionRepository,
                        UserRepository userRepository){
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutSetRepository = workoutSetRepository; 
        this.userRepository = userRepository;
    }

    public void addSet(Long id, Long sessionId, WorkoutSetRequestDTO workoutSetRequestDTO){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ID:" + id, HttpStatus.NOT_FOUND));
        WorkoutSession session = workoutSessionRepository.findByIdAndUser_Id(sessionId, id).orElseThrow(() -> new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "課表不存在, SessionId:" + sessionId, HttpStatus.NOT_FOUND));
        WorkoutSet set = new WorkoutSet();
        set.setReps(workoutSetRequestDTO.getReps());
        set.setWeight(workoutSetRequestDTO.getWeight());
        set.setTypeId(workoutSetRequestDTO.getTypeId());
        set.setSession(session);
        workoutSetRepository.save(set);
    }

    public void deleteSet(Long id, Long sessionId, Long setId){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ID:" + id, HttpStatus.NOT_FOUND));
        workoutSessionRepository.findByIdAndUser_Id(sessionId, id).orElseThrow(() -> new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "課表不存在, SessionId:" + sessionId, HttpStatus.NOT_FOUND));
        WorkoutSet set = workoutSetRepository.findBySessionIdAndSetId(sessionId, setId).orElseThrow(() -> new ApiException(ApiErrorCode.SET_NOT_FOUND, "找不到訓練組", HttpStatus.NOT_FOUND));
        workoutSetRepository.delete(set);
    }

    public void updateSet(Long id, Long sessionId, Long setId, WorkoutSetRequestDTO newSetDTO){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ID:" + id, HttpStatus.NOT_FOUND));
        workoutSessionRepository.findByIdAndUser_Id(sessionId, id).orElseThrow(() -> new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "課表不存在, SessionId:" + sessionId, HttpStatus.NOT_FOUND));
        WorkoutSet existingSet = workoutSetRepository.findById(setId).orElseThrow(() ->
            new ApiException(ApiErrorCode.SET_NOT_FOUND, "動作組數不存在 ID:" + setId, HttpStatus.NOT_FOUND)
        );
        existingSet.setTypeId(newSetDTO.getTypeId());
        existingSet.setReps(newSetDTO.getReps());
        existingSet.setWeight(newSetDTO.getWeight());
        workoutSetRepository.save(existingSet);
    }
       
}