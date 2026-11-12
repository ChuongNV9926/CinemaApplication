package com.example.CinemaApplication.dto.response;

import org.springframework.data.domain.Sort;

import java.util.List;

public record PageReponse<T>(List<T> item,
                             int page,
                             int size,
                             Long totalElement,
                             int totalPages,
                             Sort sort) {
}
