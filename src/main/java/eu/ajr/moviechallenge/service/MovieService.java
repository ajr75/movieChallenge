package eu.ajr.moviechallenge.service;

import eu.ajr.moviechallenge.entity.Movie;
import eu.ajr.moviechallenge.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public Movie save(Movie movie) {
        return this.movieRepository.save(movie);
    }

    public Movie update(UUID uuid, Movie movie) throws Exception {
        Movie movieBd = this.movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new Exception("Movie not found"));
        movie.setId(movieBd.getId());
        return this.movieRepository.save(movie);
    }

    public void delete(UUID uuid) throws Exception {
        Movie movie = this.movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new Exception("Movie not found"));

        this.movieRepository.delete(movie);
    }

}
