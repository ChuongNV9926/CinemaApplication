package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.enums.MovieStatus;
import com.example.CinemaApplication.model.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MovieRepository extends JpaRepository<Movie,Long> {
    List<Movie> findByStatusOrderByReleaseDateDesc(MovieStatus status);
    boolean existsByGenres_id(Long genreId);
    int countByStatus(MovieStatus status);
    @Query("""
            Select DISTINCT m
            From Movie m JOIN FETCH m.genres genre
            Where LOWER(m.title) LIKE LOWER(CONCAT('%',:keyword,'%')) AND
            m.status = :status AND genre.id = :genreId
            """)
    List<Movie> searchMovie(@Param("keyword") String keyword, @Param("genreId") Long genreId, @Param("status") String status, Pageable pageable);
}
