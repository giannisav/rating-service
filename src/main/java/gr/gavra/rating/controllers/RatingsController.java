package gr.gavra.rating.controllers;

import gr.gavra.rating.models.dto.ComputedRatingDto;
import gr.gavra.rating.models.dto.RatingDTO;
import gr.gavra.rating.services.RatingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/ratings")
public class RatingsController {

  private final RatingsService service;

  @PostMapping
  public ResponseEntity<RatingDTO> createRating(@RequestBody RatingDTO rating) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.saveRating(rating));
  }

  @GetMapping(path = "/{rated_entity}")
  public ResponseEntity<ComputedRatingDto> getRatingFor(
      @PathVariable("rated_entity") String ratedEntity) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getRatingFor(ratedEntity));
  }

}
