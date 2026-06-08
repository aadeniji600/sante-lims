package com.lims.dao;

import com.lims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestRequestDAO {

    /**
     * Inserts a new test request into the database.
     * Calculates expected_ready_at dynamically using Postgres interval math.
     */
    public static boolean createOrder(int customerId, int testTypeId, int tatHours) {
        String query = "INSERT INTO test_requests (customer_id, test_type_id, payment_status, expected_ready_at) " +
                       "VALUES (?, ?, 'UNPAID', NOW() + (? * INTERVAL '1 hour'))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, testTypeId);
            pstmt.setInt(3, tatHours);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error during checkout: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all active test requests for a specific customer.
     */
    public static java.util.List<com.lims.model.TestRequest> getCustomerOrders(int customerId) {
        java.util.List<com.lims.model.TestRequest> orders = new java.util.ArrayList<>();
        String query = "SELECT id, payment_status, expected_ready_at FROM test_requests WHERE customer_id = ? ORDER BY requested_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setInt(1, customerId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    com.lims.model.TestRequest req = new com.lims.model.TestRequest();
                    req.setId(rs.getInt("id"));
                    req.setPaymentStatus(rs.getString("payment_status"));
                    
                    // Convert SQL Timestamp to Java LocalDateTime
                    java.sql.Timestamp ts = rs.getTimestamp("expected_ready_at");
                    if (ts != null) {
                        req.setExpectedReadyAt(ts.toLocalDateTime());
                    }
                    orders.add(req);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer orders: " + e.getMessage());
        }
        return orders;
    }
}