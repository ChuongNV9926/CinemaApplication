package com.example.CinemaApplication.dto.response;

import com.example.CinemaApplication.enums.AccountStatus;
import com.example.CinemaApplication.enums.Role;

import java.time.LocalDate;

public record CustomerResponse(
        Long id,
        Long accountId,
        String username,
        String fullName,
        String phone,
        LocalDate birthday,
        String identityCard,
        String email,
        Role role,
        AccountStatus accountStatus
) {
}
