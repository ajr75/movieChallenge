package eu.ajr.moviechallenge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MovieDto {

    private UUID uuid;
    private String title;
    private LocalDate releaseDate;
    private int rank;
    private BigDecimal revenue;
}
