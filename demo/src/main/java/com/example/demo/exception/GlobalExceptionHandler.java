package com.example.demo.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.dto.ApiErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 處理所有未捕捉到的例外
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAllExceptions(Exception ex, WebRequest request) {
        ApiErrorResponseDTO responseDTO = new ApiErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }

    // 處理參數驗證失敗的錯誤（例如 @Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errList = ex.getBindingResult().getAllErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.toList());

        String combinedMessage = String.join("；", errList);
        
        ApiErrorResponseDTO responseDTO = new ApiErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            combinedMessage,
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    // 可以繼續擴充：處理特定錯誤類型（如自定義例外）
}
