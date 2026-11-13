package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.BookingRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.BookingResponse;
import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    // Public API: Lấy danh sách các ghế đã bán của suất chiếu
    @GetMapping("/showtimes/{showtimeId}/booked-seats")
    public ResponseEntity<ApiResponse<List<String>>> getBookedSeats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách ghế đã đặt thành công", bookingService.getBookedSeats(showtimeId)));
    }

    // Customer API: Đặt vé chọn ghế
    @PostMapping("/customer/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> bookTickets(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Đặt vé thành công", bookingService.bookTickets(principal.getUsername(), request)));
    }

    // Customer API: Xem lịch sử đặt vé cá nhân
    @GetMapping("/customer/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử đặt vé thành công", bookingService.getMyBookings(principal.getUsername())));
    }

    // Customer/Admin API: Xem chi tiết 1 đơn đặt vé
    @GetMapping("/customer/bookings/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingDetail(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id
    ) {
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin đơn đặt vé thành công", bookingService.getBookingById(principal.getUsername(), id, isAdmin)));
    }

    // Customer API: Hủy đơn đặt vé của chính mình (trước 2 giờ)
    @PutMapping("/customer/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelMyBooking(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id
    ) {
        bookingService.cancelBookingByCustomer(principal.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Hủy đơn đặt vé thành công", null));
    }

    // Admin API: Xem danh sách và lọc đơn đặt vé
    @GetMapping("/admin/bookings")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> adminGetBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn đặt vé thành công", bookingService.adminGetBookings(status, pageable)));
    }

    // Admin API: Duyệt hoặc hủy đơn đặt vé
    @PutMapping("/admin/bookings/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> adminUpdateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn đặt vé thành công", bookingService.adminUpdateBookingStatus(id, status)));
    }
}
