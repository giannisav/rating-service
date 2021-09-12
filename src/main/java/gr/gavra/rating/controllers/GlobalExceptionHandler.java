package gr.gavra.rating.controllers;

import gr.gavra.rating.exceptions.InvalidGivenArgumentException;
import gr.gavra.rating.exceptions.NotFoundRatedEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ InvalidGivenArgumentException.class })
  public ResponseEntity<String> handleBadRequest(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler({ NotFoundRatedEntity.class })
  public ResponseEntity<String> handleNotFound(Exception ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }
}
