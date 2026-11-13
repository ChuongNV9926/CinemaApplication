package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.MovieRequest;
import com.example.CinemaApplication.dto.response.MovieResponse;
import com.example.CinemaApplication.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {

    List<MovieResponse> getPublicShowingMovies();

    MovieResponse getMovieById(Long id);

    Page<MovieResponse> searchMovies(String keyword, Long genreId, MovieStatus status, Pageable pageable);

    MovieResponse createMovie(MovieRequest request);

    MovieResponse updateMovie(Long id, MovieRequest request);

    void deleteOrStopMovie(Long id);
}
