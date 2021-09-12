package gr.gavra.rating.services;

import gr.gavra.rating.exceptions.InvalidGivenArgumentException;
import gr.gavra.rating.exceptions.NotFoundRatedEntity;
import gr.gavra.rating.mappers.RatingMapper;
import gr.gavra.rating.models.db.Rating;
import gr.gavra.rating.models.dto.ComputedRatingDto;
import gr.gavra.rating.models.dto.RatingDTO;
import gr.gavra.rating.models.dto.RatingDTO.RatingDTOValidationResult;
import gr.gavra.rating.repositories.RatingsRepository;
import gr.gavra.rating.utils.BooleanValidator;
import gr.gavra.rating.utils.DateUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RatingsServiceImpl implements RatingsService {

  private final RatingsRepository repository;
  private final DateUtil dateUtil;

  @Override
  public RatingDTO saveRating(RatingDTO ratingDto) {
    var validationResult = ratingDto.isValid();
    BooleanValidator.validate(validationResult.equals(RatingDTOValidationResult.VALID),
        () -> new InvalidGivenArgumentException(validationResult.getResult()));
    var savedRating = repository.save(RatingMapper.toRating(ratingDto));

    return RatingMapper.toRatingDTO(savedRating);
  }

  @Cacheable(value = "ratings", key = "#ratedEntity")
  @Override
  public ComputedRatingDto getRatingFor(String ratedEntity) {
    BooleanValidator.validate(repository.existsByRatedEntity(ratedEntity),
        () -> new NotFoundRatedEntity(ratedEntity + " has not any ratings."));
    var now = dateUtil.now();
    var ratings = getRatingsCreatedLast100DaysFor(ratedEntity, now);
    Double overallRating = getWeightedRatingSum(ratings, now) / ratings.size() / 20;

    return new ComputedRatingDto(ratings.size(), Math.floor(overallRating * 100) / 100);
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
