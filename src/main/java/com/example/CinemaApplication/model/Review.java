package com.example.CinemaApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

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
   @Column(name = "rating", nullable = false)
   private int rating;
   @Column(name = "comment", nullable = false,columnDefinition = "varchar(2000)")
    private  String comment;
   @Column(name = "reviewDate", nullable = false)
    private LocalDate reviewDate;
   @PrePersist
    protected void onCreate(){
       reviewDate= LocalDate.now();
   }
}
