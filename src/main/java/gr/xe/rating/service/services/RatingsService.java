package gr.xe.rating.service.services;

import gr.xe.rating.service.models.dto.ComputedRatingDto;
import gr.xe.rating.service.models.dto.RatingDto;

public interface RatingsService {

    RatingDto saveRating(RatingDto ratingDto);
    ComputedRatingDto getRatingFor(String ratedEntity);
}
