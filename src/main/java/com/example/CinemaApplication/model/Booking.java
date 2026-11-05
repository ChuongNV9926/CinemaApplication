package com.example.CinemaApplication.model;

import com.example.CinemaApplication.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "showtime_id",referencedColumnName = "id")
    private Showtime showtime;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    private BookingStatus status;
    @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("seatCode ASC")
    private List<Tickets> ticketsList;
}
