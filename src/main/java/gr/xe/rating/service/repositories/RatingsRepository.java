package gr.xe.rating.service.repositories;

import gr.xe.rating.service.models.db.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingsRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllByRatedEntityEquals(String ratedEntity);
    boolean existsByRatedEntity(String ratedEntity);
}
