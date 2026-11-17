package com.skillbridge.exception;

// Exception thrown when an unauthorized user attempts an admin-only action
public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
