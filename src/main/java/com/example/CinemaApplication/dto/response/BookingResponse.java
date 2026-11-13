package com.example.CinemaApplication.dto.response;

import com.example.CinemaApplication.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        Long customerId,
        String customerName,
        Long showtimeId,
        String movieTitle,
        String roomName,
        LocalDateTime showtimeStart,
        LocalDateTime bookingDate,
        BigDecimal totalAmount,
        BookingStatus status,
        List<TicketResponse> tickets
) {
}
