package gr.xe.rating.service.models.db;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ratings",
    indexes = {
            @Index(name = "ratings_entity_idx", columnList = "rated_entity", unique = false),
            @Index(name = "ratings_created_at_idx", columnList = "created_at", unique = false),
            @Index(name = "ratings_rater_idx", columnList = "rater", unique = false)
    }
)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "given_rating", nullable = false)
    private double givenRating;

    @Column(name = "rated_entity", nullable = false)
    private String ratedEntity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "rater", nullable = true)
    private String rater;
}
