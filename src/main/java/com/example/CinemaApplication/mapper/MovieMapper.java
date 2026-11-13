package com.example.CinemaApplication.mapper;

import com.example.CinemaApplication.dto.response.MovieResponse;
import com.example.CinemaApplication.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {GenreMapper.class})
public interface MovieMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "genres", source = "genres")
    MovieResponse toResponse(Movie movie);
}
