package com.skillbridge.exception;

// Exception thrown when a user attempts to add a duplicate skill entry
public class DuplicateSkillException extends Exception {
    public DuplicateSkillException(String message) {
        super(message);
    }
}
