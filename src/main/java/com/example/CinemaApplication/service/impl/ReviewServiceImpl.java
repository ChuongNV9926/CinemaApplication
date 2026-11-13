package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.ReviewRequest;
import com.example.CinemaApplication.dto.response.ReviewResponse;
import com.example.CinemaApplication.enums.BookingStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.model.Booking;
import com.example.CinemaApplication.model.Customer;
import com.example.CinemaApplication.model.Movie;
import com.example.CinemaApplication.model.Review;
import com.example.CinemaApplication.model.ReviewId;
import com.example.CinemaApplication.repository.AccountRepository;
import com.example.CinemaApplication.repository.BookingRepository;
import com.example.CinemaApplication.repository.CustomerRepository;
import com.example.CinemaApplication.repository.MovieRepository;
import com.example.CinemaApplication.repository.ReviewRepository;
import com.example.CinemaApplication.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMovieReviews(Long movieId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByMovie_IdOrderByReviewDateDesc(movieId, pageable);
        List<ReviewResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public ReviewResponse writeOrUpdateReview(String username, Long movieId, ReviewRequest request) {
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));
        Customer customer = customerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new BusinessException("Hồ sơ khách hàng chưa có!"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim ID: " + movieId));

        // BR-16: Phải có đơn đặt vé CONFIRMED với suất chiếu đã kết thúc mới được đánh giá phim
        List<Booking> bookings = bookingRepository.findByCustomer_IdOrderByBookingDateDesc(customer.getId());
        boolean hasWatchedMovie = bookings.stream().anyMatch(b ->
                b.getStatus() == BookingStatus.CONFIRMED
                        && b.getShowtime().getMovie().getId().equals(movieId)
                        && b.getShowtime().getEndTime().isBefore(LocalDateTime.now())
        );

        if (!hasWatchedMovie) {
            throw new BusinessException("Bạn chỉ được đánh giá phim sau khi đã xem suất chiếu hoàn tất với đơn vé đã xác nhận!");
        }

        // Mỗi khách hàng 1 đánh giá cho 1 phim, cho phép sửa
        Review review = reviewRepository.findById_CustomerIdAndId_MovieId(customer.getId(), movieId)
                .orElseGet(() -> {
                    Review newReview = new Review();
                    newReview.setId(new ReviewId(customer.getId(), movieId));
                    newReview.setCustomer(customer);
                    newReview.setMovie(movie);
                    return newReview;
                });

        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        review.setReviewDate(LocalDate.now());

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMovieAverageRating(Long movieId) {
        Double avg = reviewRepository.calculateAverageRating(movieId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getCustomer().getId(),
                review.getCustomer().getFullName(),
                review.getMovie().getId(),
                review.getMovie().getTitle(),
                review.getRating(),
                review.getComment(),
                review.getReviewDate()
        );
    }
}
