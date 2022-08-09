package eu.ajr.moviechallenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.UUID;

@Data
@Entity
@Table(
        name = "movie",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"uuid"}, name = "movie_unique_uuid")
        }
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Movie implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false, unique = true)
    private UUID uuid;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "rank")
    private int rank;

    @Column(name = "revenue")
    private BigDecimal revenue;

    @PrePersist
    private void setUuid() {
        uuid = UUID.randomUUID();
    }
}
