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
    @Column(name = "booking_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "showtime_id",referencedColumnName = "showtime_id")
    private Showtime showtime;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "status", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("seatCode ASC")
    private List<Ticket> ticketList;
}
