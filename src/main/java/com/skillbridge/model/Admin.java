package com.skillbridge.model;

// Admin user model extending User
public class Admin extends User {

    public Admin() {
        super();
        this.role = "ADMIN";
    }

    public Admin(int userId, String fullName, String email, String passwordHash, String bio, String availability, double reliabilityScore) {
        super(userId, fullName, email, passwordHash, "ADMIN", bio, availability, reliabilityScore);
    }

    @Override
    public String getUserTypeDescription() {
        return "Admin - Manages platform users, monitors activity, and ensures security";
    }
}
