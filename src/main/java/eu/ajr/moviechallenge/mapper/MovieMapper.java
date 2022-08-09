package eu.ajr.moviechallenge.mapper;

import eu.ajr.moviechallenge.dto.MovieDto;
import eu.ajr.moviechallenge.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface MovieMapper {

    MovieDto toDto(Movie movie);
    Movie toEntity(MovieDto movieDto);
}
