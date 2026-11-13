package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.model.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByShowtime_Id(Long showtimeId);

    boolean existsByCustomer_Id(Long customerId);

    List<Booking> findByShowtime_Id(Long showtimeId);

    List<Booking> findByCustomer_IdOrderByBookingDateDesc(Long customerId);

    Page<Booking> findByStatusOrderByBookingDateDesc(BookingStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.status = :status AND b.bookingDate >= :startDate AND b.bookingDate < :endDate
            ORDER BY b.bookingDate DESC
            """)
    List<Booking> findConfirmedBookingsInPeriod(@Param("status") BookingStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}
