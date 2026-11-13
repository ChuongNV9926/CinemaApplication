package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.ShowtimeRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.ShowtimeResponse;
import com.example.CinemaApplication.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // Public API: Khách xem lịch chiếu của 1 phim
    @GetMapping("/movies/{movieId}/showtimes")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getPublicShowtimes(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách suất chiếu thành công", showtimeService.getPublicShowtimesByMovie(movieId)));
    }

    // Public API: Xem chi tiết 1 suất chiếu
    @GetMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin suất chiếu thành công", showtimeService.getShowtimeById(id)));
    }

    // Admin API: Xem toàn bộ suất chiếu
    @GetMapping("/admin/showtimes")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getAllShowtimes() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách toàn bộ suất chiếu thành công", showtimeService.getAllShowtimes()));
    }

    // Admin API: Tạo suất chiếu mới (Có check BR-07, BR-08, lock Room)
    @PostMapping("/admin/showtimes")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> createShowtime(@Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo suất chiếu thành công", showtimeService.createShowtime(request)));
    }

    // Admin API: Cập nhật suất chiếu (Có check EX-03)
    @PutMapping("/admin/showtimes/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> updateShowtime(
            @PathVariable Long id,
            @Valid @RequestBody ShowtimeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật suất chiếu thành công", showtimeService.updateShowtime(id, request)));
    }

    // Admin API: Hủy suất chiếu và tự động hủy các đơn đặt vé liên quan (BR-09)
    @DeleteMapping("/admin/showtimes/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelShowtime(@PathVariable Long id) {
        showtimeService.cancelShowtimeAndBookings(id);
        return ResponseEntity.ok(ApiResponse.success("Hủy suất chiếu thành công", null));
    }
}
