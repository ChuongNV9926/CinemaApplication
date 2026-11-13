package com.example.CinemaApplication.dto.request;

import com.example.CinemaApplication.enums.AgeRating;
import com.example.CinemaApplication.enums.MovieStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record MovieRequest(
        @NotBlank @Size(max = 200, message = "Tiêu đề phim tối đa 200 ký tự")
        String title,

        @NotBlank @Size(max = 100, message = "Tên đạo diễn tối đa 100 ký tự")
        String director,

        @Min(value = 30, message = "Thời lượng tối thiểu 30 phút")
        int duration,

        @NotNull(message = "Ngày khởi chiếu không được để trống")
        Instant releaseDate,

        @NotNull(message = "Phân loại độ tuổi không được để trống")
        AgeRating ageRating,

        @NotBlank @Size(max = 4000, message = "Mô tả tối đa 4000 ký tự")
        String description,

        @NotBlank @Size(max = 1000, message = "Đường dẫn poster tối đa 1000 ký tự")
        String posterUrl,

        @NotEmpty(message = "Phim phải thuộc ít nhất một thể loại")
        List<Long> genreIds,

        @NotNull(message = "Trạng thái phim không được để trống")
        MovieStatus status
) {
}
