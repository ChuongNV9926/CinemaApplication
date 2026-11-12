package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.GenreRequest;
import com.example.CinemaApplication.dto.response.GenreResponse;
import com.example.CinemaApplication.model.Genre;

import java.util.List;

public interface GenreService {
         GenreResponse create(GenreRequest genreRequest);
         List<Genre> findAll();
         GenreResponse update(Long id,GenreRequest genreRequest);
         GenreResponse delte(Long id);
}
