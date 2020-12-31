package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.RatingDto;
import org.springframework.stereotype.Component;

@Component
public final class DtoMappers {

    public RatingDto fromDbModel (Rating rating) {
        if (rating == null) {
            return null;
        }

        var ratingDto = new RatingDto();

        ratingDto.setCreatedAt(rating.getCreatedAt());
        ratingDto.setGivenRating(rating.getGivenRating());
        ratingDto.setRatedEntity(rating.getRatedEntity());
        ratingDto.setRater(rating.getRater());

        return ratingDto;
    }
}
