package gr.xe.rating.service.utils;

import gr.xe.rating.service.exceptions.InvalidGivenArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BooleanValidatorTest {

    private static final String EXCEPTION_MESSAGE = "Rated Entity should not be null or empty.";
    
    private BooleanValidator validator;
    
    @BeforeEach
    public void setUp() {
        validator = new BooleanValidator();
    }

    @Test
    public void validate_WhenGivenConditionIsFalse_ShouldThrowSuppliedException() {
        try {
            validator.validate(Boolean.FALSE, () -> new InvalidGivenArgumentException(EXCEPTION_MESSAGE));
        } catch (InvalidGivenArgumentException ex) {
            Assert.isTrue(ex.getMessage().equals(EXCEPTION_MESSAGE), "Exception message is not the same");
        }
    }

    @Test
    public void validate_WhenGivenConditionIsTrue_ShouldDoNothing() {
        assertDoesNotThrow(() ->
                validator.validate(Boolean.TRUE, () -> new InvalidGivenArgumentException(EXCEPTION_MESSAGE)));
    }

}