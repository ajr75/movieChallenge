package eu.ajr.moviechallenge.controller;

import eu.ajr.moviechallenge.entity.Movie;
import eu.ajr.moviechallenge.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<String> get() {

        return ResponseEntity.ok().body("Hello World");
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity delete(@PathVariable(value = "uuid") UUID uuid) {
        try {
            this.movieService.delete(uuid);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<Movie> update(@PathVariable(value = "uuid") UUID uuid,
                                        @RequestBody Movie movie) {
        if (!movie.getUuid().equals(uuid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Movie movieUpdated;
        try {
            movieUpdated = this.movieService.update(uuid, movie);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movieUpdated);
    }
}
