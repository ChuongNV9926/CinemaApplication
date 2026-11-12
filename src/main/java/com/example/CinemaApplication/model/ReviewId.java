package com.example.CinemaApplication.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class ReviewId {
    private Long customerId;
    private Long movieId;
}
