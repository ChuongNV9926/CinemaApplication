package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            SELECT t.seatCode FROM Ticket t
            JOIN t.booking b
            WHERE t.showtime.id = :showtimeId AND b.status <> :cancelledStatus
            """)
    List<String> findBookedSeatCodes(@Param("showtimeId") Long showtimeId,
                                     @Param("cancelledStatus") BookingStatus cancelledStatus);

    @Query("""
            SELECT COUNT(t) > 0 FROM Ticket t
            JOIN t.booking b
            WHERE t.showtime.id = :showtimeId
              AND t.seatCode IN :seatCodes
              AND b.status <> :cancelledStatus
            """)
    boolean existsActiveTicketsForSeats(@Param("showtimeId") Long showtimeId,
                                       @Param("seatCodes") List<String> seatCodes,
                                       @Param("cancelledStatus") BookingStatus cancelledStatus);
}
