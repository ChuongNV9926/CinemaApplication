package com.example.CinemaApplication.model;

import com.example.CinemaApplication.enums.RoomStatus;
import com.example.CinemaApplication.enums.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;
    @Column(name="room_name",nullable = false,unique = false,columnDefinition = "varchar(100)")
    private String name;
    @Column(name = "room_type", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private RoomType type;
    @Column(name = "total_rows", nullable = false)
    private int  totalRows;
    @Column(name = "seat_per_rows", nullable = false)
    private int seatPerRows;
    @Column(name = "status", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

}
