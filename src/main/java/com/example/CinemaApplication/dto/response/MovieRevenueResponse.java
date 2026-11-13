package com.example.CinemaApplication.dto.response;

import java.math.BigDecimal;

public record MovieRevenueResponse(
        Long movieId,
        String movieTitle,
        long totalTicketsSold,
        BigDecimal totalRevenue
) {
}
