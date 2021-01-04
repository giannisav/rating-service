package gr.xe.rating.service.services;

import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.repositories.RatingsRepository;
import gr.xe.rating.service.utils.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ScheduledTasksTest {

    private static final String CACHE_NAME = "ratings";

    private RatingsRepository repository = mock(RatingsRepository.class);

    private CacheManager manager = mock(CacheManager.class);

    private DateUtil dateUtil = mock(DateUtil.class);

    private Cache cache = mock(Cache.class);

    private ScheduledTasks tasks;

    @Before
    public void setUp() {
        tasks = new ScheduledTasks(repository, manager, dateUtil);
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

    @Test
    public void clearCache_WhenInvoked_ShouldWorkProperly() {
        List<String> cacheNames = Arrays.asList(CACHE_NAME);
        when(manager.getCacheNames()).thenReturn(cacheNames);
        when(manager.getCache(eq(CACHE_NAME))).thenReturn(cache);
        doNothing().when(cache).clear();

        tasks.clearCache();

        verify(manager).getCacheNames();
        verify(manager).getCache(eq(CACHE_NAME));
        verify(cache).clear();
    }

    private Rating createRating(Double givenRating, String ratedEntity) {
        Rating rating = new Rating();
        rating.setGivenRating(givenRating);
        rating.setRatedEntity(ratedEntity);
        return rating;
    }
}