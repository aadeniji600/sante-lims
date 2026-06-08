package com.lims.dao;

import com.lims.model.TestRequest;
import com.lims.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestRequestDAO {

    public static List<TestRequest> getCustomerOrders(int customerId) {
        List<TestRequest> list = new ArrayList<>();
        String sql =
            "SELECT r.id, r.customer_id, r.test_type_id, r.payment_status, " +
            "       r.requested_at, r.expected_ready_at " +
            "FROM test_requests r " +
            "WHERE r.customer_id = ? " +
            "ORDER BY r.requested_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TestRequest req = new TestRequest();
                req.setId(rs.getInt("id"));
                req.setCustomerId(rs.getInt("customer_id"));
                req.setTestTypeId(rs.getInt("test_type_id"));
                req.setPaymentStatus(rs.getString("payment_status"));

                Timestamp reqAt = rs.getTimestamp("requested_at");
                if (reqAt != null) req.setRequestedAt(reqAt.toLocalDateTime());

                Timestamp readyAt = rs.getTimestamp("expected_ready_at");
                if (readyAt != null) req.setExpectedReadyAt(readyAt.toLocalDateTime());

                list.add(req);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading customer orders: " + e.getMessage(), e);
        }

        return list;
    }

    public static void placeOrder(int customerId, int testTypeId, int tatHours) {
        String sql =
            "INSERT INTO test_requests (customer_id, test_type_id, payment_status, " +
            "requested_at, expected_ready_at) " +
            "VALUES (?, ?, 'UNPAID', NOW(), NOW() + INTERVAL '" + tatHours + " hours')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, testTypeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error placing order: " + e.getMessage(), e);
        }
    }
    
    public static boolean createOrder(int customerId, int testTypeId, int tatHours) {
    String sql =
        "INSERT INTO test_requests (customer_id, test_type_id, payment_status, " +
        "requested_at, expected_ready_at) " +
        "VALUES (?, ?, 'UNPAID', NOW(), NOW() + INTERVAL '" + tatHours + " hours')";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, customerId);
        ps.setInt(2, testTypeId);
        ps.executeUpdate();
        return true; // success

    } catch (SQLException e) {
        System.err.println("Error creating order: " + e.getMessage());
        return false; // failure — controller will show error alert to user
    }
    }
}