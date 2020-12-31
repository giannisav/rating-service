package gr.xe.rating.service.exceptions;

public class NotFoundRatedEntity extends RuntimeException {

    public NotFoundRatedEntity(String message) {
        super(message);
    }
}
