package gr.xe.rating.service.mappers;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.RatingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public class DbMappersTest {

    private DbMappers mapper;

    @BeforeEach
    public void setUp() {
        mapper = new DbMappers();
    }

    @Test
    public void fromDtoModel_WhenRatingDtoIsNull_ShouldReturnNull() {
        Assert.isNull(mapper.fromDtoModel(null), "Should be null");
    }

    @Test
    public void fromDtoModel_WhenRatingDtoIsOk_ShouldMapCorrectly() {
        RatingDto dto = new RatingDto();
        dto.setCreatedAt(LocalDateTime.now().minusSeconds(10));
        dto.setGivenRating(1.0);
        dto.setRatedEntity("none");
        dto.setRater("rater");

        Rating rating = mapper.fromDtoModel(dto);

        Assert.isTrue(dto.getCreatedAt().compareTo(rating.getCreatedAt()) < 0, "Created date not auto assigned");
        Assert.isTrue(dto.getGivenRating().equals(rating.getGivenRating()), "Not equal ratings");
        Assert.isTrue(dto.getRatedEntity().equals(rating.getRatedEntity()), "Not equal entities");
        Assert.isTrue(dto.getRater().equals(rating.getRater()), "Not equal raters");
    }
}