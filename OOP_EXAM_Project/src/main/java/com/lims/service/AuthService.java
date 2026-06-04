package com.lims.service;

import com.lims.dao.AuditLogDAO;
import com.lims.dao.UserDAO;
import com.lims.model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * AuthService.java
 *
 * PURPOSE: Handles all authentication logic — login checks, password hashing,
 * and password verification. Controllers call these methods; they don't deal
 * with BCrypt directly.
 *
 * WHY SEPARATE FROM DAO?
 * The DAO just moves data in/out of the database.
 * The Service adds business rules on top. e.g. "if the user is not verified,
 * reject the login" is a business rule — it belongs here, not in the DAO.
 */
public class AuthService {

    // AuthService needs these DAOs to do its job
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    /**
     * Attempts to log in a user with email + plain-text password.
     *
     * @return the User object if login is successful
     * @throws IllegalArgumentException if login fails (with a reason message)
     *
     * The controller catches this exception and shows the message to the user.
     */
    public User login(String email, String plainPassword) {

        // Step 1: Find the user in the database
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No account found with that email.");
        }

        // Step 2: Check if email is verified (only for customers)
        if (user.getRole().equals("CUSTOMER") && !user.isVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in.");
        }

        // Step 3: Check the password using BCrypt
        // BCrypt.checkpw() compares a plain-text password against a stored hash
        // It returns true if they match, false otherwise
        if (!BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        // Step 4: Log the successful login to the audit trail
        auditLogDAO.log(user.getId(), "User logged in", "users", user.getId());

        return user;
    }

    /**
     * Hashes a plain-text password using BCrypt before storing it.
     * BCrypt.gensalt() generates a random "salt" to make every hash unique
     * even if two users have the same password.
     *
     * ALWAYS call this before saving any password to the database.
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifies that a plain-text password matches a stored BCrypt hash.
     * Used during the Force Password Change to confirm the new password.
     */
    public boolean checkPassword(String plainPassword, String storedHash) {
        return BCrypt.checkpw(plainPassword, storedHash);
    }
}
