package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.dto.ApiErrorResponseDTO;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleApiException(ApiException ex, WebRequest request) {
        ApiErrorResponseDTO response = new ApiErrorResponseDTO(
            ex.getHttpStatus().value(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            ex.getErrorCode().name()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    // 處理所有未捕捉的例外
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAllExceptions(Exception ex, WebRequest request) {
        ApiErrorResponseDTO responseDTO = new ApiErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            "INTERNAL_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }

    // 處理參數驗證失敗的錯誤（例如 @Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiErrorResponseDTO responseDTO = new ApiErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            errorMessage,
            "",
            "VALIDATION_ERROR"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    // 可以繼續擴充：處理特定錯誤類型（如自定義例外）
}
