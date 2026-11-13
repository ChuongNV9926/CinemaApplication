package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.ReviewRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.ReviewResponse;
import com.example.CinemaApplication.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    // Public API: Xem danh sách đánh giá của phim
    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getMovieReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đánh giá thành công", reviewService.getMovieReviews(movieId, pageable)));
    }

    // Public API: Lấy điểm đánh giá trung bình của phim
    @GetMapping("/movies/{movieId}/rating")
    public ResponseEntity<ApiResponse<Double>> getMovieRating(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success("Lấy điểm trung bình thành công", reviewService.getMovieAverageRating(movieId)));
    }

    // Customer API: Viết hoặc cập nhật đánh giá (BR-16)
    @PostMapping("/customer/movies/{movieId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> postReview(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Đánh giá phim thành công", reviewService.writeOrUpdateReview(principal.getUsername(), movieId, request)));
    }
}
