package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.dto.Rating;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Date;

class DbMapperTests {
    @Test
    void worksWithNulls() {
        Assert.isNull(DbMappers.fromDtoModel(null), "Should be null");
    }

    @Test
    void mapsCorrectly() {
        var dto = new Rating();
        dto.setCreatedAt(Date.from(Instant.now().minusSeconds(100)));
        dto.setGivenRating(1);
        dto.setRatedEntity("none");
        dto.setRater("rater");

        var db = DbMappers.fromDtoModel(dto);

        Assert.isTrue(db.getCreatedAt().compareTo(dto.getCreatedAt()) > 0, "Created date not auto assigned");
        Assert.isTrue(dto.getGivenRating() == db.getGivenRating(), "Not equal ratings");
        Assert.isTrue(dto.getRatedEntity().equals(db.getRatedEntity()), "Not equal entities");
        Assert.isTrue(dto.getRater().equals(db.getRater()), "Not equal raters");
    }
}
