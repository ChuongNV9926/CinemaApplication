package com.example.CinemaApplication.dto.response;

import jakarta.validation.constraints.NotBlank;

public record GenreResponse( Long id,
                             String genreName,
                            String description) {
}
