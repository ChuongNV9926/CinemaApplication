package com.example.CinemaApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Tickets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="showtime_id")
    private Showtime showtime;
    @Column(nullable = false)
    private String seatCode;
    @Column(nullable = false)
    private BigDecimal price;
}
