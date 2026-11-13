package com.example.CinemaApplication.dto.request;

import com.example.CinemaApplication.enums.RoomStatus;
import com.example.CinemaApplication.enums.RoomType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomRequest(
        @NotBlank @Size(max = 100, message = "Tên phòng tối đa 100 ký tự")
        String name,

        @NotNull(message = "Loại phòng không được để trống")
        RoomType type,

        @Min(value = 1, message = "Số hàng ghế tối thiểu là 1")
        @Max(value = 26, message = "Số hàng ghế tối đa là 26 (A-Z)")
        int totalRows,

        @Min(value = 1, message = "Số ghế mỗi hàng tối thiểu là 1")
        @Max(value = 30, message = "Số ghế mỗi hàng tối đa là 30")
        int seatPerRows,

        @NotNull(message = "Trạng thái phòng không được để trống")
        RoomStatus status
) {
}
