package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.BookingRequest;
import com.example.CinemaApplication.dto.response.BookingResponse;
import com.example.CinemaApplication.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    List<String> getBookedSeats(Long showtimeId);

    BookingResponse bookTickets(String username, BookingRequest request);

    List<BookingResponse> getMyBookings(String username);

    BookingResponse getBookingById(String username, Long bookingId, boolean isAdmin);

    void cancelBookingByCustomer(String username, Long bookingId);

    Page<BookingResponse> adminGetBookings(BookingStatus status, Pageable pageable);

    BookingResponse adminUpdateBookingStatus(Long bookingId, BookingStatus newStatus);
}
