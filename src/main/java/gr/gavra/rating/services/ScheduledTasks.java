package gr.gavra.rating.services;

import gr.gavra.rating.models.db.Rating;
import gr.gavra.rating.repositories.RatingsRepository;
import gr.gavra.rating.utils.DateUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledTasks {

  private final RatingsRepository repository;
  private final DateUtil dateUtil;

  @Scheduled(cron = "0 0 4 * * ?", zone = "Europe/Athens")
  public void clearOldRatings() {
    var now = dateUtil.now();
    repository.deleteOldGames(findIdsFromRatingsOlderThan(now));
  }

  private List<Long> findIdsFromRatingsOlderThan(LocalDateTime localDateTime) {
    return repository.findAll()
        .stream()
        .filter(rating -> ChronoUnit.DAYS.between(rating.getCreatedAt(), localDateTime) > 100)
        .map(Rating::getId)
        .collect(Collectors.toList());
  }
}
