package eu.ajr.moviechallenge.controller;

import eu.ajr.moviechallenge.dto.MovieDTO;
import eu.ajr.moviechallenge.entity.Movie;
import eu.ajr.moviechallenge.service.MovieService;
import eu.ajr.moviechallenge.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
@Tag(name = "Movie service")
@RestController
@RequestMapping("movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ValidationUtil validation;

    @Operation(summary = "Register new movie data in the movie database")
    @PostMapping
    @Transactional
    public ResponseEntity<MovieDTO> create(@Valid @RequestBody MovieDTO movieDto) {
        MovieDTO createdMovieDto = this.movieService.save(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovieDto);
    }

    @Operation(summary = "Delete movie data from the movie database")
    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<Object> delete(@PathVariable(value = "uuid") UUID uuid) {
        try {
            this.movieService.delete(uuid);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body("Movie deleted with success");
    }

    @Operation(summary = "Update movie data")
    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<MovieDTO> update(@PathVariable(value = "uuid") UUID uuid,
                                        @Valid @RequestBody MovieDTO movieDto) {
        if (!movieDto.getUuid().equals(uuid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        MovieDTO movieUpdated;
        try {
            movieUpdated = this.movieService.update(uuid, movieDto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movieUpdated);
    }

    @Operation(summary = "Get movie data")
    @GetMapping("/{uuid}")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable UUID uuid) {
        MovieDTO movieDto;
        try {
            movieDto = this.movieService.findByUuid(uuid);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movieDto);
    }

    @Operation(summary = "Search for movies by release date")
    @GetMapping("/searchByDate")
    public ResponseEntity<Page<MovieDTO>> searchByDate (
            @Parameter(description = "Release date after", example = "1968-12-25")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(sort = {"rank", "title"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable
    ) {
        // if any sort object refers to non-existent field in the Movie entity class
        // returns http status 400
        if (!pageable.getSort().get().allMatch(order ->
                this.validation.validateModelField(Movie.class, order.getProperty()))) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(this.movieService.findMoviesByLaunchDate(beginDate, endDate, pageable));
    }

    @Operation(summary = "Search for movies by movie title")
    @GetMapping("/searchByTitle")
    public ResponseEntity<Page<MovieDTO>> searchByTitle (
            @RequestParam String title,
            @PageableDefault(sort = {"title"}, direction = Sort.Direction.ASC, size = 20) Pageable pageable
    ) {

        // if any sort object refers to non-existent field in the Movie entity class
        // returns http status 400
        if (!pageable.getSort().get().allMatch(order ->
                this.validation.validateModelField(Movie.class, order.getProperty()))) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(this.movieService.findByMovieTitle(title, pageable));
    }

}
