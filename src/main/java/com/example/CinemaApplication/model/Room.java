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
    private Long id;
    @Column(nullable = false,unique = false)
    private String name;
    @Column(nullable = false)
    private RoomType type;
    @Column(nullable = false)
    private int  totalRows;
    @Column(nullable = false)
    private int seatPerRows;
    @Column(nullable = false)
    private RoomStatus status;

}
