package com.example.CinemaApplication.dto.response;

import org.springframework.http.HttpStatus;

import java.util.Map;

public  record  ApiResponse<T>(
                  int httpStatus,
                               String message,
                               T data,
                               Map<String,String> fieldErrors) {
    public static <T> ApiResponse<T> success( String message,T data){
        return new ApiResponse<>(HttpStatus.OK.value(), message, data,Map.of());
    }
    public static <T> ApiResponse<T> error(int status,String message){
        return new ApiResponse<>(status,message,null, Map.of());
    }
    public static <T> ApiResponse<T> errors(int status, String message, Map<String,String> errors){
        return new ApiResponse<>(status,message,null,errors);
    }
}
