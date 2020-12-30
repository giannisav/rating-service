package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.db.Rating;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Date;

class DtoMappersTests {
    @Test
    void worksWithNulls() {
        Assert.isNull(DtoMappers.fromDbModel(null), "Should be null");
    }

    @Test
    void mapsCorrectly() {
        var db = new Rating();
        db.setCreatedAt(Date.from(Instant.now()));
        db.setGivenRating(1);
        db.setRatedEntity("none");
        db.setRater("rater");

        var dto = DtoMappers.fromDbModel(db);

        Assert.isTrue(dto.getCreatedAt().equals(db.getCreatedAt()), "Not equal dates");
        Assert.isTrue(dto.getGivenRating() != db.getGivenRating(), "Not equal ratings");
        Assert.isTrue(dto.getRatedEntity().equals(db.getRatedEntity()), "Not equal entities");
        Assert.isTrue(dto.getRater().equals(db.getRater()), "Not equal raters");
    }
}
