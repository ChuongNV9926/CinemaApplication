package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.ReviewRequest;
import com.example.CinemaApplication.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Page<ReviewResponse> getMovieReviews(Long movieId, Pageable pageable);

    ReviewResponse writeOrUpdateReview(String username, Long movieId, ReviewRequest request);

    Double getMovieAverageRating(Long movieId);
}
