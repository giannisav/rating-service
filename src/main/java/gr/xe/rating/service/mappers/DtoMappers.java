package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.dto.Rating;

public final class DtoMappers {
    public static Rating fromDbModel (gr.xe.rating.service.models.db.Rating rating) {
        if (rating == null) {
            return null;
        }

        var o = new Rating();

        o.setCreatedAt(rating.getCreatedAt());
        o.setGivenRating(rating.getGivenRating());
        o.setRatedEntity(rating.getRatedEntity());
        o.setRater(rating.getRater());

        return o;
    }
}
