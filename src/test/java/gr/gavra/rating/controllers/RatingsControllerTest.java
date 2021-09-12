package gr.gavra.rating.controllers;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.gavra.rating.models.dto.ComputedRatingDto;
import gr.gavra.rating.models.dto.RatingDTO;
import gr.gavra.rating.services.RatingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

@ExtendWith(MockitoExtension.class)
class RatingsControllerTest {

  private static final String RATED_ENTITY = "property_3742";

  @Mock
  private RatingsService service;

  private RatingDTO ratingDto;

  private RatingsController controller;

  @BeforeEach
  public void setUp() {
    controller = new RatingsController(service);
    ratingDto = new RatingDTO();
  }

  @Test
  void createRating_WhenInvoked_ShouldWorkProperly() {
    //Given
    ratingDto.setGivenRating(5.0);
    ratingDto.setRatedEntity(RATED_ENTITY);
    when(service.saveRating(ratingDto)).thenReturn(ratingDto);
    //When
    ResponseEntity<RatingDTO> response = controller.createRating(ratingDto);
    //Assert
    verify(service).saveRating(eq(ratingDto));
    Assert.isTrue(HttpStatus.CREATED.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue(response.getBody() != null, "Response body should not be null");
    Assert.isTrue(RATED_ENTITY.equals(response.getBody().getRatedEntity()),
        "Not same rated entity");
    Assert.isTrue(response.getBody().getGivenRating().equals(5.0), "Not same given rating");
    Assert.isNull(response.getBody().getRater(), "Rater should be null");
  }

  @Test
  void getRatingFor_WhenInvoked_ShouldWorkProperly() {
    //Given
    var computedRatingDto = new ComputedRatingDto(3, 1.72);
    when(service.getRatingFor(RATED_ENTITY)).thenReturn(computedRatingDto);
    //When
    ResponseEntity<ComputedRatingDto> response = controller.getRatingFor(RATED_ENTITY);
    //Assert
    verify(service).getRatingFor(eq(RATED_ENTITY));
    Assert.isTrue(HttpStatus.OK.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue(response.getBody() != null, "Response body should not be null");
    Assert.isTrue(response.getBody().getNumOfRatings().equals(3),
        "Not same number of considered ratings");
    Assert.isTrue(response.getBody().getOverallRating().equals(1.72), "Not same overall rating");
  }

}