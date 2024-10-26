package com.management.ManagementInventaris.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * The NoNumbersValidator class is the validator that implements the logic to check if a given
 * String contains only alphabetic characters (i.e., no numeric characters).
 *
 * <p>This validator is associated with the NoNumbers annotation. It overrides the
 * ConstraintValidator's isValid method to provide the validation logic.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @Getter
 * @Setter
 * public class User {
 *
 *     @NoNumbers(message = "Username must not contain numbers")
 *
 *     private String username;
 * }
 * }
 * </pre>
 *
 * @see com.management.ManagementInventaris.validations.NoNumbers
 */
public final class NoNumbersValidator implements ConstraintValidator<NoNumbers, String> {
    /**
     * Initializes the validator in preparation for isValid calls. This method is typically used
     * to initialize any resources the validator might need.
     *
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(NoNumbers constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Implements the validation logic. The state of value must not be altered.
     *
     * <p>This method returns true if the value is not null and contains only alphabetic characters,
     * otherwise it returns false.</p>
     *
     * @param value   the object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code true} if {@code value} is valid, {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("[a-zA-Z]+");
    }
}