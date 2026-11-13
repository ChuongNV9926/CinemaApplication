package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.MovieRequest;
import com.example.CinemaApplication.dto.response.MovieResponse;
import com.example.CinemaApplication.enums.MovieStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.mapper.MovieMapper;
import com.example.CinemaApplication.model.Genre;
import com.example.CinemaApplication.model.Movie;
import com.example.CinemaApplication.repository.GenreRepository;
import com.example.CinemaApplication.repository.MovieRepository;
import com.example.CinemaApplication.repository.ShowtimeRepository;
import com.example.CinemaApplication.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getPublicShowingMovies() {
        return movieRepository.findByStatusOrderByReleaseDateDesc(MovieStatus.SHOWING)
                .stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));
        return movieMapper.toResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> searchMovies(String keyword, Long genreId, MovieStatus status, Pageable pageable) {
        // Lấy danh sách phim theo filter
        List<Movie> movies;
        if (keyword != null && !keyword.isBlank() && genreId != null && status != null) {
            movies = movieRepository.searchMovie(keyword.trim(), genreId, status.name(), pageable);
        } else if (status != null) {
            movies = movieRepository.findByStatusOrderByReleaseDateDesc(status);
        } else {
            movies = movieRepository.findAll();
        }

        List<MovieResponse> responses = movies.stream().map(movieMapper::toResponse).toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        List<Genre> genres = genreRepository.findAllById(request.genreIds());
        if (genres.isEmpty()) {
            throw new BusinessException("Thể loại phim không hợp lệ hoặc không tồn tại");
        }

        Movie movie = new Movie();
        movie.setTitle(request.title().trim());
        movie.setDirector(request.director().trim());
        movie.setDuration(request.duration());
        movie.setReleaseDate(request.releaseDate());
        movie.setAgeRating(request.ageRating());
        movie.setDescription(request.description().trim());
        movie.setPosterUrl(request.posterUrl().trim());
        movie.setStatus(request.status());
        movie.setGenres(genres);

        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(savedMovie);
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));

        List<Genre> genres = genreRepository.findAllById(request.genreIds());
        if (genres.isEmpty()) {
            throw new BusinessException("Thể loại phim không hợp lệ");
        }

        movie.setTitle(request.title().trim());
        movie.setDirector(request.director().trim());
        movie.setDuration(request.duration());
        movie.setReleaseDate(request.releaseDate());
        movie.setAgeRating(request.ageRating());
        movie.setDescription(request.description().trim());
        movie.setPosterUrl(request.posterUrl().trim());
        movie.setStatus(request.status());
        movie.setGenres(genres);

        return movieMapper.toResponse(movie);
    }

    @Override
    @Transactional
    public void deleteOrStopMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));

        // BR-06: Phim chưa có suất chiếu thì xóa hẳn; đã có suất chiếu thì chuyển STOPPED
        boolean hasShowtime = showtimeRepository.existsByMovie_Id(id);
        if (hasShowtime) {
            movie.setStatus(MovieStatus.STOPPED);
            movieRepository.save(movie);
        } else {
            movieRepository.delete(movie);
        }
    }
}
