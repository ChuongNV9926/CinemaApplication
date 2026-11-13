package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.response.BookingResponse;
import com.example.CinemaApplication.dto.response.MovieRevenueResponse;
import com.example.CinemaApplication.dto.response.RevenueReportResponse;
import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.mapper.BookingMapper;
import com.example.CinemaApplication.model.Booking;
import com.example.CinemaApplication.model.Movie;
import com.example.CinemaApplication.repository.BookingRepository;
import com.example.CinemaApplication.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate) {
        // BR-15: StartDate <= EndDate
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Chỉ tính các đơn CONFIRMED theo booking_date trong kỳ
        List<Booking> confirmedBookings = bookingRepository.findConfirmedBookingsInPeriod(
                BookingStatus.CONFIRMED, startDateTime, endDateTime
        );

        long totalBookings = confirmedBookings.size();
        long totalTickets = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        Map<Long, MovieStats> movieStatsMap = new HashMap<>();

        for (Booking booking : confirmedBookings) {
            totalRevenue = totalRevenue.add(booking.getTotalAmount());
            int ticketsInBooking = (booking.getTicketList() != null) ? booking.getTicketList().size() : 0;
            totalTickets += ticketsInBooking;

            Movie movie = booking.getShowtime().getMovie();
            MovieStats stats = movieStatsMap.computeIfAbsent(movie.getId(), k -> new MovieStats(movie.getId(), movie.getTitle()));
            stats.ticketsSold += ticketsInBooking;
            stats.revenue = stats.revenue.add(booking.getTotalAmount());
        }

        // Sắp xếp doanh thu theo phim giảm dần
        List<MovieRevenueResponse> movieRevenues = movieStatsMap.values().stream()
                .sorted(Comparator.comparing(MovieStats::getRevenue).reversed())
                .map(s -> new MovieRevenueResponse(s.movieId, s.movieTitle, s.ticketsSold, s.revenue))
                .toList();

        List<BookingResponse> bookingResponses = confirmedBookings.stream()
                .map(bookingMapper::toResponse)
                .toList();

        return new RevenueReportResponse(
                startDate,
                endDate,
                totalBookings,
                totalTickets,
                totalRevenue,
                movieRevenues,
                bookingResponses
        );
    }

    private static class MovieStats {
        Long movieId;
        String movieTitle;
        long ticketsSold = 0;
        BigDecimal revenue = BigDecimal.ZERO;

        MovieStats(Long movieId, String movieTitle) {
            this.movieId = movieId;
            this.movieTitle = movieTitle;
        }

        BigDecimal getRevenue() {
            return revenue;
        }
    }
}
