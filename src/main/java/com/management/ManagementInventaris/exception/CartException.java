package com.management.ManagementInventaris.exception;

import lombok.Getter;

@Getter
public class CartException extends RuntimeException {
    private final String details;

    public CartException(String message, String details) {
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}