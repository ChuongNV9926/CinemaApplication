package com.example.CinemaApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
   @EmbeddedId
    private  ReviewId id;
   @MapsId("customerId")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

   @MapsId("movieId")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;
   @Column(nullable = false)
   private int rating;
   @Column(nullable = false)
    private  String comment;
   @Column(nullable = false)
    private Instant reviewDate;
   @PrePersist
    protected void onCreate(){
       reviewDate= Instant.now();
   }
}
