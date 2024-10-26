package com.management.ManagementInventaris.validations;

import com.management.ManagementInventaris.filter.ToxicWordFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Validator for the {@link NoToxic} annotation.
 * It checks if the input contains toxic words based on the {@link NoToxic} settings.
 */
@Getter
@Setter
public class NoToxicValidator implements ConstraintValidator<NoToxic, String> {

    protected String[] customWords;
    protected String replacement;
    protected boolean validate;

    /**
     * Initializes the validator in preparation for
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param annotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(NoToxic annotation) {
        this.customWords = annotation.customWords();
        this.replacement = annotation.replacement();
        this.validate = annotation.validate();
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (validate) return !ToxicWordFilter.containsToxicWords(value, customWords);
        return true; // No validation if validate is false
    }
}