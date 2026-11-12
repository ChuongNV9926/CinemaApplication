package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.GenreRequest;
import com.example.CinemaApplication.dto.response.GenreResponse;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.mapper.GenreMapper;
import com.example.CinemaApplication.model.Genre;
import com.example.CinemaApplication.repository.GenreRepository;
import com.example.CinemaApplication.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImplement implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    @Override
    public List<Genre> findAll() {
        return genreRepository.findAllOrderByIdAsc();

    }
    @Override
    @Transactional
    public GenreResponse create(GenreRequest genreRequest){
        Genre genre=Genre.builder().name(genreRequest.genreName()).
                description(genreRequest.description()).build();
        genreRepository.save(genre);
        return genreMapper.toResponse(genre);

    }



    @Override
    @Transactional
    public GenreResponse update(Long id, GenreRequest genreRequest) {
        Genre genre= find(id);
        if(genreRepository.existsByNameIgnoreCaseAndIdNot(genreRequest.genreName(),id)){
            throw new BusinessException("Genre Name already Existed");
        }
        genre.setName(genreRequest.genreName());
        genre.setDescription(genreRequest.description());
        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional
    public GenreResponse delte(Long id) {
        Genre genre= find(id);
        if(genre.getMovies().size()>0) {
            throw new BusinessException("Cannot delete genre because it ís associated with existing movie");
        }
        genreRepository.delete(genre);
        return genreMapper.toResponse(genre);
    }
    public Genre find(Long id){
        return    genreRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not Found Genre Id: "+id));

    }
}
