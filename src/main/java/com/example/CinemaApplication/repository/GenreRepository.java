package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GenreRepository extends JpaRepository<Genre,Long> {
    boolean existsByNameIgnoreCase(String genreName);
    boolean existsByNameIgnoreCaseAndIdNot(String genreName,Long id );
    List<Genre> findAllOrderByIdAsc();
}
