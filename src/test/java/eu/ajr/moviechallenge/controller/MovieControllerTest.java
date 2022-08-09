package eu.ajr.moviechallenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.ajr.moviechallenge.dto.MovieDto;
import eu.ajr.moviechallenge.service.MovieService;
import eu.ajr.moviechallenge.util.ValidationUtil;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MovieController.class)
public class MovieControllerTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieControllerTest.class);
    private static final String REQUEST_ERROR = "Not capable of performing request to {}";
    private static final String URL = "/movie";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieServiceMock;

    @MockBean
    private ValidationUtil validationUtilMock;

    private String jsonPost;

    private MovieDto movieDto;

    private Page<MovieDto> pageMovieDto;

    @Before
    @Override
    public void setUp() throws JsonProcessingException {

        this.movieDto = new MovieDto();
        this.movieDto.setUuid(UUID.randomUUID());
        this.movieDto.setRank(8);
        this.movieDto.setRevenue(BigDecimal.TEN);
        this.movieDto.setReleaseDate(LocalDate.now());
        this.movieDto.setTitle("2001 Space Odyssey");

        List<MovieDto> movieList = Collections.singletonList(this.movieDto);

        this.pageMovieDto = new PageImpl<>(movieList);


        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        jsonPost = objectMapper.writeValueAsString(this.movieDto);
    }

    @Test
    public void testFindByTitleThenSuccess() {
        when(this.movieServiceMock.findByMovieTitle(anyString(), any(Pageable.class))).thenReturn(this.pageMovieDto);
        when(this.validationUtilMock.validateModelField(any(), anyString())).thenReturn(Boolean.TRUE);
        try {
            this.mockMvc
                    .perform(get(URL + "/searchByTitle?title=test"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            LOGGER.error(REQUEST_ERROR, URL + "searchBYTitle?title=test");
        }
        verify(this.movieServiceMock, times(1)).findByMovieTitle(anyString(), any(Pageable.class));
        verify(this.validationUtilMock, times(1)).validateModelField(any(), anyString());
    }

    @Test
    public void testFindByDateThenSuccess() {
        when(this.movieServiceMock.findMoviesByReleaseDate(any(LocalDate.class), any(LocalDate.class), any(Pageable.class))).thenReturn(this.pageMovieDto);
        when(this.validationUtilMock.validateModelField(any(), anyString())).thenReturn(Boolean.TRUE);
        try {
            this.mockMvc
                    .perform(get(URL + "/searchByDate?beginDate=" + LocalDate.of(1980, 12, 12) + "&endDate=" + LocalDate.now()))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            LOGGER.error(REQUEST_ERROR, URL + "/searchByDate?beginDate=" + LocalDate.of(1980, 12, 12) + "&endDate=" + LocalDate.now());
        }
        verify(this.movieServiceMock, times(1)).findMoviesByReleaseDate(any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
        verify(this.validationUtilMock, times(2)).validateModelField(any(), anyString());
    }

    @Test
    public void testCreateThenSuccess() {
        when(this.movieServiceMock.save(any())).thenReturn(this.movieDto);

        try {
            this.mockMvc
                    .perform(post(URL)
                            .content(jsonPost)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            LOGGER.error(REQUEST_ERROR, URL);
        }

        verify(this.movieServiceMock, times(1)).save(any());
    }

    @Test
    public void testDeleteThenSuccess() throws Exception {
        doNothing().when(this.movieServiceMock).delete(any(UUID.class));
        try {
            this.mockMvc
                    .perform(delete(URL + "/" + this.movieDto.getUuid()))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            LOGGER.error(REQUEST_ERROR, URL);
        }

        verify(this.movieServiceMock, times(1)).delete(this.movieDto.getUuid());
    }

    @Test
    public void testUpdateThenSuccess() throws Exception {

        when(this.movieServiceMock.update(any(UUID.class), any(MovieDto.class))).thenReturn(this.movieDto);

        try {
            this.mockMvc
                    .perform(put(URL + "/{uuid}", this.movieDto.getUuid())
                            .content(jsonPost)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rank", is(this.movieDto.getRank())))
                    .andExpect(jsonPath("$.uuid", is(this.movieDto.getUuid().toString())));
        } catch (Exception e) {
            LOGGER.error(REQUEST_ERROR, URL);
        }

        verify(this.movieServiceMock, times(1)).update(any(UUID.class), any(MovieDto.class));
    }

    @Test
    public void testGetMovieThenSuccess() throws Exception {
        given(this.movieServiceMock.findByUuid(any(UUID.class))).willReturn(this.movieDto);

        try {
            this.mockMvc
                    .perform(get(URL + "/" + this.movieDto.getUuid()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rank", is(this.movieDto.getRank())))
                    .andExpect(jsonPath("$.uuid", is(this.movieDto.getUuid().toString())));
        } catch (Exception e) {
            LOGGER.error(REQUEST_ERROR, URL);
        }

        verify(this.movieServiceMock, times(1)).findByUuid(any(UUID.class));
    }

}
