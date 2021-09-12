package gr.gavra.rating.models.dto;

import static gr.gavra.rating.models.dto.RatingDTO.RatingDTOValidationResult.EMPTY_RATED_ENTITY;
import static gr.gavra.rating.models.dto.RatingDTO.RatingDTOValidationResult.NOT_ACCEPTED_GIVEN_RATING;
import static gr.gavra.rating.models.dto.RatingDTO.RatingDTOValidationResult.VALID;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {

  public enum RatingDTOValidationResult {
    VALID("Valid"),
    EMPTY_RATED_ENTITY("Rated Entity should not be null or empty."),
    NOT_ACCEPTED_GIVEN_RATING("Given rating is null or has not any of the accepted values.");

    private String result;

    RatingDTOValidationResult(String result) {
      this.result = result;
    }

    public String getResult() {
      return this.result;
    }
  }

  private final static List<Double> VALID_RATING_VALUES =
      Arrays.asList(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0);

  private Double givenRating;
  private String ratedEntity;
  private String rater;
  private LocalDateTime createdAt;

  public RatingDTOValidationResult isValid() {
    if (!givenRatingIsValid()) {
      return NOT_ACCEPTED_GIVEN_RATING;
    }
    if (!ratedEntityIsValid()) {
      return EMPTY_RATED_ENTITY;
    }
    return VALID;
  }

  private boolean ratedEntityIsValid() {
    return this.ratedEntity != null && this.ratedEntity.length() > 0;
  }

  private boolean givenRatingIsValid() {
    return VALID_RATING_VALUES.contains(this.givenRating);
  }
}
