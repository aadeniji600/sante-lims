package com.lims.dao;

import com.lims.model.TestRequest;
import com.lims.util.DBConnection;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class RequestDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private static String fmt(Timestamp ts) {
        return ts == null ? "" : ts.toLocalDateTime().format(FMT);
    }

    public List<TestRequest> findAll() throws SQLException {
        List<TestRequest> list = new ArrayList<>();
        String sql =
            "SELECT r.id, r.customer_id, COALESCE(u.full_name, u.name) AS customer, " +
            "       r.test_type_id AS test_id, t.name AS test, t.price, r.payment_status, r.requested_at " +
            "FROM test_requests r " +
            "JOIN users u ON u.id = r.customer_id " +
            "JOIN tests t ON t.id = r.test_type_id " +
            "ORDER BY r.requested_at DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TestRequest(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer"),
                        rs.getInt("test_id"),
                        rs.getString("test"),
                        rs.getDouble("price"),
                        rs.getString("payment_status"),
                        fmt(rs.getTimestamp("requested_at"))));
            }
        }
        return list;
    }

    public List<TestRequest> findPaidWithoutSample() throws SQLException {
        List<TestRequest> list = new ArrayList<>();
        String sql =
            "SELECT r.id, r.customer_id, COALESCE(u.full_name, u.name) AS customer, " +
            "       r.test_type_id AS test_id, t.name AS test, t.price, r.payment_status, r.requested_at " +
            "FROM test_requests r " +
            "JOIN users u ON u.id = r.customer_id " +
            "JOIN tests t ON t.id = r.test_type_id " +
            "LEFT JOIN samples s ON s.request_id = r.id " +
            "WHERE r.payment_status = 'PAID' AND s.id IS NULL " +
            "ORDER BY r.requested_at";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TestRequest(
                        rs.getInt("id"), rs.getInt("customer_id"), rs.getString("customer"),
                        rs.getInt("test_id"), rs.getString("test"), rs.getDouble("price"),
                        rs.getString("payment_status"), fmt(rs.getTimestamp("requested_at"))));
            }
        }
        return list;
    }

    public void markPaid(int requestId) throws SQLException {
        String sql = "UPDATE test_requests SET payment_status='PAID' WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "Request #" + requestId + " marked PAID", "test_requests", requestId);
    }
    
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

public static List<CustomerResultDTO> getValidatedResultsForCustomer(int customerId) {
    List<CustomerResultDTO> list = new ArrayList<>();
    String sql =
        "SELECT res.id, t.name AS test, res.file_path, res.result_text, res.validated_at " +
        "FROM results res " +
        "JOIN test_requests r ON r.id = res.request_id " +
        "JOIN tests t ON t.id = r.test_type_id " +
        "WHERE r.customer_id = ? AND res.validated = TRUE " +
        "ORDER BY res.validated_at DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Timestamp ts = rs.getTimestamp("validated_at");
            java.time.LocalDateTime validatedAt = ts != null ? ts.toLocalDateTime() : null;

            list.add(new CustomerResultDTO(
                rs.getInt("id"),
                rs.getString("test"),
                rs.getString("file_path"),
                rs.getString("result_text"),
                validatedAt
            ));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error loading customer results: " + e.getMessage(), e);
    }

    return list;
  }
}
