package eu.ajr.moviechallenge.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "movie",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"uuid"}, name = "movie_unique_uuid")
        }
)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "UUID NOT NULL UNIQUE", updatable = false)
    private UUID uuid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "launch_date")
    private Date launchDate;

    @Column(name = "rank")
    private int rank;

    @Column(name = "revenue")
    private BigDecimal revenue;

    @PrePersist
    private void setUuid() {
        uuid = UUID.randomUUID();
    }
}
