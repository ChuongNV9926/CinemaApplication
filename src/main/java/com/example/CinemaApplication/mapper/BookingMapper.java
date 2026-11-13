package com.example.CinemaApplication.mapper;

import com.example.CinemaApplication.dto.response.BookingResponse;
import com.example.CinemaApplication.dto.response.TicketResponse;
import com.example.CinemaApplication.model.Booking;
import com.example.CinemaApplication.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullName")
    @Mapping(target = "showtimeId", source = "showtime.id")
    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    @Mapping(target = "roomName", source = "showtime.room.name")
    @Mapping(target = "showtimeStart", source = "showtime.startTime")
    @Mapping(target = "tickets", source = "ticketList")
    BookingResponse toResponse(Booking booking);

    TicketResponse toTicketResponse(Ticket ticket);
}
