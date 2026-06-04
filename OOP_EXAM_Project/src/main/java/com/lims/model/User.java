package com.lims.model;

import java.time.LocalDateTime;

/**
 * User.java
 *
 * PURPOSE: Represents a single row from the 'users' table.
 * Every field here maps to a column in the database.
 *
 * A "model" class (also called a POJO — Plain Old Java Object) only holds
 * data. It has no logic. It just stores values and lets you get/set them.
 *
 * HOW TO USE:
 *   User user = userDAO.findByEmail("someone@email.com");
 *   System.out.println(user.getName());  // prints their name
 */
public class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;  // NEVER store the plain password
    private String role;          // use AppConstants.ROLE_* values
    private boolean isFirstLogin;
    private boolean isVerified;
    private LocalDateTime createdAt;

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    // Empty constructor — needed for creating a new User object to fill in
    public User() {}

    // Full constructor — used when loading a user from the database
    public User(int id, String name, String email, String passwordHash,
                String role, boolean isFirstLogin, boolean isVerified,
                LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isFirstLogin = isFirstLogin;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // These are the standard way to read/write fields in Java.
    // In IntelliJ: right-click → Generate → Getters and Setters
    // ---------------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isFirstLogin() { return isFirstLogin; }
    public void setFirstLogin(boolean firstLogin) { isFirstLogin = firstLogin; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

