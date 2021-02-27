package gr.xe.rating.service.services;

import gr.xe.rating.service.exceptions.InvalidGivenArgumentException;
import gr.xe.rating.service.exceptions.NotFoundRatedEntity;
import gr.xe.rating.service.mappers.DbMappers;
import gr.xe.rating.service.mappers.DtoMappers;
import gr.xe.rating.service.models.db.Rating;
import gr.xe.rating.service.models.dto.ComputedRatingDto;
import gr.xe.rating.service.models.dto.RatingDto;
import gr.xe.rating.service.repositories.RatingsRepository;
import gr.xe.rating.service.utils.BooleanValidator;
import gr.xe.rating.service.utils.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RatingsServiceImpl implements RatingsService{

    private final static List<Double> VALID_RATING_VALUES =
            Arrays.asList(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0);

    private final BooleanValidator validator;
    private final RatingsRepository repository;
    private final DbMappers dbMapper;
    private final DtoMappers dtoMapper;
    private final DateUtil dateUtil;

    @Override
    public RatingDto saveRating(RatingDto ratingDto) {
        validator.validate(VALID_RATING_VALUES.contains(ratingDto.getGivenRating()),
                () -> new InvalidGivenArgumentException("Given rating is null or has not any of accepted values."));
        validator.validate(ratingDto.getRatedEntity() != null && ratingDto.getRatedEntity().length() > 0,
                () -> new InvalidGivenArgumentException("Rated Entity should not be null or empty."));

        Rating savedEntity = repository.save(dbMapper.fromDtoModel(ratingDto));

        return dtoMapper.fromDbModel(savedEntity);
    }

    @Cacheable(value = "ratings", key = "#ratedEntity")
    @Override
    public ComputedRatingDto getRatingFor(String ratedEntity) {
        validator.validate(repository.existsByRatedEntity(ratedEntity),
                () -> new NotFoundRatedEntity(ratedEntity + " has not any ratings."));
        LocalDateTime now = dateUtil.now();
        List<Rating> ratings = getRatingsCreatedLast100DaysFor(ratedEntity, now);

        ComputedRatingDto computedRatingDto = new ComputedRatingDto();
        computedRatingDto.setNumOfRatings(ratings.size());
        double overallRating = getWeightedRatingSum(ratings, now) / ratings.size() / 20;
        computedRatingDto.setOverallRating(Math.floor(overallRating * 100) / 100);

        return computedRatingDto;
    }

    private List<Rating> getRatingsCreatedLast100DaysFor(String ratedEntity, LocalDateTime now) {
        return repository.findAllByRatedEntityEquals(ratedEntity)
                .stream()
                .filter(rating -> ChronoUnit.DAYS.between(rating.getCreatedAt(), now) <= 100)
                .collect(Collectors.toList());
    }

    private Double getWeightedRatingSum(List<Rating> ratings, LocalDateTime now) {

        return ratings
                .stream()
                .mapToDouble(rating -> 100 *
                        (rating.getGivenRating() / 5.0) *
                        (1 - ChronoUnit.DAYS.between(rating.getCreatedAt(), now) / 100.0) *
                        (rating.getRater() == null ? 0.1 : 1.0))
                .sum();
    }
}
