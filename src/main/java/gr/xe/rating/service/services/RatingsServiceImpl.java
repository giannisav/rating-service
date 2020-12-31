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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RatingsServiceImpl implements RatingsService{

    private final static List<Double> ACCEPTED_RATING_VALUES =
            Arrays.asList(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0);

    private final BooleanValidator validator;
    private final RatingsRepository repository;
    private final DbMappers dbMapper;
    private final DtoMappers dtoMapper;

    @Override
    public RatingDto saveRating(RatingDto ratingDTO) {
        validator.validate(ACCEPTED_RATING_VALUES.contains(ratingDTO.getGivenRating()),
                () -> new InvalidGivenArgumentException("Given rating is null or has not any of accepted values"));
        validator.validate(ratingDTO.getRatedEntity() != null && ratingDTO.getRatedEntity().length() >= 1,
                () -> new InvalidGivenArgumentException("Rated Entity should not be null or empty."));

        Rating savedEntity = repository.save(dbMapper.fromDtoModel(ratingDTO));

        return dtoMapper.fromDbModel(savedEntity);
    }

    @Transactional
    @Override
    public ComputedRatingDto getRatingFor(String ratedEntity) {
        List<Rating> ratings = getRatingsCreatedLast100DaysFor(ratedEntity);

        validator.validate(ratings.size() == 0,
                () -> new NotFoundRatedEntity(ratedEntity + " has not any ratings"));

        ComputedRatingDto computedRatingDto = new ComputedRatingDto();
        computedRatingDto.setNumOfRatings(ratings.size());
        double overallRating = getWeightedRatingSum(ratings) / ratings.size() / 20;
        computedRatingDto.setOverallRating(Math.floor(overallRating * 100) / 100);

        return computedRatingDto;
    }

    private List<Rating> getRatingsCreatedLast100DaysFor(String ratedEntity) {
        LocalDate now = LocalDate.now();
        return repository.findAllByRatedEntityEquals(ratedEntity)
                .stream()
                .filter(rating -> Math.abs(Period.between(rating.getCreatedAt(), now).getDays()) <= 100)
                .collect(Collectors.toList());
    }

    private Double getWeightedRatingSum(List<Rating> ratings) {
        LocalDate now = LocalDate.now();

        return ratings
                .stream()
                .mapToDouble(rating -> 100 *
                        (rating.getGivenRating() / 5.0) *
                        ((1 - Math.abs(Period.between(rating.getCreatedAt(), now).getDays()) / 100.0)) *
                        (rating.getRater() == null ? 0.1 : 1.0))
                .sum();
    }
}
