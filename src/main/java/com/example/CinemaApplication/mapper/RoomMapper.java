package com.example.CinemaApplication.mapper;

import com.example.CinemaApplication.dto.response.RoomResponse;
import com.example.CinemaApplication.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "totalSeats", expression = "java(room.getTotalRows() * room.getSeatPerRows())")
    RoomResponse toResponse(Room room);
}
