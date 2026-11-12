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
    @Column(name = "movie_id")
    private Long id;
    @Column(name = "title", nullable = false,columnDefinition = "varchar(200)")
    private String title;
    @Column(name = "director", nullable = false,columnDefinition = "varchar(100)")
    private String director;
    @Column(nullable = false,name = "duration")
    private int duration;
    @Column(nullable = false,name = "release_date")
    private Instant releaseDate;
    @Column(nullable = false,name = "age_rating",columnDefinition = "varchar(10)")
    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;
    @Column(name = "description", nullable = false,columnDefinition = "varchar(4000)")
    private String description;
    @Column(name = "poster_url", nullable = false,columnDefinition = "varchar(1000)")
    private String posterUrl;
    @ManyToMany
    @JoinTable(name = "movie_genres",
               joinColumns = @JoinColumn(name =  "movie_id"),
               inverseJoinColumns = @JoinColumn(name="genre_id",referencedColumnName = "genre_id"))
     private List<Genre> genres;
    @Column(name = "status", nullable = false,columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private MovieStatus status;
}
