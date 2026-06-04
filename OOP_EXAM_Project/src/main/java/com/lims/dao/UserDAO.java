package com.lims.dao;

import com.lims.model.User;
import com.lims.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * UserDAO.java
 *
 * PURPOSE: All database operations related to the 'users' table live here.
 * No other class should write SQL for users — they call these methods instead.
 *
 * "DAO" (Data Access Object) is a standard pattern where each database table
 * gets its own class that handles reading and writing for that table.
 *
 * HOW TO USE (for Members 2 and 3):
 *   UserDAO userDAO = new UserDAO();
 *   User u = userDAO.findByEmail("customer@example.com");
 */
public class UserDAO {

    /**
     * Finds a user by their email address.
     * Returns null if no user with that email exists.
     * Used during login.
     */
    public User findByEmail(String email) {
        // PreparedStatement prevents SQL Injection attacks.
        // The '?' is a placeholder — the actual value is set on the next line.
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email); // replace first '?' with the email value
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // rs.next() moves to the first result row.
                // If it returns false, no user was found.
                return mapRowToUser(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + e.getMessage(), e);
        }

        return null; // no user found
    }

    /**
     * Saves a brand new user to the database.
     * The password must already be BCrypt-hashed before calling this.
     */
    public void save(User user) {
        String sql = """
            INSERT INTO users (name, email, password_hash, role, is_first_login, is_verified)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setBoolean(5, user.isFirstLogin());
            stmt.setBoolean(6, user.isVerified());

            stmt.executeUpdate(); // executeUpdate() for INSERT/UPDATE/DELETE

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the user's password hash in the database.
     * Called after the user completes the Force Password Change flow.
     */
    public void updatePassword(int userId, String newPasswordHash) {
        // Also sets is_first_login = FALSE so they aren't forced again next time
        String sql = "UPDATE users SET password_hash = ?, is_first_login = FALSE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }

    /**
     * Marks a user's email as verified.
     * Called after the customer clicks the verification link.
     */
    public void markEmailVerified(int userId) {
        String sql = "UPDATE users SET is_verified = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error verifying email: " + e.getMessage(), e);
        }
    }
    
    /**
    * Temporarily stores the email verification code for a customer.
    * We reuse the password_hash column to store it until they verify.
    * After verification, this gets replaced with their real password hash.
    */
    public void saveVerificationCode(String email, String code) {
    String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, code);
        stmt.setString(2, email);
        stmt.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Error saving verification code: " + e.getMessage(), e);
    }
}

    /**
     * PRIVATE HELPER METHOD
     * Converts a database row (ResultSet) into a User Java object.
     * Called internally so we don't repeat this mapping in every method above.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setFirstLogin(rs.getBoolean("is_first_login"));
        user.setVerified(rs.getBoolean("is_verified"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }

        return user;
    }
}
