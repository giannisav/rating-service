package gr.gavra.rating.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import gr.gavra.rating.exceptions.InvalidGivenArgumentException;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class BooleanValidatorTest {

  private static final String EXCEPTION_MESSAGE = "Rated Entity should not be null or empty.";

  @Test
  public void validate_WhenGivenConditionIsFalse_ShouldThrowSuppliedException() {
    try {
      BooleanValidator.validate(Boolean.FALSE,
          () -> new InvalidGivenArgumentException(EXCEPTION_MESSAGE));
    } catch (InvalidGivenArgumentException ex) {
      Assert.isTrue(ex.getMessage().equals(EXCEPTION_MESSAGE), "Exception message is not the same");
    }
  }

  @Test
  public void validate_WhenGivenConditionIsTrue_ShouldDoNothing() {
    assertDoesNotThrow(() ->
        BooleanValidator.validate(Boolean.TRUE,
            () -> new InvalidGivenArgumentException(EXCEPTION_MESSAGE)));
  }

}