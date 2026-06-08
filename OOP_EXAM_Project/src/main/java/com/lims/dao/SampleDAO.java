package com.lims.dao;

import com.lims.model.Sample;
import com.lims.util.DBConnection;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SampleDAO.java
 * Sample lifecycle tracking: COLLECTED -> PROCESSING -> VALIDATION -> COMPLETED
 * Used by Member 2's SampleTrackingController.
 */
public class SampleDAO {

    public static final String[] STATUSES = {"COLLECTED", "PROCESSING", "VALIDATION", "COMPLETED"};
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private static String fmt(Timestamp ts) {
        return ts == null ? "" : ts.toLocalDateTime().format(FMT);
    }

    public List<Sample> findAll() throws SQLException {
        List<Sample> list = new ArrayList<>();
        String sql =
            "SELECT s.id, s.request_id, COALESCE(u.full_name, u.name) AS customer, t.name AS test, " +
            "       s.status, s.updated_at " +
            "FROM samples s " +
            "JOIN test_requests r ON r.id = s.request_id " +
            "JOIN users u ON u.id = r.customer_id " +
            "JOIN tests t ON t.id = r.test_type_id " +
            "ORDER BY s.id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sample s = new Sample();
                s.setId(rs.getInt("id"));
                s.setRequestId(rs.getInt("request_id"));
                // Store customer name and test name temporarily using available fields
                list.add(buildSample(rs));
            }
        }
        return list;
    }

    private Sample buildSample(ResultSet rs) throws SQLException {
        // We use a display-friendly constructor approach
        // The Sample model stores requestId, status etc.
        // For table display we add customerName/testName via a subclass approach
        return new SampleDisplay(
            rs.getInt("id"),
            rs.getInt("request_id"),
            rs.getString("customer"),
            rs.getString("test"),
            rs.getString("status"),
            fmt(rs.getTimestamp("updated_at"))
        );
    }

    public void register(int requestId) throws SQLException {
        String sql = "INSERT INTO samples (request_id, status, updated_at) VALUES (?, 'COLLECTED', NOW())";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "Sample collected for request #" + requestId, "samples", requestId);
    }

    public void updateStatus(int sampleId, String status) throws SQLException {
        String sql = "UPDATE samples SET status=?, updated_at=NOW() WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, sampleId);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "Sample #" + sampleId + " status -> " + status, "samples", sampleId);
    }

    /**
     * SampleDisplay extends Sample to carry the extra display fields
     * (customerName, testName) that come from the JOIN query.
     * Member 2's SampleTrackingController uses these for table columns.
     */
    public static class SampleDisplay extends Sample {
        private final String customerName;
        private final String testName;
        private final String updatedAtDisplay;

        public SampleDisplay(int id, int requestId, String customerName,
                             String testName, String status, String updatedAt) {
            setId(id);
            setRequestId(requestId);
            setStatus(status);
            this.customerName = customerName;
            this.testName = testName;
            this.updatedAtDisplay = updatedAt;
        }

        public String getCustomerName() { return customerName; }
        public String getTestName()     { return testName; }
        // Override so table column shows formatted string instead of LocalDateTime
        public String getUpdatedAt()    { return updatedAtDisplay; }
    }
}
