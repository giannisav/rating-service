package gr.gavra.rating.utils;

import java.util.function.Supplier;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BooleanValidator {

  public <E extends RuntimeException> void validate(Boolean condition,
      Supplier<E> exceptionSupplier) {
    if (!condition) {
      throw exceptionSupplier.get();
    }
  }
}
