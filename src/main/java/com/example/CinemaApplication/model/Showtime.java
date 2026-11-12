package com.example.CinemaApplication.model;

import com.example.CinemaApplication.enums.ShowTimeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "movie_id",referencedColumnName = "movie_id" )
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="room_id",referencedColumnName = "room_id")
    private Room room;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    @Column(name = "ticket_price", nullable = false)
    private BigDecimal ticketPrice;
    @Column(name = "status", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private ShowTimeStatus status;

}
