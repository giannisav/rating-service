package gr.gavra.rating.models.db;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
  private Double givenRating;

  @Column(name = "rated_entity", nullable = false)
  private String ratedEntity;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "rater", nullable = true)
  private String rater;

  @PrePersist
  public void onPrePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
