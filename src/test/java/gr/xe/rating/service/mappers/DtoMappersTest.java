package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.RatingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.util.Assert;

import java.time.LocalDateTime;


public class DtoMappersTest {

    private DtoMappers mapper;

    @BeforeEach
    public void setUp() {
        mapper = new DtoMappers();
    }

    @Test
    public void fromDbModel_WhenRatingIsNull_ShouldReturnNull() {
        Assert.isNull(mapper.fromDbModel(null), "Should be null");
    }

    @Test
    public void fromDbModel_WhenRatingIsOk_ShouldMapCorrectly() {
        Rating rating = new Rating();
        rating.setCreatedAt(LocalDateTime.now());
        rating.setGivenRating(1.0);
        rating.setRatedEntity("none");
        rating.setRater("rater");

        RatingDto dto = mapper.fromDbModel(rating);

        Assert.isTrue(dto.getCreatedAt().equals(rating.getCreatedAt()), "Not equal dates");
        Assert.isTrue(dto.getGivenRating().equals(rating.getGivenRating()), "Not equal ratings");
        Assert.isTrue(dto.getRatedEntity().equals(rating.getRatedEntity()), "Not equal entities");
        Assert.isTrue(dto.getRater().equals(rating.getRater()), "Not equal raters");
    }
}