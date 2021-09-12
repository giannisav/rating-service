package gr.gavra.rating.services;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.gavra.rating.exceptions.InvalidGivenArgumentException;
import gr.gavra.rating.mappers.RatingMapper;
import gr.gavra.rating.models.db.Rating;
import gr.gavra.rating.models.dto.RatingDTO;
import gr.gavra.rating.repositories.RatingsRepository;
import gr.gavra.rating.utils.BooleanValidator;
import gr.gavra.rating.utils.DateUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.util.Assert;

@ExtendWith(MockitoExtension.class)
class RatingsServiceImplTest {

  private static final String SIGNED_IN = "2341";
  private static final Double INVALID_RATING = 0.4;
  private static final Double EXPECTED_OVERALL_RATING = 1.72;
  private static final Integer EXPECTED_CONSIDERED_RATINGS = 3;
  protected static final String RATED_ENTITY = "property_3742";
  protected static final Double VALID_RATING = 0.5;

  private static MockedStatic<BooleanValidator> validator;
  private static MockedStatic<RatingMapper> mapper;

  @Mock
  private RatingsRepository repository;
  @Mock
  private DateUtil dateUtil;
  @Mock
  private InvalidGivenArgumentException invalidArgumentException;
  @Mock
  private RatingsService service;

  @BeforeEach
  void setUp() {
    mapper = Mockito.mockStatic(RatingMapper.class);
    validator = Mockito.mockStatic(BooleanValidator.class);
    service = new RatingsServiceImpl(repository, dateUtil);
  }

  @Test
  void saveRating_WhenRatingDtoIsValid_CorrectEntityIsSavedAndReturned() {
    //Given
    var ratingDTO = createRatingDto(VALID_RATING, RATED_ENTITY);
    var rating = createRating(VALID_RATING, RATED_ENTITY, null, null);
    validator.when(() -> BooleanValidator.validate(anyBoolean(), any(Supplier.class)))
        .thenAnswer((Answer<Void>) invocation -> null);
    mapper.when(() -> RatingMapper.toRating(eq(ratingDTO))).thenReturn(rating);
    when(repository.save(eq(rating))).thenReturn(rating);
    mapper.when(() -> RatingMapper.toRatingDTO(eq(rating))).thenReturn(ratingDTO);
    //When
    var returnedDto = service.saveRating(ratingDTO);
    //Assert
    verify(repository).save(eq(rating));
    Assert.isTrue(returnedDto.getRatedEntity().equals(RATED_ENTITY), "Not same rated entity");
    Assert.isTrue(returnedDto.getGivenRating().equals(VALID_RATING), "Not same rating");
  }

  @Test
  void saveRating_WhenRatingDtoIsNotValid_ExceptionIsThrown() {
    //Given
    var ratingDTO = createRatingDto(INVALID_RATING, RATED_ENTITY);
    validator.when(() -> BooleanValidator.validate(anyBoolean(), any(Supplier.class)))
        .thenThrow(invalidArgumentException);
    //When
    assertThrows(InvalidGivenArgumentException.class, () -> service.saveRating(ratingDTO));
    //Assert
    verify(repository, never()).save(any(Rating.class));
  }

  @Test
  void getRatingFor_WhenInvoked_CorrectComputedDtoIsReturned() {
    //Given
    validator.when(() -> BooleanValidator.validate(anyBoolean(), any(Supplier.class)))
        .thenAnswer((Answer<Void>) invocation -> null);
    var date = LocalDate.of(2020, 11, 4).atTime(4, 0, 0);
    when(dateUtil.now()).thenReturn(date);
    var rating1 = createRating(4.5, RATED_ENTITY, LocalDate.of(2020, 11, 4).atTime(11, 20), null);
    var rating2 = createRating(5.0, RATED_ENTITY, LocalDate.of(2020, 10, 10).atTime(23, 10),
        SIGNED_IN);
    var rating3 = createRating(1.5, RATED_ENTITY, LocalDate.of(2020, 9, 25).atTime(9, 50),
        SIGNED_IN);
    var rating4 = createRating(3.5, RATED_ENTITY, LocalDate.of(2020, 7, 15).atTime(9, 20),
        SIGNED_IN);

    var ratings = Arrays.asList(rating1, rating2, rating3, rating4);
    when(repository.findAllByRatedEntityEquals(eq(RATED_ENTITY))).thenReturn(ratings);
    //When
    var computedRatingDto = service.getRatingFor(RATED_ENTITY);
    //Assert
    Assert.isTrue(computedRatingDto.getNumOfRatings().equals(EXPECTED_CONSIDERED_RATINGS),
        "Not same number of considered ratings");
    Assert.isTrue(computedRatingDto.getOverallRating().equals(EXPECTED_OVERALL_RATING),
        "Not same overall rating");
  }

  @Test
  void getRatingFor_WhenInvoked_CannotReturnNegative() {
    //Given
    validator.when(() -> BooleanValidator.validate(anyBoolean(), any(Supplier.class)))
        .thenAnswer((Answer<Void>) invocation -> null);
    var now = LocalDate.now().atStartOfDay().plusNanos(-1);
    var ratingCreatedAt = now.plusDays(-101).plusNanos(1);
    when(dateUtil.now()).thenReturn(now);
    var rating1 = createRating(4.5, RATED_ENTITY, ratingCreatedAt, null);
    List<Rating> ratingList = Arrays.asList(rating1);
    when(repository.findAllByRatedEntityEquals(eq(RATED_ENTITY))).thenReturn(ratingList);
    //When
    var computedRatingDto = service.getRatingFor(RATED_ENTITY);
    //Assert
    Assert.isTrue(computedRatingDto.getNumOfRatings().equals(1),
        "Not same number of considered ratings");
    Assert.isTrue(computedRatingDto.getOverallRating().equals(0.0), "Not same overall rating");
  }

  @AfterEach
  void cleanUp() {
    validator.close();
    mapper.close();
  }

  private RatingDTO createRatingDto(Double givenRating, String ratedEntity) {
    var ratingDto = new RatingDTO();
    ratingDto.setGivenRating(givenRating);
    ratingDto.setRatedEntity(ratedEntity);
    return ratingDto;
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