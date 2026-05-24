package com.studytracker.exception;

/**
 * Thrown when input data fails validation checks.
 */
public class ValidationException extends RuntimeException {

    private final String field;

    public ValidationException(String field, String message) {
        super(String.format("Validation failed for '%s': %s", field, message));
        this.field = field;
    }

    public String getField() { return field; }
}
