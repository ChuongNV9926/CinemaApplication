package com.example.CinemaApplication.dto.response;

import com.example.CinemaApplication.enums.AgeRating;
import com.example.CinemaApplication.enums.MovieStatus;

import java.time.Instant;
import java.util.List;

public record MovieResponse(
        Long id,
        String title,
        String director,
        int duration,
        Instant releaseDate,
        AgeRating ageRating,
        String description,
        String posterUrl,
        MovieStatus status,
        List<GenreResponse> genres
) {
}
