package com.example.CinemaApplication.model;

import com.example.CinemaApplication.enums.AccountStatus;
import com.example.CinemaApplication.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="account_id")
    private Long id;
    @Column(name = "username", nullable = false,unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "role", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "status", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @Column(name = "token_version", nullable = false, columnDefinition = "integer default 0")
    private int tokenVersion;
    public Account( String username,String password, Role role,AccountStatus status) {
        this.password = password;
        this.username = username;
        this.role = role;
        this.status=status;
    }
}
