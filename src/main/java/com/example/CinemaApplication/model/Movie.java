package com.example.CinemaApplication.model;

import com.example.CinemaApplication.enums.AgeRating;
import com.example.CinemaApplication.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name="movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String director;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false)
    private Instant release_date;
    @Column(nullable = false)
    private AgeRating ageRating;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String poster_url;
    @ManyToMany
    @JoinTable(name = "movie_genres",
               joinColumns = @JoinColumn(name =  "movie_id"),
               inverseJoinColumns = @JoinColumn(name="genre_id",referencedColumnName = "id"))
     private List<Genre> genreList;
    @Column(nullable = false)
    private MovieStatus status;
}
