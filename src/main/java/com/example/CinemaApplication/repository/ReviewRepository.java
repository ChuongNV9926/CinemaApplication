package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.model.Review;
import com.example.CinemaApplication.model.ReviewId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    Page<Review> findByMovie_IdOrderByReviewDateDesc(Long movieId, Pageable pageable);

    Optional<Review> findById_CustomerIdAndId_MovieId(Long customerId, Long movieId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Double calculateAverageRating(@Param("movieId") Long movieId);
}
