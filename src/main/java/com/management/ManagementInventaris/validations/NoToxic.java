package com.management.ManagementInventaris.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation for validating and filtering toxic words in method parameters.
 * Can be applied to fields, methods, and parameters.
 *
 * <p>Usage example:</p>
 * <pre>
 * &#64;NoToxic(message = "Inappropriate language found!", customWords = {"badword"})
 * private String content;
 * </pre>
 *
 * @version 6.4.5
 */
@Constraint(validatedBy = NoToxicValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface NoToxic {

    /**
     * Error message when validation fails.
     *
     * @return The error message.
     */
    String message() default "Content contains toxic words!";

    /**
     * Custom toxic words to filter or validate.
     *
     * @return An array of custom toxic words.
     */
    String[] customWords() default {};

    /**
     * Replacement string for toxic words.
     *
     * @return The replacement string.
     */
    String replacement() default "***";

    /**
     * Whether to strictly validate the presence of toxic words.
     *
     * @return True if validation is required, false otherwise.
     */
    boolean validate() default false;

    /**
     * Validation groups.
     *
     * @return Validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload associated with this constraint.
     *
     * @return Payload classes.
     */
    Class<? extends Payload>[] payload() default {};
}