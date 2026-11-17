package com.skillbridge.model;

// Mentor model extending User
public class Mentor extends User {

    public Mentor() {
        super();
        this.role = "MENTOR";
    }

    public Mentor(int userId, String fullName, String email, String passwordHash, String bio, String availability, double reliabilityScore) {
        super(userId, fullName, email, passwordHash, "MENTOR", bio, availability, reliabilityScore);
    }

    @Override
    public String getUserTypeDescription() {
        return "Mentor - Guides students and conducts advanced skill sessions";
    }
}
