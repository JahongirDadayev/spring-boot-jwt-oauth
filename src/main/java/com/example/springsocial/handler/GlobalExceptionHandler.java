package com.example.springsocial.handler;

import com.example.springsocial.model.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ApiResponse> handler(RuntimeException exception){
        log.error("Runtime Error {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("An error occurred", null, false));
    }
}
