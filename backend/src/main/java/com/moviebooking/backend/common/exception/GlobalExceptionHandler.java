package com.moviebooking.backend.common.exception;

import com.moviebooking.backend.common.dto.response.ApiResponse;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException e) {
        return toResponse(e.getErrorCode(), e.getErrorCode().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : ErrorCode.INVALID_REQUEST.getMessage();
        return toResponse(ErrorCode.INVALID_REQUEST, message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            HandlerMethodValidationException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(Exception e) {
        return toResponse(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.getMessage());
    }

    // Khóa gọi trực tiếp qua EntityManager không được Spring dịch sang OptimisticLockingFailureException
    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class,
            PessimisticLockException.class, LockTimeoutException.class})
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(Exception e) {
        return toResponse(ErrorCode.CONCURRENT_UPDATE, ErrorCode.CONCURRENT_UPDATE.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUncategorized(Exception e) {
        log.error("Lỗi chưa được xử lý", e);
        return toResponse(ErrorCode.UNCATEGORIZED, ErrorCode.UNCATEGORIZED.getMessage());
    }

    private ResponseEntity<ApiResponse<Void>> toResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }
}
