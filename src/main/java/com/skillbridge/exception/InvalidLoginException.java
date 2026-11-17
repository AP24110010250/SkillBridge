package com.skillbridge.exception;

// Exception thrown when user login fails due to invalid credentials
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message);
    }
}
