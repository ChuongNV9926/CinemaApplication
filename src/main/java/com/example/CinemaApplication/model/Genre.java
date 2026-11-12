package com.example.CinemaApplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "genres")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private long id;
    @Column(name = "genre_name", nullable = false,unique = true,columnDefinition = "varchar(100)")
    private String name;
    @Column(nullable = false,name = "description",columnDefinition = "varchar(1000)")
    private String description;
    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;
}
