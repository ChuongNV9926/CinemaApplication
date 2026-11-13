package com.example.CinemaApplication.dto.response;

import com.example.CinemaApplication.enums.ShowTimeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowtimeResponse(
        Long id,
        Long movieId,
        String movieTitle,
        Long roomId,
        String roomName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal ticketPrice,
        ShowTimeStatus status
) {
}
