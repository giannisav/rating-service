package gr.gavra.rating.repositories;

import gr.gavra.rating.models.db.Rating;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RatingsRepository extends JpaRepository<Rating, Long> {

  List<Rating> findAllByRatedEntityEquals(String ratedEntity);

  boolean existsByRatedEntity(String ratedEntity);

  @Modifying
  @Query("delete from Rating r where r.id in (?1)")
  void deleteOldGames(List<Long> ids);
}
