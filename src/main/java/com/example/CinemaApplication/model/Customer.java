package com.example.CinemaApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String full_name;
    @Column(nullable = false,length = 10,unique = true)
    private String phone;
    @Column(nullable = false)
    private Instant birthday;
    @Column(nullable = false,length = 12,unique = true)
    private String identityCard;
    @Column(nullable = false,unique = true)
    private String email;
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
