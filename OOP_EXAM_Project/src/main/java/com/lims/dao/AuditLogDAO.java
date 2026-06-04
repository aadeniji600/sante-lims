package com.lims.dao;

import com.lims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * AuditLogDAO.java
 *
 * PURPOSE: Handles writing to the audit_log table in the database.
 * This is an append-only log — we ONLY ever INSERT here.
 * No UPDATE, no DELETE, ever. This is intentional.
 *
 * The audit log records every important action in the system so the
 * Super Admin can see a full history of what happened and who did it.
 *
 * HOW TO USE (for Members 2 and 3):
 *   AuditLogDAO auditLogDAO = new AuditLogDAO();
 *   auditLogDAO.log(userId, "Marked request #5 as PAID", "test_request", 5);
 */
public class AuditLogDAO {

    /**
     * Writes a new entry to the audit log.
     *
     * @param userId     - the ID of the user who performed the action
     *                     (get this from SessionManager.getCurrentUser().getId())
     * @param action     - plain English description of what happened
     *                     e.g. "User logged in" or "Result validated for sample #12"
     * @param entityType - which table was affected
     *                     e.g. "users", "test_request", "result", "sample"
     * @param entityId   - the ID of the specific record that was affected
     *                     e.g. if test_request #7 was updated, pass 7
     */
    public void log(int userId, String action, String entityType, int entityId) {

        String sql = """
                INSERT INTO audit_log (user_id, action, entity_type, entity_id)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, entityType);
            stmt.setInt(4, entityId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            // We print the error but don't crash the app if audit logging fails.
            // Audit logging should never interrupt the main flow of the application.
            System.err.println("Audit log failed: " + e.getMessage());
        }
    }
}