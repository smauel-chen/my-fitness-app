package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SubmitWeightDTO;
import com.example.demo.dto.WeightTrendDTO;
import com.example.demo.entity.BodyWeightRecord;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.BodyWeightRecordRepository;
import com.example.demo.repository.UserRepository;

@Service
public class BodyWeightRecordService {
    
    private final BodyWeightRecordRepository bodyWeightRecordRepository;
    private final UserRepository userRepository;


    public BodyWeightRecordService(
        UserRepository userRepository,
        BodyWeightRecordRepository bodyWeightRecordRepository
    ){
        this.userRepository = userRepository;
        this.bodyWeightRecordRepository = bodyWeightRecordRepository;
    }

    //chart4
    public List<WeightTrendDTO> getWeightTrend(Long userId, String period) {
        LocalDate start = switch (period) {
            case "weekly" -> LocalDate.now().minusDays(6);
            case "two-weeks" -> LocalDate.now().minusDays(13);
            case "monthly" -> LocalDate.now().minusDays(29);
            default -> throw new ApiException(ApiErrorCode.INVALID_REQUEST, "不合法的時間區間", HttpStatus.BAD_REQUEST);
        };
    
        return bodyWeightRecordRepository.findByUserIdAndDateAfterOrderByDateAsc(userId, start)
            .stream()
            .map(r -> new WeightTrendDTO(r.getDate(), r.getWeight()))
            .collect(Collectors.toList());
    }

    public void saveOrUpdateWeight(Long userId, SubmitWeightDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者", HttpStatus.NOT_FOUND));

            System.out.println(dto.getWeight()+"日期"+dto.getDate());
        if (dto.getDate() == null || dto.getWeight() == null) {
            throw new ApiException(ApiErrorCode.BAD_REQUEST, "輸入不能為空", HttpStatus.BAD_REQUEST);
        }
            
        BodyWeightRecord record = bodyWeightRecordRepository
            .findByUserIdAndDate(userId, dto.getDate())
            .orElse(new BodyWeightRecord(dto.getDate(), dto.getWeight(), user));
        record.setWeight(dto.getWeight());
        System.out.println(record.getWeight()+"日期"+record.getDate());

        if(record.getDate() == null || record.getWeight() == null){
            throw new ApiException(ApiErrorCode.BAD_REQUEST, "輸入不能為空", HttpStatus.BAD_REQUEST);
        }
        bodyWeightRecordRepository.save(record);
    }   

    public List<BodyWeightRecord> getAllRecords(Long id){
        return bodyWeightRecordRepository.findByUserIdOrderByDateAsc(id);
    }

    public void deleteUglyDate(Long id, Long weightId){
        BodyWeightRecord uglyData = bodyWeightRecordRepository.findById(weightId).orElseThrow(() ->
            new ApiException(ApiErrorCode.WEIGHT_NOT_FOUND, "沒有找到資料", HttpStatus.NOT_FOUND)
        );
        if(uglyData.getDate() != null && uglyData.getWeight()!= null){
            throw new ApiException(ApiErrorCode.BAD_REQUEST, "再確認一次", HttpStatus.BAD_REQUEST);
        }
        bodyWeightRecordRepository.delete(uglyData);
    }

}
