package gr.gavra.rating.services;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.gavra.rating.models.db.Rating;
import gr.gavra.rating.repositories.RatingsRepository;
import gr.gavra.rating.utils.DateUtil;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {

  @Mock
  private RatingsRepository repository;
  @Mock
  private DateUtil dateUtil = mock(DateUtil.class);

  private ScheduledTasks tasks;

  @BeforeEach
  void setUp() {
    tasks = new ScheduledTasks(repository, dateUtil);
  }

  @Test
  void clearOldRatings_WhenInvoked_ShouldWorkProperly() {
    //Given
    var date = LocalDate.of(2020, 11, 4).atTime(4, 0, 0);
    when(dateUtil.now()).thenReturn(date);
    var rating1 = createRating(1L, RatingsServiceImplTest.VALID_RATING,
        RatingsServiceImplTest.RATED_ENTITY);
    rating1.setCreatedAt(LocalDate.of(2020, 11, 4).atTime(11, 20));
    var rating2 = createRating(2L, RatingsServiceImplTest.VALID_RATING,
        RatingsServiceImplTest.RATED_ENTITY);
    rating2.setCreatedAt(LocalDate.of(2020, 3, 10).atTime(23, 10));
    List<Rating> ratings = Arrays.asList(rating1, rating2);
    List<Long> idOfTheOldRating = Arrays.asList(2L);
    when(repository.findAll()).thenReturn(ratings);
    doNothing().when(repository).deleteOldGames(eq(idOfTheOldRating));
    //When
    tasks.clearOldRatings();
    //Assert
    verify(repository).deleteOldGames(eq(idOfTheOldRating));
  }

  private Rating createRating(Long id, Double givenRating, String ratedEntity) {
    var rating = new Rating();
    rating.setId(id);
    rating.setGivenRating(givenRating);
    rating.setRatedEntity(ratedEntity);
    return rating;
  }
}