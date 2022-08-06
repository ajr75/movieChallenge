package eu.ajr.moviechallenge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MovieDTO {

    private UUID uuid;
    private String title;
    private Date releaseDate;
    private int rank;
    private BigDecimal revenue;
}
