package gr.xe.rating.service.controllers;

import gr.xe.rating.service.models.dto.ComputedRatingDto;
import gr.xe.rating.service.models.dto.RatingDto;
import gr.xe.rating.service.services.RatingsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/ratings")
public class RatingsController {

    private static final Logger logger = LoggerFactory.getLogger(RatingsController.class);
    private final RatingsService service;

    @PostMapping
    public ResponseEntity<RatingDto> createRating(@RequestBody RatingDto rating) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveRating(rating));
    }

    @GetMapping(path = "/{rated_entity}")
    public ResponseEntity<ComputedRatingDto> getRatingFor(@PathVariable("rated_entity") String ratedEntity) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getRatingFor(ratedEntity));
    }

}
