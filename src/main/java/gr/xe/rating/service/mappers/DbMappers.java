package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.RatingDto;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
public class DbMappers {

    public Rating fromDtoModel (RatingDto ratingDto) {
        if (ratingDto == null) {
            return null;
        }

        var rating = new Rating();

        rating.setGivenRating(ratingDto.getGivenRating());
        rating.setRatedEntity(ratingDto.getRatedEntity());
        rating.setRater(ratingDto.getRater());
        rating.setCreatedAt(LocalDateTime.now());

        return rating;
    }
}
