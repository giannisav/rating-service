package gr.xe.rating.service.services;

import gr.xe.rating.service.repositories.RatingsRepository;
import gr.xe.rating.service.utils.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@AllArgsConstructor
@Component
public class ScheduledTasks {

    private RatingsRepository repository;
    private CacheManager manager;
    private DateUtil dateUtil;

    @Scheduled(cron = "0 0 4 * * ?", zone = "Europe/Athens")
    public void clearOldRatings() {
        LocalDateTime now = dateUtil.now();
        repository.findAll()
                .stream()
                .filter(rating -> ChronoUnit.DAYS.between(rating.getCreatedAt(), now) > 100)
                .forEach(repository::delete);
    }

    @Scheduled(fixedRate = 60*60*1000, initialDelay = 60*60*1000)
    public void clearCache() {
        manager.getCacheNames()
                .stream()
                .forEach(c -> Objects.requireNonNull(manager.getCache(c)).clear());
    }
}
