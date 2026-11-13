package com.example.CinemaApplication.dto.response;

import java.math.BigDecimal;

public record TicketResponse(
        Long id,
        String seatCode,
        BigDecimal price
) {
}
