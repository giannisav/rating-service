package gr.xe.rating.service.integration;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.ComputedRatingDto;
import gr.xe.rating.service.models.dto.RatingDto;
import gr.xe.rating.service.repositories.RatingsRepository;
import gr.xe.rating.service.utils.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.mockito.Mockito.when;

@DirtiesContext
@RunWith(SpringRunner.class)
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
        URI uri = new URI("http://localhost:" + randomServerPort + "/ratings");
        Rating rating = createRating(4.7, RATED_ENTITY);
        HttpEntity<Rating> request = new HttpEntity<>(rating);

        ResponseEntity<String> response = testRestTemplate.postForEntity(uri, request, String.class);

        Assert.isTrue(HttpStatus.BAD_REQUEST.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue("Given rating is null or has not any of accepted values.".equals(response.getBody()),
                "Not equal exception message");
    }

    @Test
    public void createRating_WhenRequestBodyHasNotValidRatedEntity_BadRequestWithCorrespondingMessageIsReturned()
            throws URISyntaxException {
        URI uri = new URI("http://localhost:" + randomServerPort + "/ratings");
        Rating rating = createRating(4.5, "");
        HttpEntity<Rating> request = new HttpEntity<>(rating);

        ResponseEntity<String> response = testRestTemplate.postForEntity(uri, request, String.class);

        Assert.isTrue(HttpStatus.BAD_REQUEST.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue("Rated Entity should not be null or empty.".equals(response.getBody()),
                "Not equal exception message");
    }

    @Test
    public void createRating_WhenRequestBodyHasIsValid_CreatedWithCorrectDtoIsReturned()
            throws URISyntaxException {
        URI uri = new URI("http://localhost:" + randomServerPort + "/ratings");
        Rating rating = createRating(4.5, RATED_ENTITY);
        HttpEntity<Rating> request = new HttpEntity<>(rating);

        ResponseEntity<RatingDto> response = testRestTemplate.postForEntity(uri, request, RatingDto.class);

        Assert.isTrue(HttpStatus.CREATED.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue(response.getBody() != null, "Response body should not be null");
        Assert.isTrue(RATED_ENTITY.equals(response.getBody().getRatedEntity()), "Not same rated entity");
        Assert.isTrue(response.getBody().getGivenRating().equals(4.5), "Not same given rating");
        Assert.isNull(response.getBody().getRater(), "Rater should be null");
    }

    @Test
    public void getRatingForApi_WhenEntityHasNoRatings_NotFoundWithCorrespondingMessageIsReturned()
            throws URISyntaxException {
        URI uri = new URI("http://localhost:" + randomServerPort + "/ratings/" + INVALID_ENTITY);

        ResponseEntity<String> response = testRestTemplate.getForEntity(uri, String.class);

        Assert.isTrue(HttpStatus.NOT_FOUND.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue((INVALID_ENTITY + " has not any ratings.").equals(response.getBody()),
                "Not equal exception message");
    }

    @Test
    public void getRatingForApi_WhenEntityHas_CorrectResponseIsReturned() throws URISyntaxException {
        URI uri = new URI("http://localhost:" + randomServerPort + "/ratings/" + RATED_ENTITY);
        LocalDateTime date = LocalDate.of(2020, 11, 4).atTime(4, 0, 0);
        when(dateUtil.now()).thenReturn(date);
        Rating rating1 = createRating(4.5, RATED_ENTITY);
        repository.save(rating1);
        rating1.setCreatedAt(LocalDate.of(2020, 11, 4).atTime(11, 20));
        repository.save(rating1);
        Rating rating2 = createRating(5.0, RATED_ENTITY);
        repository.save(rating2);
        rating2.setCreatedAt(LocalDate.of(2020, 10, 10).atTime(23, 10));
        rating2.setRater(SIGNED_IN);
        repository.save(rating2);
        Rating rating3 = createRating(1.5, RATED_ENTITY);
        repository.save(rating3);
        rating3.setCreatedAt(LocalDate.of(2020, 9, 25).atTime(9, 50));
        rating3.setRater(SIGNED_IN);
        repository.save(rating3);
        Rating rating4 = createRating(3.5, RATED_ENTITY);
        repository.save(rating4);
        rating4.setCreatedAt(LocalDate.of(2020, 3, 15).atTime(9, 20));
        rating4.setRater(SIGNED_IN);
        repository.save(rating4);

        ResponseEntity<ComputedRatingDto> response = testRestTemplate.getForEntity(uri, ComputedRatingDto.class);

        Assert.isTrue(HttpStatus.OK.equals(response.getStatusCode()), "Not equal status code");
        Assert.isTrue(response.getBody() != null, "Response body should not be null");
        Assert.isTrue(response.getBody().getNumOfRatings().equals(EXPECTED_CONSIDERED_RATINGS),"Not same number of considered ratings");
        Assert.isTrue(response.getBody().getOverallRating().equals(EXPECTED_OVERALL_RATING), "Not same overall rating");
    }

    private Rating createRating(Double givenRating, String ratedEntity) {
        Rating rating = new Rating();
        rating.setGivenRating(givenRating);
        rating.setRatedEntity(ratedEntity);
        return rating;
    }
}
