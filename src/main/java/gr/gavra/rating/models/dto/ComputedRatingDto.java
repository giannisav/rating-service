package gr.gavra.rating.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComputedRatingDto {

  private Integer numOfRatings;
  private Double overallRating;
}
