package com.tsms.config;

import com.tsms.dto.Dto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * GlobalExceptionHandler — Catches all exceptions thrown in Controllers/Services
 * Returns a clean JSON error response instead of a stack trace
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Dto.ApiResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(
            Dto.ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dto.ApiResponse> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError().body(
            Dto.ApiResponse.builder()
                .success(false)
                .message("Internal server error: " + ex.getMessage())
                .build()
        );
    }
}
