package com.chriso.financetransaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(
            ServiceUnavailableException ex
    ) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                503,
                "SERVICE_UNAVAILABLE",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                400,
                "BUSINESS_ERROR",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(error);
    }
}