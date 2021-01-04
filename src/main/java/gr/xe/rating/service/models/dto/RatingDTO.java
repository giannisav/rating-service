package gr.xe.rating.service.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RatingDto {

    private Double givenRating;

    private String ratedEntity;

    private String rater;

    private LocalDateTime createdAt;
}
