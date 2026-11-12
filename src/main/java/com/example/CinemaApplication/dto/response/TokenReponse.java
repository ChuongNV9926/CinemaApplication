package com.example.CinemaApplication.dto.response;

import java.time.LocalDateTime;

public record TokenReponse (String accessToken,
                            String tokenType,
                            long expiresIn,
                            CurrentUserReponse user) {
}
