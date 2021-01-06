package gr.xe.rating.service.services;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.repositories.RatingsRepository;
import gr.xe.rating.service.utils.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ScheduledTasksTest {

    private RatingsRepository repository = mock(RatingsRepository.class);

    private DateUtil dateUtil = mock(DateUtil.class);

    private ScheduledTasks tasks;

    @BeforeEach
    public void setUp() {
        tasks = new ScheduledTasks(repository, dateUtil);
    }

    @Test
    public void clearOldRatings_WhenInvoked_ShouldWorkProperly() {
        LocalDateTime date = LocalDate.of(2020, 11, 4).atTime(4, 0, 0);
        when(dateUtil.now()).thenReturn(date);
        Rating rating1 = createRating(RatingsServiceImplTest.VALID_RATING, RatingsServiceImplTest.RATED_ENTITY);
        rating1.setCreatedAt(LocalDate.of(2020, 11, 4).atTime(11, 20));
        Rating rating2 = createRating(RatingsServiceImplTest.VALID_RATING, RatingsServiceImplTest.RATED_ENTITY);
        rating2.setCreatedAt(LocalDate.of(2020, 3, 10).atTime(23, 10));
        List<Rating> ratings = Arrays.asList(rating1, rating2);
        when(repository.findAll()).thenReturn(ratings);
        doNothing().when(repository).delete(any(Rating.class));

        tasks.clearOldRatings();

        verify(repository).delete(eq(rating2));
    }

    private Rating createRating(Double givenRating, String ratedEntity) {
        Rating rating = new Rating();
        rating.setGivenRating(givenRating);
        rating.setRatedEntity(ratedEntity);
        return rating;
    }
}