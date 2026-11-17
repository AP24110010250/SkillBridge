package com.skillbridge.model;

// Student model extending User
public class Student extends User {

    public Student() {
        super();
        this.role = "STUDENT";
    }

    public Student(int userId, String fullName, String email, String passwordHash, String bio, String availability, double reliabilityScore) {
        super(userId, fullName, email, passwordHash, "STUDENT", bio, availability, reliabilityScore);
    }

    @Override
    public String getUserTypeDescription() {
        return "Student - Learns skills and shares knowledge with peers";
    }
}
