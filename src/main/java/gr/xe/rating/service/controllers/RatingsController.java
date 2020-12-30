package gr.xe.rating.service.controllers;

import gr.xe.rating.service.mappers.DbMappers;
import gr.xe.rating.service.mappers.DtoMappers;
import gr.xe.rating.service.models.dto.Rating;
import gr.xe.rating.service.repositories.RatingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/ratings")
public class RatingsController {

    private static final Logger logger = LoggerFactory.getLogger(RatingsController.class);
    private final RatingsRepository repository;

    public RatingsController (RatingsRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Rating> createRating (@RequestBody Rating rating) {
        var db = DbMappers.fromDtoModel(rating);
        repository.save(db);
        var dto = DtoMappers.fromDbModel(db);
        return ResponseEntity.ok(dto);
    }

}
