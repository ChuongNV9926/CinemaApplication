package com.example.CinemaApplication.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BookingRequest(
        @NotNull(message = "Mã suất chiếu không được để trống")
        Long showtimeId,

        @NotEmpty(message = "Danh sách ghế không được để trống")
        @Size(min = 1, max = 8, message = "Chỉ được chọn từ 1 đến 8 ghế trong một lần đặt")
        List<String> seatCodes
) {
}
