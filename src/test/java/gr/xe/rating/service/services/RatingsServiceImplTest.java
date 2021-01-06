package gr.xe.rating.service.services;


import gr.xe.rating.service.exceptions.InvalidGivenArgumentException;
import gr.xe.rating.service.mappers.DbMappers;
import gr.xe.rating.service.mappers.DtoMappers;
import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.ComputedRatingDto;
import gr.xe.rating.service.models.dto.RatingDto;
import gr.xe.rating.service.repositories.RatingsRepository;
import gr.xe.rating.service.utils.BooleanValidator;
import gr.xe.rating.service.utils.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RatingsServiceImplTest {

    private static final String SIGNED_IN = "2341";
    private static final Double INVALID_RATING = 0.4;
    private static final Double EXPECTED_OVERALL_RATING = 1.72;
    private static final Integer EXPECTED_CONSIDERED_RATINGS = 3;
    protected static final String RATED_ENTITY = "property_3742";
    protected static final Double VALID_RATING = 0.5;

    private BooleanValidator validator = mock(BooleanValidator.class);

    private RatingsRepository repository = mock(RatingsRepository.class);

    private DbMappers dbMapper = mock(DbMappers.class);

    private DtoMappers dtoMapper = mock(DtoMappers.class);

    private DateUtil dateUtil = mock(DateUtil.class);

    private InvalidGivenArgumentException invalidArgumentException = mock(InvalidGivenArgumentException.class);

    private RatingsService service;

    @BeforeEach
    public void setUp() {

        service = new RatingsServiceImpl(validator, repository, dbMapper, dtoMapper, dateUtil);
    }

    @Test
    public void saveRating_WhenRatingDtoIsValid_CorrectEntityIsSavedAndReturned() {
        RatingDto ratingDto = createRatingDto(VALID_RATING, RATED_ENTITY);
        Rating rating = createRating(VALID_RATING, RATED_ENTITY);
        doNothing().when(validator).validate(anyBoolean(), any(Supplier.class));
        when(dbMapper.fromDtoModel(eq(ratingDto))).thenReturn(rating);
        when(repository.save(eq(rating))).thenReturn(rating);
        when(dtoMapper.fromDbModel(eq(rating))).thenReturn(ratingDto);

        RatingDto returnedDto = service.saveRating(ratingDto);

        verify(validator, times(2)).validate(anyBoolean(), any(Supplier.class));
        verify(dbMapper).fromDtoModel(eq(ratingDto));
        verify(repository).save(eq(rating));
        verify(dtoMapper).fromDbModel(eq(rating));
        Assert.isTrue(returnedDto.getRatedEntity().equals(RATED_ENTITY), "Not same rated entity");
        Assert.isTrue(returnedDto.getGivenRating().equals(VALID_RATING), "Not same rating");
    }

    @Test
    public void saveRating_WhenRatingDtoIsNotValid_ExceptionIsThrown() {
        RatingDto ratingDto = createRatingDto(INVALID_RATING, RATED_ENTITY);
        doThrow(invalidArgumentException).when(validator).validate(anyBoolean(), any(Supplier.class));

        assertThrows(InvalidGivenArgumentException.class, () -> service.saveRating(ratingDto));
        verify(dbMapper, never()).fromDtoModel(eq(ratingDto));
        verify(repository, never()).save(any(Rating.class));
        verify(dtoMapper, never()).fromDbModel(any(Rating.class));
    }

    @Test
    public void getRatingFor_WhenInvoked_CorrectComputedDtoIsReturned() {
        doNothing().when(validator).validate(anyBoolean(), any(Supplier.class));
        LocalDateTime date = LocalDate.of(2020, 11, 4).atTime(4, 0, 0);
        when(dateUtil.now()).thenReturn(date);
        Rating rating1 = createRating(4.5, RATED_ENTITY);
        rating1.setCreatedAt(LocalDate.of(2020, 11, 4).atTime(11, 20));
        Rating rating2 = createRating(5.0, RATED_ENTITY);
        rating2.setCreatedAt(LocalDate.of(2020, 10, 10).atTime(23, 10));
        rating2.setRater(SIGNED_IN);
        Rating rating3 = createRating(1.5, RATED_ENTITY);
        rating3.setCreatedAt(LocalDate.of(2020, 9, 25).atTime(9, 50));
        rating3.setRater(SIGNED_IN);
        Rating rating4 = createRating(3.5, RATED_ENTITY);
        rating4.setCreatedAt(LocalDate.of(2020, 7, 15).atTime(9, 20));
        rating4.setRater(SIGNED_IN);
        List<Rating> ratings = Arrays.asList(rating1, rating2, rating3, rating4);
        when(repository.findAllByRatedEntityEquals(eq(RATED_ENTITY))).thenReturn(ratings);

        ComputedRatingDto computedRatingDto = service.getRatingFor(RATED_ENTITY);

        Assert.isTrue(computedRatingDto.getNumOfRatings().equals(EXPECTED_CONSIDERED_RATINGS),"Not same number of considered ratings");
        Assert.isTrue(computedRatingDto.getOverallRating().equals(EXPECTED_OVERALL_RATING), "Not same overall rating");
    }

    private RatingDto createRatingDto(Double givenRating, String ratedEntity){
        RatingDto ratingDto = new RatingDto();
        ratingDto.setGivenRating(givenRating);
        ratingDto.setRatedEntity(ratedEntity);
        return ratingDto;
    }

    private Rating createRating(Double givenRating, String ratedEntity) {
        Rating rating = new Rating();
        rating.setGivenRating(givenRating);
        rating.setRatedEntity(ratedEntity);
        return rating;
    }
}