package com.management.ManagementInventaris.utils;

import com.management.ManagementInventaris.filter.ToxicWordFilter;
import com.management.ManagementInventaris.validations.NoToxic;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * This class is an Aspect for filtering toxic words in method parameters.
 * It uses the {@link NoToxic} annotation to identify methods that need to be processed.
 *
 * @author Dearly Febriano Irwansyah
 * @since 6.4.5
 */
@Aspect
@Component
public class NoToxicAspect {

    protected static final Logger logger = LoggerFactory.getLogger(NoToxicAspect.class);

    /**
     * Around advice that intercepts method calls annotated with {@link NoToxic},
     * validates the parameters, and either filters or blocks execution based on the annotation's properties.
     *
     * @param joinPoint The join point for the intercepted method.
     * @param noToxic The annotation instance containing custom words, replacement, and validation flag.
     * @return The result of the method execution after filtering toxic words.
     * @throws Throwable If validation fails or an error occurs during method execution.
     */
    @Around("@annotation(noToxic)")
    public Object filterToxicWords(ProceedingJoinPoint joinPoint, NoToxic noToxic) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String[] customWords = noToxic.customWords();
        String replacement = noToxic.replacement();
        boolean validate = noToxic.validate();

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String originalText) {

                if (validate && ToxicWordFilter.containsToxicWords(originalText, customWords)) {
                    logger.error("Validation failed for argument: {}. Contains toxic words.", originalText);
                    throw new IllegalArgumentException("Method argument contains toxic words: " + originalText);
                }

                String filteredText = ToxicWordFilter.filterToxic(originalText, customWords, replacement);
                if (!filteredText.equals(originalText)) {
                    logger.info("Filtered argument: {} -> {}", originalText, filteredText);
                }
                args[i] = filteredText;
            }
        }

        logger.debug("Proceeding with method execution for {}.{} with arguments: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(args));

        return joinPoint.proceed(args);
    }
}