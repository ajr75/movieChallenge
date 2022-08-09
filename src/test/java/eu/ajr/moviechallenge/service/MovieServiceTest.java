package eu.ajr.moviechallenge.service;

import eu.ajr.moviechallenge.dto.MovieDto;
import eu.ajr.moviechallenge.entity.Movie;
import eu.ajr.moviechallenge.mapper.MovieMapper;
import eu.ajr.moviechallenge.repository.MovieRepository;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class MovieServiceTest extends TestCase {

    @Mock
    private MovieRepository movieRepositoryMock;

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieMapper movieMapperMock;

    private Movie movie;

    private MovieDto movieDto;

    private Page<MovieDto> pageMovieDto;

    @Before
    @Override
    public void setUp() {

        this.movieDto = new MovieDto();
        this.movieDto.setUuid(UUID.randomUUID());
        this.movieDto.setRank(8);
        this.movieDto.setRevenue(BigDecimal.TEN);
        this.movieDto.setReleaseDate(LocalDate.now());
        this.movieDto.setTitle("2001 Space Odyssey");

        this.movie = new Movie();
        this.movie.setUuid(this.movieDto.getUuid());
        this.movie.setRank(this.movieDto.getRank());
        this.movie.setTitle(this.movieDto.getTitle());
        this.movie.setReleaseDate(this.movieDto.getReleaseDate());
        this.movie.setRevenue(this.movieDto.getRevenue());

        List<MovieDto> movieList = Collections.singletonList(this.movieDto);

        this.pageMovieDto = new PageImpl<>(movieList);
    }

    @Test
    public void testSaveThenSuccess() {
        when(this.movieRepositoryMock.save(any(Movie.class))).thenReturn(this.movie);
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);
        when(this.movieMapperMock.toEntity(any(MovieDto.class))).thenReturn(this.movie);

        MovieDto result = this.movieService.save(this.movieDto);
        assertEquals(this.movieDto, result);

        verify(this.movieRepositoryMock, times(1)).save(any(Movie.class));
        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
        verify(this.movieMapperMock, times(1)).toEntity((any(MovieDto.class)));
    }

    @Test
    public void testFindByUuidThenSuccess() throws Exception {
        when(this.movieRepositoryMock.findByUuid(any())).thenReturn(Optional.of(this.movie));
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);
        MovieDto result = this.movieService.findByUuid(this.movieDto.getUuid());
        assertEquals(this.movieDto, result);
        verify(this.movieRepositoryMock, times(1)).findByUuid(any());
        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
    }

    @Test
    public void testDeleteThenSuccess() throws Exception {
        when(this.movieRepositoryMock.findByUuid(any())).thenReturn(Optional.of(this.movie));
        doNothing().when(this.movieRepositoryMock).delete(any(Movie.class));
        this.movieService.delete(UUID.randomUUID());
        verify(this.movieRepositoryMock, times(1)).findByUuid(any());
        verify(this.movieRepositoryMock, times(1)).delete(any());
    }

    @Test
    public void testUpdateThenSuccess () throws Exception {
        when(this.movieRepositoryMock.findByUuid(any())).thenReturn(Optional.of(this.movie));
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);
        when(this.movieMapperMock.toEntity(any(MovieDto.class))).thenReturn(this.movie);
        when(this.movieRepositoryMock.save(any(Movie.class))).thenReturn(this.movie);

        MovieDto result = this.movieService.update(UUID.randomUUID(), this.movieDto);
        assertEquals(this.movieDto, result);

        verify(this.movieRepositoryMock, times(1)).save(any(Movie.class));
        verify(this.movieRepositoryMock, times(1)).findByUuid(any());
        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
        verify(this.movieMapperMock, times(1)).toEntity((any(MovieDto.class)));
    }

    @Test
    public void testFindByMovieTitleThenSuccess() {

        Page<Movie> pageMovie = new PageImpl<>(Collections.singletonList(this.movie));

        when(this.movieRepositoryMock.findByTitleContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(pageMovie);
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);

        Page<MovieDto> pageResult = this.movieService.findByMovieTitle("test", Pageable.unpaged());
        assertEquals(this.pageMovieDto, pageResult);

        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
        verify(this.movieRepositoryMock, times(1))
                .findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    public void testFindByReleaseDateAfterThenSuccess() {

        Page<Movie> pageMovie = new PageImpl<>(Collections.singletonList(this.movie));
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);
        when(this.movieRepositoryMock.findByReleaseDateAfter(any(LocalDate.class), any(Pageable.class))).thenReturn(pageMovie);

        Page<MovieDto> pageResult = this.movieService.findMoviesByReleaseDate(LocalDate.EPOCH, null, Pageable.unpaged());
        assertEquals(this.pageMovieDto, pageResult);

        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
        verify(this.movieRepositoryMock, times(1)).findByReleaseDateAfter(any(LocalDate.class), any(Pageable.class));

    }

    @Test
    public void testFindByReleaseDateBeforeThenSuccess() {

        Page<Movie> pageMovie = new PageImpl<>(Collections.singletonList(this.movie));
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);
        when(this.movieRepositoryMock.findByReleaseDateBefore(any(LocalDate.class), any(Pageable.class))).thenReturn(pageMovie);

        Page<MovieDto> pageResult = this.movieService.findMoviesByReleaseDate(null, LocalDate.now(), Pageable.unpaged());
        assertEquals(this.pageMovieDto, pageResult);

        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
        verify(this.movieRepositoryMock, times(1)).findByReleaseDateBefore(any(LocalDate.class), any(Pageable.class));

    }

    @Test
    public void testFindByReleaseDateBetweenThenSuccess() {
        Page<Movie> pageMovie = new PageImpl<>(Collections.singletonList(this.movie));
        when(this.movieMapperMock.toDto(any(Movie.class))).thenReturn(this.movieDto);
        when(this.movieRepositoryMock.findByReleaseDateBetween(any(LocalDate.class), any(LocalDate.class), any(Pageable.class))).thenReturn(pageMovie);

        Page<MovieDto> pageResult = this.movieService.findMoviesByReleaseDate(LocalDate.EPOCH, LocalDate.now(), Pageable.unpaged());
        assertEquals(this.pageMovieDto, pageResult);

        verify(this.movieMapperMock, times(1)).toDto((any(Movie.class)));
        verify(this.movieRepositoryMock, times(1)).findByReleaseDateBetween(any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }
}
