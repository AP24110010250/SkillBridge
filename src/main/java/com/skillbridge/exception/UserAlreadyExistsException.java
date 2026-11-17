package com.skillbridge.exception;

// Exception thrown when attempting to register an already existing email
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
