package com.example.CinemaApplication.dto.request;

import com.example.CinemaApplication.enums.ShowTimeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowtimeRequest(
        @NotNull(message = "Mã phim không được để trống")
        Long movieId,

        @NotNull(message = "Mã phòng không được để trống")
        Long roomId,

        @NotNull(message = "Thời gian bắt đầu không được để trống")
        LocalDateTime startTime,

        @NotNull(message = "Thời gian kết thúc không được để trống")
        LocalDateTime endTime,

        @NotNull(message = "Giá vé không được để trống")
        @DecimalMin(value = "1000", message = "Giá vé tối thiểu là 1,000 VND")
        BigDecimal ticketPrice,

        @NotNull(message = "Trạng thái suất chiếu không được để trống")
        ShowTimeStatus status
) {
}
