package gr.xe.rating.service.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RatingDto {

    private Double givenRating;

    private String ratedEntity;

    private String rater;

    private LocalDate createdAt;
}
