package com.lims.dao;

import com.lims.model.LabResult;
import com.lims.util.DBConnection;
import com.lims.util.SessionManager;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ResultDAO.java
 * Result upload and manual validation.
 * After validate() is called, the customer can see their result.
 * Used by Member 2's ResultUploadController.
 */
public class ResultDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private static String fmt(Timestamp ts) {
        return ts == null ? "" : ts.toLocalDateTime().format(FMT);
    }

    public List<LabResult> findAll() throws SQLException {
        List<LabResult> list = new ArrayList<>();
        String sql =
            "SELECT res.id, res.request_id, COALESCE(u.full_name, u.name) AS customer, t.name AS test, " +
            "       res.file_path, res.result_text, res.validated, res.uploaded_at " +
            "FROM results res " +
            "JOIN test_requests r ON r.id = res.request_id " +
            "JOIN users u ON u.id = r.customer_id " +
            "JOIN tests t ON t.id = r.test_type_id " +
            "ORDER BY res.id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new LabResult(
                        rs.getInt("id"),
                        rs.getInt("request_id"),
                        rs.getString("customer"),
                        rs.getString("test"),
                        rs.getString("file_path"),
                        rs.getString("result_text"),
                        rs.getBoolean("validated"),
                        fmt(rs.getTimestamp("uploaded_at"))));
            }
        }
        return list;
    }

    public void upload(int requestId, String filePath, String resultText) throws SQLException {
        String sql = "INSERT INTO results (request_id, file_path, result_text, validated, uploaded_at) " +
                     "VALUES (?,?,?,FALSE, NOW())";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            if (filePath == null || filePath.isBlank()) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, filePath);
            if (resultText == null || resultText.isBlank()) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, resultText);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "Result uploaded for request #" + requestId, "results", requestId);
    }

    public void validate(int resultId) throws SQLException {
        String sql = "UPDATE results SET validated=TRUE, validated_by=?, validated_at=NOW() WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            // Use SessionManager (your foundation class) to get current user ID
            int uid = SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getId() : 0;
            if (uid > 0) ps.setInt(1, uid);
            else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, resultId);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(
            SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getId() : 0,
            "Result #" + resultId + " validated and released to customer",
            "results", resultId
        );
    }
    
    /**
 * CustomerResultDTO
 * Holds result data formatted for the customer's Result Vault screen.
 */
public static class CustomerResultDTO {
    private final int id;
    private final String testName;
    private final String fileUrl;
    private final String resultText;
    private final java.time.LocalDateTime validatedAt;

    public CustomerResultDTO(int id, String testName, String fileUrl,
                              String resultText, java.time.LocalDateTime validatedAt) {
        this.id = id;
        this.testName = testName;
        this.fileUrl = fileUrl;
        this.resultText = resultText;
        this.validatedAt = validatedAt;
    }

    public int getId()                              { return id; }
    public String getTestName()                     { return testName; }
    public String getFileUrl()                      { return fileUrl; }
    public String getResultText()                   { return resultText; }
    public java.time.LocalDateTime getValidatedAt() { return validatedAt; }
}

/**
 * Returns all validated results for a specific customer.
 * Only shows results where validated = TRUE.
 */
public static java.util.List<CustomerResultDTO> getValidatedResultsForCustomer(int customerId) {
    java.util.List<CustomerResultDTO> list = new java.util.ArrayList<>();
    String sql =
        "SELECT res.id, t.name AS test, res.file_path, res.result_text, res.validated_at " +
        "FROM results res " +
        "JOIN test_requests r ON r.id = res.request_id " +
        "JOIN tests t ON t.id = r.test_type_id " +
        "WHERE r.customer_id = ? AND res.validated = TRUE " +
        "ORDER BY res.validated_at DESC";

    try (java.sql.Connection conn = com.lims.util.DBConnection.getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, customerId);
        java.sql.ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            java.sql.Timestamp ts = rs.getTimestamp("validated_at");
            java.time.LocalDateTime validatedAt = ts != null ? ts.toLocalDateTime() : null;
            list.add(new CustomerResultDTO(
                rs.getInt("id"),
                rs.getString("test"),
                rs.getString("file_path"),
                rs.getString("result_text"),
                validatedAt
            ));
        }

    } catch (java.sql.SQLException e) {
        throw new RuntimeException("Error loading customer results: " + e.getMessage(), e);
    }

    return list;

}
}
