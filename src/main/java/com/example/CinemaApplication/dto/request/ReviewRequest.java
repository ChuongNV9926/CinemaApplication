package com.example.CinemaApplication.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @Min(value = 1, message = "Điểm đánh giá tối thiểu là 1 sao")
        @Max(value = 5, message = "Điểm đánh giá tối đa là 5 sao")
        int rating,

        @NotBlank(message = "Bình luận không được để trống")
        @Size(max = 2000, message = "Bình luận tối đa 2000 ký tự")
        String comment
) {
}
