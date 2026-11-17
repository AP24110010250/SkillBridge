package com.skillbridge;

import java.sql.Connection;
import java.util.List;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.service.SkillCategoryService;
import com.skillbridge.util.DatabaseConnection;

// Main verification runner for Phase 1
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       SkillBridge - Phase 1 Verification         ");
        System.out.println("==================================================");

        // 1. Demonstrate Core Java OOP concepts
        System.out.println("\n[1] Testing OOP Inheritance and Polymorphism:");
        User student = new Student(1, "Koushik Chava", "koushik@example.com", "pass123", "CSE Undergrad", "Evenings", 98.0);
        System.out.println("User Created: " + student.getFullName() + " (" + student.getEmail() + ")");
        System.out.println("Role: " + student.getRole());
        System.out.println("Polymorphic Description: " + student.getUserTypeDescription());

        // 2. Test Database Connection
        System.out.println("\n[2] Testing JDBC Database Connectivity:");
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("JDBC Connection SUCCESSFUL!");
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database Version: " + conn.getMetaData().getDatabaseProductVersion());
                DatabaseConnection.closeConnection(conn);
            }
        } catch (Exception e) {
            System.err.println("JDBC Connection FAILED: " + e.getMessage());
            System.err.println("Please check database.properties and ensure MySQL server is running.");
            return;
        }

        // 3. Test DAO & Service Layer Architecture
        System.out.println("\n[3] Testing DAO and Service Architecture:");
        try {
            SkillCategoryService categoryService = new SkillCategoryService();
            List<SkillCategory> categories = categoryService.getAllCategories();

            System.out.println("Total Skill Categories in DB: " + categories.size());
            for (SkillCategory cat : categories) {
                System.out.println(" - [" + cat.getCategoryId() + "] " + cat.getCategoryName() + ": " + cat.getDescription());
            }

            // Check if test category already exists
            boolean exists = false;
            for (SkillCategory cat : categories) {
                if ("Cloud & DevOps".equalsIgnoreCase(cat.getCategoryName())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                boolean added = categoryService.addCategory("Cloud & DevOps", "Cloud platforms and CI/CD pipelines");
                System.out.println("Added test category 'Cloud & DevOps': " + (added ? "SUCCESS" : "FAILED"));
            } else {
                System.out.println("Category 'Cloud & DevOps' already present.");
            }

        } catch (Exception e) {
            System.err.println("DAO/Service verification error: " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("       Phase 1 Foundation Setup Complete!         ");
        System.out.println("==================================================");
    }
}
