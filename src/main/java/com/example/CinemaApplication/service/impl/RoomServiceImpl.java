package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.RoomRequest;
import com.example.CinemaApplication.dto.response.RoomResponse;
import com.example.CinemaApplication.enums.RoomStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.mapper.RoomMapper;
import com.example.CinemaApplication.model.Room;
import com.example.CinemaApplication.repository.RoomRepository;
import com.example.CinemaApplication.repository.ShowtimeRepository;
import com.example.CinemaApplication.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(roomMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getActiveRooms() {
        return roomRepository.findByStatus(RoomStatus.ACTIVE).stream().map(roomMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu ID: " + id));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.findByNameIgnoreCase(request.name().trim()).isPresent()) {
            throw new BusinessException("Tên phòng chiếu đã tồn tại!");
        }

        Room room = new Room();
        room.setName(request.name().trim());
        room.setType(request.type());
        room.setTotalRows(request.totalRows());
        room.setSeatPerRows(request.seatPerRows());
        room.setStatus(request.status());

        Room saved = roomRepository.save(room);
        return roomMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu ID: " + id));

        if (roomRepository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), id)) {
            throw new BusinessException("Tên phòng chiếu đã tồn tại!");
        }

        // EX-02: Không cho đổi sơ đồ ghế nếu phòng đã từng có suất chiếu
        boolean hasShowtime = showtimeRepository.existsByRoom_Id(id);
        if (hasShowtime && (room.getTotalRows() != request.totalRows() || room.getSeatPerRows() != request.seatPerRows())) {
            throw new BusinessException("Không thể sửa sơ đồ ghế của phòng đã có suất chiếu!");
        }

        room.setName(request.name().trim());
        room.setType(request.type());
        room.setTotalRows(request.totalRows());
        room.setSeatPerRows(request.seatPerRows());
        room.setStatus(request.status());

        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public void deleteOrDeactivateRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu ID: " + id));

        // EX-01: Phòng đã có suất chiếu không được xóa hẳn, chuyển sang MAINTENANCE
        boolean hasShowtime = showtimeRepository.existsByRoom_Id(id);
        if (hasShowtime) {
            room.setStatus(RoomStatus.MAINTENANCE);
            roomRepository.save(room);
        } else {
            roomRepository.delete(room);
        }
    }
}
