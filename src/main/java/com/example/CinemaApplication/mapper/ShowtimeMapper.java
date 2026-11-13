package com.example.CinemaApplication.mapper;

import com.example.CinemaApplication.dto.response.ShowtimeResponse;
import com.example.CinemaApplication.model.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShowtimeMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieTitle", source = "movie.title")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    ShowtimeResponse toResponse(Showtime showtime);
}
