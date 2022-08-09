package eu.ajr.moviechallenge.controller;

import eu.ajr.moviechallenge.dto.MovieDto;
import eu.ajr.moviechallenge.entity.Movie;
import eu.ajr.moviechallenge.service.MovieService;
import eu.ajr.moviechallenge.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ResponseEntity<MovieDto> create(@Valid @RequestBody MovieDto movieDto) {
        MovieDto createdMovieDto = this.movieService.save(movieDto);
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
    public ResponseEntity<MovieDto> update(@PathVariable(value = "uuid") UUID uuid,
                                        @Valid @RequestBody MovieDto movieDto) {
        if (!movieDto.getUuid().equals(uuid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        MovieDto movieUpdated;
        try {
            movieUpdated = this.movieService.update(uuid, movieDto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movieUpdated);
    }

    @Operation(summary = "Get movie data")
    @GetMapping("/{uuid}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable UUID uuid) {
        MovieDto movieDto;
        try {
            movieDto = this.movieService.findByUuid(uuid);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movieDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad Request - check the search parameters")
    })
    @Parameter(name = "beginDate", description = "Release date after", example = "1968-12-25")
    @Parameter(name = "endDate",description = "Release date before", example = "1968-12-25")
    @Parameter(name = "pageable",description = "Sort options", example = "{\n" +
            "  \"page\": 0,\n" +
            "  \"size\": 30,\n" +
            "  \"sort\": [\n" +
            "    \"rank\"\n" +
            "  ]\n" +
            "}")
    @Operation(summary = "Search for movies by release date")
    @GetMapping("/searchByDate")
    public ResponseEntity<Page<MovieDto>> searchByDate (
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

        return ResponseEntity.ok(this.movieService.findMoviesByReleaseDate(beginDate, endDate, pageable));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad Request - check the search parameters")
    })
    @Parameter(name = "title", description = "Movie title")
    @Parameter(name = "pageable",description = "Sort options", example = "{\n" +
            "  \"page\": 0,\n" +
            "  \"size\": 30,\n" +
            "  \"sort\": [\n" +
            "    \"rank\"\n" +
            "  ]\n" +
            "}")
    @Operation(summary = "Search for movies by movie title")
    @GetMapping("/searchByTitle")
    public ResponseEntity<Page<MovieDto>> searchByTitle (
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
