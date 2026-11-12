package com.example.CinemaApplication.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "Fullname must be not blank")
        String fullName,
        @NotBlank(message = "Email must be not blank")
        @Email(message = "Invalid Email!")
        String email,
        @NotBlank(message = "Phone must be not blank")
        @Pattern(regexp = "^(0|\\+84)\\d{9}$",message = "Phone number must contain exactly 10 digits and start with zero")
        String mobile,
        @NotNull(message = "Please choose your birthday!")
        LocalDate birthday,
        @NotBlank(message = "Identity Card must be not blank")
        @Pattern(regexp = "^\\d{12}$",message = "Identity Card must contain 12 digits")
        String identityCard,
        @NotBlank (message = "Password must be not blank")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "Password must be contain at least 8 character and contain at lease  one upper letter, one lower letter  and one digit")
        String password,
        @NotBlank (message = "Confirm Password must be not blank")
        String confirmPassword)
{
}
