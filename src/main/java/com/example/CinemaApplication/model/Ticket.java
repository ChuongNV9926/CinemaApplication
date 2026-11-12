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

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="showtime_id")
    private Showtime showtime;
    @Column(name = "seat_code", nullable = false,columnDefinition = "varchar(4)")
    private String seatCode;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
