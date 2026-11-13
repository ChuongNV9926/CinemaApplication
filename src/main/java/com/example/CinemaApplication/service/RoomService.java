package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.RoomRequest;
import com.example.CinemaApplication.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {

    List<RoomResponse> getAllRooms();

    List<RoomResponse> getActiveRooms();

    RoomResponse getRoomById(Long id);

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteOrDeactivateRoom(Long id);
}
