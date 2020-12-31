package gr.xe.rating.service.utils;

import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Component
public class BooleanValidator {

    public <E extends RuntimeException> void validate(Boolean condition, Supplier<E> exceptionSupplier) {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }
}
