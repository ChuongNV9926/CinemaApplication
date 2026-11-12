package com.example.CinemaApplication.mapper;

import com.example.CinemaApplication.dto.response.GenreResponse;
import com.example.CinemaApplication.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {
    @Mapping(target = "id",source = "id")
    @Mapping(target = "genreName",source = "name")
    @Mapping(target = "description",source="description")
    GenreResponse toResponse(Genre genre);
}
