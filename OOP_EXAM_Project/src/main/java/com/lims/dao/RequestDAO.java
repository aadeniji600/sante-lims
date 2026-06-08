package com.lims.dao;

import com.lims.model.TestRequest;
import com.lims.util.DBConnection;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestDAO.java
 * Reads the Test Request Queue and updates payment status.
 * Used by Member 2's RequestQueueController.
 */
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
}
