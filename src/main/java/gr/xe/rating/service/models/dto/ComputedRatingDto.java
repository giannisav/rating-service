package gr.xe.rating.service.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComputedRatingDto {

    private Integer numOfRatings;

    private Double overallRating;
}
