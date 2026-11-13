package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.ShowtimeRequest;
import com.example.CinemaApplication.dto.response.ShowtimeResponse;

import java.util.List;

public interface ShowtimeService {

    List<ShowtimeResponse> getPublicShowtimesByMovie(Long movieId);

    ShowtimeResponse getShowtimeById(Long id);

    List<ShowtimeResponse> getAllShowtimes();

    ShowtimeResponse createShowtime(ShowtimeRequest request);

    ShowtimeResponse updateShowtime(Long id, ShowtimeRequest request);

    void cancelShowtimeAndBookings(Long id);
}
