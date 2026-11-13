package com.example.CinemaApplication.dto.response;

import com.example.CinemaApplication.enums.RoomStatus;
import com.example.CinemaApplication.enums.RoomType;

public record RoomResponse(
        Long id,
        String name,
        RoomType type,
        int totalRows,
        int seatPerRows,
        int totalSeats,
        RoomStatus status
) {
}
