package com.example.CinemaApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;
    @Column(name = "full_name", nullable = false,columnDefinition = "varchar(100)")
    private String fullName;
    @Column(name = "mobile", nullable = false,length = 10,unique = true,columnDefinition = "varchar(10)")
    private String phone;
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;
    @Column(name = "identity_card", nullable = false,length = 12,unique = true,columnDefinition = "varchar(12)")
    private String identityCard;
    @Column(name = "email", nullable = false,unique = true,columnDefinition = "varchar(100)")
    private String email;
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Customer(String fullName, String phone, LocalDate birthday, String identityCard, String email, Account account) {
        this.fullName = fullName;
        this.phone = phone;
        this.birthday = birthday;
        this.identityCard = identityCard;
        this.email = email;
        this.account = account;
    }
}
