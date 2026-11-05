package com.example.CinemaApplication.model;

import com.example.CinemaApplication.enums.ShowTimeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "movie_id",referencedColumnName = "id")
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="room_id",referencedColumnName = "id")
    private Room room;
    @Column(nullable = false)
    private Instant startTime;
    @Column(nullable = false)
    private Instant endTime;
    @Column(nullable = false)
    private double ticketPrice;
    @Column(nullable = false)
    private ShowTimeStatus status;

}
