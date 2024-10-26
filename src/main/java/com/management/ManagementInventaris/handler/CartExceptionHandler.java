package com.management.ManagementInventaris.handler;

import com.management.ManagementInventaris.exception.CartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for handling exceptions related to cart operations.
 * This class is responsible for capturing exceptions thrown by the cart-related services and
 * providing appropriate HTTP responses. It uses Spring's {@code @ControllerAdvice} to handle
 * exceptions globally across the application.
 *
 * <p>The {@code CartExceptionHandler} class handles:
 * <ul>
 *     <li>{@link CartException} - Custom exception specific to cart operations.</li>
 *     <li>{@link Exception} - Any other unforeseen exceptions that may occur.</li>
 * </ul>
 *
 * The responses are formatted using the {@link ErrorDetails} class to provide meaningful
 * information about the error.
 *
 * @see CartException
 * @see ErrorDetails
 */
@ControllerAdvice
public class CartExceptionHandler {

    /**
     * Handles {@link CartException} and provides a {@link ResponseEntity} with a
     * {@link ErrorDetails} object containing details of the exception.
     *
     * <p>The HTTP status for {@link CartException} is set to {@link HttpStatus#BAD_REQUEST}.
     *
     * @param ex The {@link CartException} to handle.
     * @return A {@link ResponseEntity} containing {@link ErrorDetails} and HTTP status {@link HttpStatus#BAD_REQUEST}.
     */
    @ExceptionHandler(CartException.class)
    public ResponseEntity<ErrorDetails> handleCartException(CartException ex) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getDetails());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any other {@link Exception} that is not specifically handled by other handlers.
     * Provides a {@link ResponseEntity} with a default error message and {@link ErrorDetails}.
     *
     * <p>The HTTP status for general exceptions is set to {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     *
     * @param ex The {@link Exception} to handle.
     * @return A {@link ResponseEntity} containing {@link ErrorDetails} and HTTP status {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneralException(Exception ex) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "An unexpected error occurred");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Represents the details of an error response.
 *
 * <p>This class is used to encapsulate the information about an error that occurs in the application.
 * It includes the HTTP status, a message describing the error, and additional details if available.
 *
 * @see CartExceptionHandler
 */
record ErrorDetails(HttpStatus status, String message, String details) {
    /**
     * Constructs a new {@code ErrorDetails} instance.
     *
     * @param status  The HTTP status associated with the error.
     * @param message A descriptive message about the error.
     * @param details Additional details about the error, which may include specific information or context.
     */
    ErrorDetails {
    }
}