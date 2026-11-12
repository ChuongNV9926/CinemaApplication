package com.example.CinemaApplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
                           @NotBlank(message = "Please input your username!")
                           @Size(message = "Username length must be below 100 character ",max = 100)
                           String username,
                           @NotBlank(message = "Please enter your password")
                           @Size(max = 72, message = "Password length must be below 72 character")
                           String password) {
}
