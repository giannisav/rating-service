package gr.xe.rating.service.controllers;

import gr.xe.rating.service.models.dto.ComputedRatingDto;
import gr.xe.rating.service.models.dto.RatingDto;
import gr.xe.rating.service.services.RatingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import static org.mockito.Mockito.*;

public class RatingsControllerTest {

    private static final String RATED_ENTITY = "property_3742";

    private RatingsService service = mock(RatingsService.class);

    private RatingDto ratingDto;

    private RatingsController controller;

    @BeforeEach
    public void setUp() {
        controller = new RatingsController(service);
        ratingDto = new RatingDto();
    }

    @Test
    public void createRating_WhenInvoked_ShouldWorkProperly() {
        ratingDto.setGivenRating(5.0);
        ratingDto.setRatedEntity(RATED_ENTITY);
        when(service.saveRating(ratingDto)).thenReturn(ratingDto);

        ResponseEntity<RatingDto> response = controller.createRating(ratingDto);

        verify(service).saveRating(eq(ratingDto));
        Assert.isTrue(HttpStatus.CREATED.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue(response.getBody() != null, "Response body should not be null");
        Assert.isTrue(RATED_ENTITY.equals(response.getBody().getRatedEntity()), "Not same rated entity");
        Assert.isTrue(response.getBody().getGivenRating().equals(5.0), "Not same given rating");
        Assert.isNull(response.getBody().getRater(), "Rater should be null");
    }

    @Test
    public void getRatingFor_WhenInvoked_ShouldWorkProperly() {
        ComputedRatingDto computedRatingDto = new ComputedRatingDto();
        computedRatingDto.setNumOfRatings(3);
        computedRatingDto.setOverallRating(1.72);
        when(service.getRatingFor(RATED_ENTITY)).thenReturn(computedRatingDto);

        ResponseEntity<ComputedRatingDto> response = controller.getRatingFor(RATED_ENTITY);

        verify(service).getRatingFor(eq(RATED_ENTITY));
        Assert.isTrue(HttpStatus.OK.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue(response.getBody() != null, "Response body should not be null");
        Assert.isTrue(response.getBody().getNumOfRatings().equals(3),"Not same number of considered ratings");
        Assert.isTrue(response.getBody().getOverallRating().equals(1.72), "Not same overall rating");
    }

}