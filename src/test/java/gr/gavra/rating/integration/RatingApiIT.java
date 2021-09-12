package gr.gavra.rating.integration;

import static org.mockito.Mockito.when;

import gr.gavra.rating.models.db.Rating;
import gr.gavra.rating.models.dto.ComputedRatingDto;
import gr.gavra.rating.models.dto.RatingDTO;
import gr.gavra.rating.repositories.RatingsRepository;
import gr.gavra.rating.utils.DateUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.Assert;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RatingApiIT {

  private static final String RATED_ENTITY = "property_3742";
  private static final String INVALID_ENTITY = "Foo";
  private static final String SIGNED_IN = "2341";
  private static final Double EXPECTED_OVERALL_RATING = 1.72;
  private static final Integer EXPECTED_CONSIDERED_RATINGS = 3;

  @LocalServerPort
  private int randomServerPort;

  @MockBean
  private DateUtil dateUtil;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private RatingsRepository repository;

  @Test
  public void createRating_WhenRequestBodyHasNotValidRating_BadRequestWithCorrespondingMessageIsReturned()
      throws URISyntaxException {
    var uri = new URI("http://localhost:" + randomServerPort + "/ratings");
    var rating = createRating(4.7, RATED_ENTITY, null, null);
    HttpEntity<Rating> request = new HttpEntity<>(rating);

    ResponseEntity<String> response = testRestTemplate.postForEntity(uri, request, String.class);

    Assert.isTrue(HttpStatus.BAD_REQUEST.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue(
        "Given rating is null or has not any of accepted values.".equals(response.getBody()),
        "Not equal exception message");
  }

  @Test
  public void createRating_WhenRequestBodyHasNotValidRatedEntity_BadRequestWithCorrespondingMessageIsReturned()
      throws URISyntaxException {
    var uri = new URI("http://localhost:" + randomServerPort + "/ratings");
    var rating = createRating(4.5, "", null, null);
    HttpEntity<Rating> request = new HttpEntity<>(rating);

    ResponseEntity<String> response = testRestTemplate.postForEntity(uri, request, String.class);

    Assert.isTrue(HttpStatus.BAD_REQUEST.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue("Rated Entity should not be null or empty.".equals(response.getBody()),
        "Not equal exception message");
  }

  @Test
  public void createRating_WhenRequestBodyHasIsValid_CreatedWithCorrectDtoIsReturned()
      throws URISyntaxException {
    var uri = new URI("http://localhost:" + randomServerPort + "/ratings");
    var rating = createRating(4.5, RATED_ENTITY, null, null);
    HttpEntity<Rating> request = new HttpEntity<>(rating);

    ResponseEntity<RatingDTO> response = testRestTemplate.postForEntity(uri, request,
        RatingDTO.class);

    Assert.isTrue(HttpStatus.CREATED.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue(response.getBody() != null, "Response body should not be null");
    Assert.isTrue(RATED_ENTITY.equals(response.getBody().getRatedEntity()),
        "Not same rated entity");
    Assert.isTrue(response.getBody().getGivenRating().equals(4.5), "Not same given rating");
    Assert.isNull(response.getBody().getRater(), "Rater should be null");
  }

  @Test
  public void getRatingForApi_WhenEntityHasNoRatings_NotFoundWithCorrespondingMessageIsReturned()
      throws URISyntaxException {
    var uri = new URI("http://localhost:" + randomServerPort + "/ratings/" + INVALID_ENTITY);

    ResponseEntity<String> response = testRestTemplate.getForEntity(uri, String.class);

    Assert.isTrue(HttpStatus.NOT_FOUND.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue((INVALID_ENTITY + " has not any ratings.").equals(response.getBody()),
        "Not equal exception message");
  }

  @Test
  public void getRatingForApi_WhenEntityHasRatings_CorrectResponseIsReturned()
      throws URISyntaxException {
    var uri = new URI("http://localhost:" + randomServerPort + "/ratings/" + RATED_ENTITY);
    var date = LocalDate.of(2020, 11, 4).atTime(4, 0, 0);
    when(dateUtil.now()).thenReturn(date);
    var rating1 = createRating(4.5, RATED_ENTITY, LocalDate.of(2020, 11, 4).atTime(11, 20), null);
    var rating2 = createRating(5.0, RATED_ENTITY, LocalDate.of(2020, 10, 10).atTime(23, 10),
        SIGNED_IN);
    var rating3 = createRating(1.5, RATED_ENTITY, LocalDate.of(2020, 9, 25).atTime(9, 50),
        SIGNED_IN);
    var rating4 = createRating(3.5, RATED_ENTITY, LocalDate.of(2020, 3, 15).atTime(9, 20),
        SIGNED_IN);
    repository.saveAll(Arrays.asList(rating1, rating2, rating3, rating4));

    ResponseEntity<ComputedRatingDto> response = testRestTemplate.getForEntity(uri,
        ComputedRatingDto.class);

    Assert.isTrue(HttpStatus.OK.equals(response.getStatusCode()), "Not equal status code");
    Assert.isTrue(response.getBody() != null, "Response body should not be null");
    Assert.isTrue(response.getBody().getNumOfRatings().equals(EXPECTED_CONSIDERED_RATINGS),
        "Not same number of considered ratings");
    Assert.isTrue(response.getBody().getOverallRating().equals(EXPECTED_OVERALL_RATING),
        "Not same overall rating");
  }

  private Rating createRating(Double givenRating, String ratedEntity, LocalDateTime createdAt,
      String rater) {
    var rating = new Rating();
    rating.setGivenRating(givenRating);
    rating.setRatedEntity(ratedEntity);
    rating.setCreatedAt(createdAt);
    rating.setRater(rater);
    return rating;
  }
}
