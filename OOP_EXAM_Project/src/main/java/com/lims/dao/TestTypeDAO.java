package com.lims.dao;

import com.lims.model.TestType;
import com.lims.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TestTypeDAO.java
 * Used by Member 3's TestBrowserController to load available tests
 * for the customer to browse and order from.
 */
public class TestTypeDAO {

    /**
     * Returns all available tests from the tests table.
     * Customer sees this list when browsing what to order.
     */
    public static List<TestType> getAllTests() {
        List<TestType> list = new ArrayList<>();
        String sql = "SELECT id, name, price, tat_hours, result_format FROM tests ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TestType t = new TestType();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setPrice(rs.getDouble("price"));
                t.setTatHours(rs.getInt("tat_hours"));
                t.setResultFormat(rs.getString("result_format"));
                list.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading tests: " + e.getMessage(), e);
        }

        return list;
    }
}
