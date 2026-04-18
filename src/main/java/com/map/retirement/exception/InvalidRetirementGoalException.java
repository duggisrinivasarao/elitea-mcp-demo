package com.map.retirement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when retirement goal inputs are invalid.
 * Story: MAP-18 — Input Validation
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRetirementGoalException extends RuntimeException {
    public InvalidRetirementGoalException(String message) {
        super(message);
    }
}
