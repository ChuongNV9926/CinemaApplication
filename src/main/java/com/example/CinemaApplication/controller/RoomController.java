package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.RoomRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.RoomResponse;
import com.example.CinemaApplication.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phòng thành công", roomService.getAllRooms()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getActiveRooms() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phòng hoạt động thành công", roomService.getActiveRooms()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin phòng thành công", roomService.getRoomById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo phòng chiếu thành công", roomService.createRoom(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phòng chiếu thành công", roomService.updateRoom(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        roomService.deleteOrDeactivateRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa hoặc chuyển trạng thái bảo trì phòng thành công", null));
    }
}
