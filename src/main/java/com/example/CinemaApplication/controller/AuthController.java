package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.LoginRequest;
import com.example.CinemaApplication.dto.request.RegisterRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.CurrentUserReponse;
import com.example.CinemaApplication.dto.response.TokenReponse;
import com.example.CinemaApplication.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenReponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok().body(ApiResponse.success("Login Successfully",authService.login(loginRequest)));
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserReponse>> me(@AuthenticationPrincipal UserDetails principal){
        return ResponseEntity.ok().body(ApiResponse.success("Get Data Successfully",authService.me(principal.getUsername())));
    }
    @PostMapping("/register")
    public  ResponseEntity<ApiResponse<Void>> reigster(@Valid @RequestBody RegisterRequest registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.ok().body((ApiResponse.success("Register Successfully",null)));
    }
}
