package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.GenreRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.GenreResponse;
import com.example.CinemaApplication.model.Genre;
import com.example.CinemaApplication.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class GenreController {
    private final GenreService genreService;
    @GetMapping("/genre")
    public ResponseEntity<ApiResponse<List<Genre>>> listGenre(){
        return ResponseEntity.ok().body(ApiResponse.success("Get Genre List Successfully",genreService.findAll()));
    }
    @PostMapping("/genre")
    public ResponseEntity<ApiResponse<GenreResponse>> create(@RequestBody @Valid GenreRequest genreRequest){
        return ResponseEntity.ok().
                body(ApiResponse.success("Create genre successfully.",genreService.create(genreRequest)));
    }
    @PutMapping("/genre/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> update(@PathVariable("id") Long id,
                                                             @RequestBody GenreRequest genreRequest ){
        return ResponseEntity.ok(
                ApiResponse.success("Updated Successfully", genreService.update(id,genreRequest)));
    }
    @DeleteMapping("/genre/{id}")
        public ResponseEntity<ApiResponse<GenreResponse>> delete(@PathVariable("id") Long id){
             return ResponseEntity.ok(ApiResponse.success(("Deleted successfully"),genreService.delte(id)));
        }

}
