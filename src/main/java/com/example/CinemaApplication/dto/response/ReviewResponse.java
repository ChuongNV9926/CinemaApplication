package com.example.CinemaApplication.dto.response;

import java.time.LocalDate;

public record ReviewResponse(
        Long customerId,
        String customerName,
        Long movieId,
        String movieTitle,
        int rating,
        String comment,
        LocalDate reviewDate
) {
}
