package eu.ajr.moviechallenge.repository;

import eu.ajr.moviechallenge.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findAll(Pageable pageable);

    Optional<Movie> findByUuid(UUID uuid);
}
