package gr.gavra.rating.exceptions;

public class NotFoundRatedEntity extends RuntimeException {

  public NotFoundRatedEntity(String message) {
    super(message);
  }
}
