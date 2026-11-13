package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.ShowtimeRequest;
import com.example.CinemaApplication.dto.response.ShowtimeResponse;
import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.enums.MovieStatus;
import com.example.CinemaApplication.enums.RoomStatus;
import com.example.CinemaApplication.enums.ShowTimeStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.mapper.ShowtimeMapper;
import com.example.CinemaApplication.model.Booking;
import com.example.CinemaApplication.model.Movie;
import com.example.CinemaApplication.model.Room;
import com.example.CinemaApplication.model.Showtime;
import com.example.CinemaApplication.repository.BookingRepository;
import com.example.CinemaApplication.repository.MovieRepository;
import com.example.CinemaApplication.repository.RoomRepository;
import com.example.CinemaApplication.repository.ShowtimeRepository;
import com.example.CinemaApplication.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ShowtimeMapper showtimeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getPublicShowtimesByMovie(Long movieId) {
        LocalDateTime now = LocalDateTime.now();
        return showtimeRepository.findByMovie_IdAndStatusEqualsAndStartTimeAfterOrderByStartTimeAsc(movieId, ShowTimeStatus.OPEN, now)
                .stream()
                .map(showtimeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeResponse getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu ID: " + id));
        return showtimeMapper.toResponse(showtime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream().map(showtimeMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        // Khóa phòng với Pessimistic Lock để chống trùng lịch đồng thời
        Room room = roomRepository.findByIdWithShowTimeUpdate(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu ID: " + request.roomId()));

        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new BusinessException("Phòng chiếu đang bảo trì, không thể tạo suất chiếu!");
        }

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim ID: " + request.movieId()));

        if (movie.getStatus() == MovieStatus.STOPPED) {
            throw new BusinessException("Phim đã ngừng chiếu, không thể tạo suất chiếu mới!");
        }

        validateShowtimeSchedule(request.startTime(), request.endTime(), movie.getDuration(), room.getId(), -1L);

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.startTime());
        showtime.setEndTime(request.endTime());
        showtime.setTicketPrice(request.ticketPrice());
        showtime.setStatus(request.status());

        Showtime saved = showtimeRepository.save(showtime);
        return showtimeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu ID: " + id));

        // EX-03: Nếu suất chiếu đã có Booking, không cho sửa phim, phòng, thời gian và giá vé
        boolean hasBooking = bookingRepository.existsByShowtime_Id(id);
        if (hasBooking) {
            if (!showtime.getRoom().getId().equals(request.roomId())
                    || !showtime.getMovie().getId().equals(request.movieId())
                    || !showtime.getStartTime().isEqual(request.startTime())
                    || !showtime.getEndTime().isEqual(request.endTime())
                    || showtime.getTicketPrice().compareTo(request.ticketPrice()) != 0) {
                throw new BusinessException("Suất chiếu đã có khách đặt vé, không thể sửa thông tin phim, phòng, giờ hoặc giá vé!");
            }
        }

        Room room = roomRepository.findByIdWithShowTimeUpdate(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu ID: " + request.roomId()));

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim ID: " + request.movieId()));

        validateShowtimeSchedule(request.startTime(), request.endTime(), movie.getDuration(), room.getId(), id);

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.startTime());
        showtime.setEndTime(request.endTime());
        showtime.setTicketPrice(request.ticketPrice());
        showtime.setStatus(request.status());

        return showtimeMapper.toResponse(showtime);
    }

    @Override
    @Transactional
    public void cancelShowtimeAndBookings(Long id) {
        Showtime showtime = showtimeRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu ID: " + id));

        // BR-09: Suất chiếu chưa có Booking thì xóa hẳn; đã có Booking thì CANCELLED và hủy toàn bộ đơn thuộc suất
        List<Booking> bookings = bookingRepository.findByShowtime_Id(id);
        if (bookings.isEmpty()) {
            showtimeRepository.delete(showtime);
        } else {
            showtime.setStatus(ShowTimeStatus.CANCELLED);
            showtimeRepository.save(showtime);

            for (Booking booking : bookings) {
                if (booking.getStatus() != BookingStatus.CANCELLED) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                }
            }
        }
    }

    private void validateShowtimeSchedule(LocalDateTime start, LocalDateTime end, int durationMinutes, Long roomId, Long excludedId) {
        // BR-07: StartTime < EndTime; EndTime >= StartTime + Duration
        if (!start.isBefore(end)) {
            throw new BusinessException("Thời gian bắt đầu phải trước thời gian kết thúc!");
        }

        if (end.isBefore(start.plusMinutes(durationMinutes))) {
            throw new BusinessException("Thời gian kết thúc phải đủ thời lượng của phim (" + durationMinutes + " phút)!");
        }

        // BR-08: Cùng phòng không chồng giờ chiếu, bỏ qua các suất CANCELLED
        List<Showtime> overlapping = showtimeRepository.findOverlappingIgnoreCancelled(
                roomId, start, end, excludedId, ShowTimeStatus.CANCELLED
        );

        if (!overlapping.isEmpty()) {
            throw new BusinessException("Phòng chiếu đã có suất chiếu khác trong khoảng thời gian này!");
        }
    }
}
