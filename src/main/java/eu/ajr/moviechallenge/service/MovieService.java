package eu.ajr.moviechallenge.service;

import eu.ajr.moviechallenge.dto.MovieDTO;
import eu.ajr.moviechallenge.entity.Movie;
import eu.ajr.moviechallenge.mapper.MovieMapper;
import eu.ajr.moviechallenge.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class MovieService {

    private static final String ERROR_MOVIE_NOT_FOUND = "Movie not found";

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    public MovieDTO save(MovieDTO movie) {
        Movie createdMovie = this.movieRepository.save(this.movieMapper.toEntity(movie));
        return this.movieMapper.toDto(createdMovie);
    }

    public MovieDTO update(UUID uuid, MovieDTO movieDto) throws Exception {
        Movie movieBd = this.movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new Exception(ERROR_MOVIE_NOT_FOUND));
        Movie movieToUpdate = this.movieMapper.toEntity(movieDto);
        movieToUpdate.setId(movieBd.getId());
        return this.movieMapper.toDto(this.movieRepository.save(movieToUpdate));
    }

    public void delete(UUID uuid) throws Exception {
        Movie movie = this.movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new Exception(ERROR_MOVIE_NOT_FOUND));

        this.movieRepository.delete(movie);
    }

    public MovieDTO findByUuid(UUID uuid) throws Exception {
        return this.movieMapper.toDto(
                this.movieRepository.findByUuid(uuid)
                        .orElseThrow(() -> new Exception(ERROR_MOVIE_NOT_FOUND))
        );
    }

    public Page<MovieDTO> findMoviesByLaunchDate(LocalDate beginDate, LocalDate endDate, Pageable pageable) {

        throw new IllegalArgumentException("teste de excepção");

/*        Page<Movie> pageMoviesBd;
        if (beginDate != null && endDate != null) {
            pageMoviesBd =  this.movieRepository.findByReleaseDateBetween(beginDate, endDate, pageable);
            return pageMoviesBd.map(movie -> movieMapper.toDto(movie));
        }

        if (beginDate != null) {
            pageMoviesBd = this.movieRepository.findByReleaseDateAfter(beginDate, pageable);
            return pageMoviesBd.map(movie -> movieMapper.toDto(movie));
        }

        if (endDate != null) {
            pageMoviesBd = this.movieRepository.findByReleaseDateBefore(endDate, pageable);

            return pageMoviesBd.map(movie -> movieMapper.toDto(movie));
        }

        pageMoviesBd = this.movieRepository.findAll(pageable);
        return pageMoviesBd.map(movie -> movieMapper.toDto(movie));*/
    }

    public Page<MovieDTO>  findByMovieTitle(String titleSearch, Pageable pageable) {
        Page<Movie> pageMoviesBd = this.movieRepository.findByTitleContainingIgnoreCase(titleSearch, pageable);
        return pageMoviesBd.map(movie -> movieMapper.toDto(movie));
    }

}
