package com.lims.model;

import java.time.LocalDateTime;

/**
 * AuditLog.java
 * Represents one row from the audit_log table.
 * Used by the Super Admin to view a history of all actions in the system.
 *
 * NOTE: This model is READ-ONLY in practice. We only ever INSERT into
 * the audit_log table (via AuditLogDAO), never update or delete rows.
 * This model is used to display existing log entries on the audit trail screen.
 */
public class AuditLog {

    private int id;
    private int userId;       // who performed the action
    private String action;    // plain English description e.g. "User logged in"
    private String entityType; // which table was affected e.g. "test_request"
    private int entityId;     // ID of the specific record affected
    private LocalDateTime loggedAt;

    // Empty constructor
    public AuditLog() {}

    // Full constructor
    public AuditLog(int id, int userId, String action, String entityType,
                    int entityId, LocalDateTime loggedAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.loggedAt = loggedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }

    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}
