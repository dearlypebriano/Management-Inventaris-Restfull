package com.management.ManagementInventaris.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The {@link NoNumbers} annotation is a custom constraint annotation used for validating that a given
 * String does not contain any numeric characters. This annotation can be applied to fields,
 * methods, parameters, and other annotations.
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @NoNumbers(message = "Name must not contain numbers")
 * private String name;
 * }
 * </pre>
 *
 * <p>The default error message is "NOT_LETTER". This can be overridden by providing a custom
 * message in the annotation. The annotation also supports validation groups and payloads.</p>
 *
 * <p>This annotation is validated by the NoNumbersValidator class.</p>
 *
 * @see com.management.ManagementInventaris.validations.NoNumbersValidator
 */
@Constraint(validatedBy = NoNumbersValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface NoNumbers {

    /**
     * The error message that will be returned if the String contains numeric characters.
     *
     * @return the error message
     */
    String message() default "NOT_LETTER";

    /**
     * Allows specification of validation groups, to which this constraint belongs.
     *
     * @return array of validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients of the Bean Validation API to assign custom payload objects to a constraint.
     *
     * @return array of payload classes
     */
    Class<? extends Payload>[] payload() default {};
}