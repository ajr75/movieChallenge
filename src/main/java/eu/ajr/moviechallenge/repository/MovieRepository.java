package eu.ajr.moviechallenge.repository;

import eu.ajr.moviechallenge.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends PagingAndSortingRepository<Movie, Long> {

    Page<Movie> findAll(Pageable pageable);

    Optional<Movie> findByUuid(UUID uuid);

    Page<Movie> findByReleaseDateBetween(LocalDate beginDate, LocalDate endDate, Pageable pageable);

    Page<Movie> findByReleaseDateBefore(LocalDate date, Pageable pageable);

    Page<Movie> findByReleaseDateAfter(LocalDate date, Pageable pageable);

    Page<Movie> findByTitleContainingIgnoreCase(String titleSearch, Pageable pageable);
}
