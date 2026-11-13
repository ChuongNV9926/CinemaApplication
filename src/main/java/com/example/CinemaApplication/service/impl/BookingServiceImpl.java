package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.BookingRequest;
import com.example.CinemaApplication.dto.response.BookingResponse;
import com.example.CinemaApplication.enums.AgeRating;
import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.enums.ShowTimeStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.mapper.BookingMapper;
import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.model.Booking;
import com.example.CinemaApplication.model.Customer;
import com.example.CinemaApplication.model.Showtime;
import com.example.CinemaApplication.model.Ticket;
import com.example.CinemaApplication.repository.AccountRepository;
import com.example.CinemaApplication.repository.BookingRepository;
import com.example.CinemaApplication.repository.CustomerRepository;
import com.example.CinemaApplication.repository.ShowtimeRepository;
import com.example.CinemaApplication.repository.TicketRepository;
import com.example.CinemaApplication.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<String> getBookedSeats(Long showtimeId) {
        return ticketRepository.findBookedSeatCodes(showtimeId, BookingStatus.CANCELLED);
    }

    @Override
    @Transactional
    public BookingResponse bookTickets(String username, BookingRequest request) {
        // 1. Lấy thông tin khách hàng từ username đã đăng nhập
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));
        Customer customer = customerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new BusinessException("Hồ sơ khách hàng chưa được thiết lập!"));

        // 2. Khóa suất chiếu bằng PESSIMISTIC_WRITE để chống tranh ghế đồng thời
        Showtime showtime = showtimeRepository.findByIdForUpdate(request.showtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu ID: " + request.showtimeId()));

        if (showtime.getStatus() == ShowTimeStatus.CANCELLED) {
            throw new BusinessException("Suất chiếu đã bị hủy, không thể đặt vé!");
        }

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Suất chiếu đã bắt đầu hoặc đã kết thúc!");
        }

        // BR-11: Kiểm tra độ tuổi tại ngày chiếu
        LocalDate showDate = showtime.getStartTime().toLocalDate();
        int ageAtShowDate = Period.between(customer.getBirthday(), showDate).getYears();
        validateAgeRating(showtime.getMovie().getAgeRating(), ageAtShowDate);

        // Chuẩn hóa và kiểm tra ghế trùng lặp trong request (EX-06)
        List<String> normalizedSeats = request.seatCodes().stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .toList();

        Set<String> uniqueSeats = new HashSet<>(normalizedSeats);
        if (uniqueSeats.size() != normalizedSeats.size()) {
            throw new BusinessException("Danh sách ghế chứa mã ghế bị lặp lại!");
        }

        // Kiểm tra ghế có hợp lệ trong phòng chiếu không
        for (String seat : normalizedSeats) {
            validateSeatInRoom(seat, showtime.getRoom().getTotalRows(), showtime.getRoom().getSeatPerRows());
        }

        // BR-10: Kiểm tra ghế đã bị đặt chưa (bỏ qua các đơn CANCELLED)
        boolean hasBookedSeats = ticketRepository.existsActiveTicketsForSeats(
                showtime.getId(), normalizedSeats, BookingStatus.CANCELLED
        );
        if (hasBookedSeats) {
            throw new BusinessException("Một hoặc nhiều ghế bạn chọn đã có người đặt!");
        }

        // BR-12: TotalAmount = TicketPrice x Số ghế; đơn mới là PENDING
        BigDecimal totalAmount = showtime.getTicketPrice().multiply(BigDecimal.valueOf(normalizedSeats.size()));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);

        List<Ticket> tickets = new ArrayList<>();
        for (String seat : normalizedSeats) {
            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(showtime);
            ticket.setSeatCode(seat);
            ticket.setPrice(showtime.getTicketPrice());
            tickets.add(ticket);
        }
        booking.setTicketList(tickets);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String username) {
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));
        Customer customer = customerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new BusinessException("Hồ sơ khách hàng chưa có!"));

        return bookingRepository.findByCustomer_IdOrderByBookingDateDesc(customer.getId())
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String username, Long bookingId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt vé ID: " + bookingId));

        if (!isAdmin) {
            Account account = accountRepository.findByUsernameIgnoreCase(username)
                    .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));
            if (!booking.getCustomer().getAccount().getId().equals(account.getId())) {
                throw new BusinessException("Bạn không có quyền xem đơn đặt vé của người khác!");
            }
        }

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public void cancelBookingByCustomer(String username, Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt vé ID: " + bookingId));

        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));

        // Khách chỉ được hủy đơn của chính mình
        if (!booking.getCustomer().getAccount().getId().equals(account.getId())) {
            throw new BusinessException("Bạn chỉ có quyền hủy đơn đặt vé của chính mình!");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Đơn đặt vé này đã bị hủy trước đó!");
        }

        // BR-14: Bắt buộc hủy trước giờ chiếu ít nhất 2 giờ
        LocalDateTime deadline = booking.getShowtime().getStartTime().minusHours(2);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new BusinessException("Chỉ được hủy đơn đặt vé trước giờ chiếu ít nhất 2 giờ!");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> adminGetBookings(BookingStatus status, Pageable pageable) {
        Page<Booking> page = (status != null)
                ? bookingRepository.findByStatusOrderByBookingDateDesc(status, pageable)
                : bookingRepository.findAll(pageable);

        List<BookingResponse> content = page.getContent().stream().map(bookingMapper::toResponse).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public BookingResponse adminUpdateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt vé ID: " + bookingId));

        // BR-13: PENDING -> CONFIRMED; PENDING/CONFIRMED -> CANCELLED. CANCELLED là trạng thái kết thúc.
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Đơn đã bị hủy, không thể thay đổi trạng thái!");
        }

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);
        return bookingMapper.toResponse(updated);
    }

    private void validateAgeRating(AgeRating rating, int age) {
        switch (rating) {
            case T13 -> {
                if (age < 13) throw new BusinessException("Phim dành cho khán giả từ 13 tuổi trở lên!");
            }
            case T16 -> {
                if (age < 16) throw new BusinessException("Phim dành cho khán giả từ 16 tuổi trở lên!");
            }
            case T18 -> {
                if (age < 18) throw new BusinessException("Phim dành cho khán giả từ 18 tuổi trở lên!");
            }
            default -> {} // P: mọi lứa tuổi
        }
    }

    private void validateSeatInRoom(String seat, int maxRows, int seatsPerRow) {
        if (seat.length() < 2 || seat.length() > 3) {
            throw new BusinessException("Mã ghế không hợp lệ: " + seat);
        }
        char rowChar = seat.charAt(0);
        int rowIndex = rowChar - 'A' + 1;
        if (rowIndex < 1 || rowIndex > maxRows) {
            throw new BusinessException("Hàng ghế " + rowChar + " không tồn tại trong phòng chiếu!");
        }
        try {
            int seatNum = Integer.parseInt(seat.substring(1));
            if (seatNum < 1 || seatNum > seatsPerRow) {
                throw new BusinessException("Số ghế " + seatNum + " vượt quá số ghế trong hàng (" + seatsPerRow + ")!");
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("Mã ghế không hợp lệ: " + seat);
        }
    }
}
