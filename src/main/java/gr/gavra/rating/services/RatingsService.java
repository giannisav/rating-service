package gr.gavra.rating.services;

import gr.gavra.rating.models.dto.ComputedRatingDto;
import gr.gavra.rating.models.dto.RatingDTO;

public interface RatingsService {

  RatingDTO saveRating(RatingDTO ratingDto);

  ComputedRatingDto getRatingFor(String ratedEntity);
}
