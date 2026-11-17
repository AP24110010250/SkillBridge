package com.skillbridge.model;

import java.sql.Timestamp;

// Abstract User class demonstrating OOP inheritance and polymorphism
public abstract class User {

    protected int userId;
    protected String fullName;
    protected String email;
    protected String passwordHash;
    protected String role;
    protected String bio;
    protected String availability;
    protected double reliabilityScore;
    protected Timestamp createdAt;

    // Default constructor
    public User() {
        this.reliabilityScore = 100.0;
    }

    // Parameterized constructor
    public User(int userId, String fullName, String email, String passwordHash, String role, String bio, String availability, double reliabilityScore) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.bio = bio;
        this.availability = availability;
        this.reliabilityScore = reliabilityScore;
    }

    // Abstract method demonstrating polymorphism
    public abstract String getUserTypeDescription();

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public double getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(double reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
