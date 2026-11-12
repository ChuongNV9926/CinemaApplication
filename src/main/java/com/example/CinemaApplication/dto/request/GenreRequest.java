package com.example.CinemaApplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequest(
        @NotBlank @Size(max = 100,message = "Genre Name must be contain at most 100 character!")
        String genreName,
        @NotBlank @Size(max = 1000,message = "Genre description must be contain at most 1000 character!")
        String description) {
}
