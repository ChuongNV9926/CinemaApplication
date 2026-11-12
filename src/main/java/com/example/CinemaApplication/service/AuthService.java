package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.LoginRequest;
import com.example.CinemaApplication.dto.request.RegisterRequest;
import com.example.CinemaApplication.dto.response.CurrentUserReponse;
import com.example.CinemaApplication.dto.response.TokenReponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    TokenReponse login(LoginRequest loginRequest);
    CurrentUserReponse me(String username);
    void register(RegisterRequest registerRequest);
}
