package com.skillbridge.service;

import java.util.List;
import com.skillbridge.dao.UserDao;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.exception.InvalidLoginException;
import com.skillbridge.exception.UserAlreadyExistsException;
import com.skillbridge.model.Admin;
import com.skillbridge.model.Mentor;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.util.PasswordUtil;

// Service handling User authentication, registration, and profile management
public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Register a new user
    public User registerUser(String fullName, String email, String password, String role, String bio, String availability)
            throws DatabaseException, UserAlreadyExistsException {

        // Validate basic inputs
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // Check if user already exists
        User existing = userDao.findByEmail(normalizedEmail);
        if (existing != null) {
            throw new UserAlreadyExistsException("An account with email " + normalizedEmail + " already exists");
        }

        // Determine user type polymorphically
        String userRole = (role != null && !role.trim().isEmpty()) ? role.trim().toUpperCase() : "STUDENT";
        User user;
        if ("MENTOR".equals(userRole)) {
            user = new Mentor();
        } else if ("ADMIN".equals(userRole)) {
            user = new Admin();
        } else {
            user = new Student();
        }

        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole(userRole);
        user.setBio(bio != null ? bio.trim() : "");
        user.setAvailability(availability != null ? availability.trim() : "");
        user.setReliabilityScore(100.0);

        boolean created = userDao.insert(user);
        if (!created) {
            throw new DatabaseException("Failed to register user in database");
        }

        return user;
    }

    // Authenticate user login
    public User loginUser(String email, String password) throws DatabaseException, InvalidLoginException {
        if (email == null || password == null) {
            throw new InvalidLoginException("Email and password must not be empty");
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = userDao.findByEmail(normalizedEmail);

        if (user == null) {
            throw new InvalidLoginException("No account found with email: " + normalizedEmail);
        }

        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordHash());
        if (!valid) {
            throw new InvalidLoginException("Incorrect password for user: " + normalizedEmail);
        }

        return user;
    }

    // Update user profile details
    public boolean updateProfile(int userId, String fullName, String bio, String availability) throws DatabaseException {
        User user = userDao.findById(userId);
        if (user == null) {
            return false;
        }

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (bio != null) {
            user.setBio(bio.trim());
        }
        if (availability != null) {
            user.setAvailability(availability.trim());
        }

        return userDao.update(user);
    }

    // Retrieve user by ID
    public User getUserById(int userId) throws DatabaseException {
        return userDao.findById(userId);
    }

    // Retrieve user by email
    public User getUserByEmail(String email) throws DatabaseException {
        if (email == null) {
            return null;
        }
        return userDao.findByEmail(email.trim().toLowerCase());
    }

    // Get all registered users
    public List<User> getAllUsers() throws DatabaseException {
        return userDao.findAll();
    }
}
