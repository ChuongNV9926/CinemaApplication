package com.example.CinemaApplication.dto.response;

import com.example.CinemaApplication.enums.Role;

public record CurrentUserReponse(Long accountId,
                                 Long customerId,
                                 String displayName,
                                 Role role) {
}
