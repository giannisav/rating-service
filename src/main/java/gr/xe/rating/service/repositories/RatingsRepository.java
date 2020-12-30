package gr.xe.rating.service.repositories;

import gr.xe.rating.service.models.db.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingsRepository extends JpaRepository<Rating, Long> {
}
