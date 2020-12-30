package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.db.Rating;

import java.time.Instant;
import java.util.Date;

public final class DbMappers {
    public static Rating fromDtoModel (gr.xe.rating.service.models.dto.Rating rating) {
        if (rating == null) {
            return null;
        }

        var o = new Rating();

        o.setGivenRating(rating.getGivenRating());
        o.setRatedEntity(rating.getRatedEntity());
        o.setRater(rating.getRater());
        o.setCreatedAt(Date.from(Instant.now()));

        return o;
    }
}
