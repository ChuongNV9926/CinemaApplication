package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.MovieRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.MovieResponse;
import com.example.CinemaApplication.enums.MovieStatus;
import com.example.CinemaApplication.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    // Public API xem phim đang chiếu
    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getPublicMovies() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phim thành công", movieService.getPublicShowingMovies()));
    }

    // Public API chi tiết phim
    @GetMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin phim thành công", movieService.getMovieById(id)));
    }

    // Admin API tìm kiếm, lọc, phân trang phim
    @GetMapping("/admin/movies")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> adminSearchMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm phim thành công", movieService.searchMovies(keyword, genreId, status, pageable)));
    }

    // Admin API tạo phim mới
    @PostMapping("/admin/movies")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo phim mới thành công", movieService.createMovie(request)));
    }

    // Admin API cập nhật phim
    @PutMapping("/admin/movies/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phim thành công", movieService.updateMovie(id, request)));
    }

    // Admin API xóa hoặc ngừng chiếu (BR-06)
    @DeleteMapping("/admin/movies/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteOrStopMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa hoặc cập nhật trạng thái phim thành công", null));
    }
}
