package com.example.CinemaApplication.exception;

import com.example.CinemaApplication.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> methodNotValidHanlder(MethodArgumentNotValidException e){
        Map<String,String> errors= new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error->errors.put(error.getField(),error.getDefaultMessage()));
        return ResponseEntity.badRequest().
                body(ApiResponse.errors(HttpStatus.BAD_REQUEST.value(), "Tài khoản hoặc mật khẩu khong hop le",errors));
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> businessExceptionHanlder(BusinessException e){
        return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException e){
        return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.NO_CONTENT.value(), e.getMessage()));

    }
}
