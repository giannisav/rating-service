package gr.gavra.rating.mappers;

import gr.gavra.rating.models.db.Rating;
import gr.gavra.rating.models.dto.RatingDTO;
import java.time.LocalDateTime;

public class RatingMapper {

  public static Rating toRating(RatingDTO ratingDTO) {
    if (ratingDTO == null) {
      return null;
    }
    var rating = new Rating();
    rating.setGivenRating(ratingDTO.getGivenRating());
    rating.setRatedEntity(ratingDTO.getRatedEntity());
    rating.setRater(ratingDTO.getRater());
    rating.setCreatedAt(LocalDateTime.now());

    return rating;
  }

  public static RatingDTO toRatingDTO(Rating rating) {
    if (rating == null) {
      return null;
    }
    var ratingDTO = new RatingDTO();
    ratingDTO.setCreatedAt(rating.getCreatedAt());
    ratingDTO.setGivenRating(rating.getGivenRating());
    ratingDTO.setRatedEntity(rating.getRatedEntity());
    ratingDTO.setRater(rating.getRater());
    return ratingDTO;
  }
}