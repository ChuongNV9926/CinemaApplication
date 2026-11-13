package com.example.CinemaApplication.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RevenueReportResponse(
        LocalDate startDate,
        LocalDate endDate,
        long totalConfirmedBookings,
        long totalTicketsSold,
        BigDecimal totalRevenue,
        List<MovieRevenueResponse> movieRevenues,
        List<BookingResponse> bookings
) {
}
