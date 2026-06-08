package com.lims.dao;

import com.lims.model.TestType;
import com.lims.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TestDAO.java
 * Handles all database operations for the tests table.
 * Used by Member 2's TestBuilderController to create/edit/delete test types.
 */
public class TestDAO {

    public List<TestType> findAll() throws SQLException {
        List<TestType> list = new ArrayList<>();
        String sql = "SELECT id, name, price, tat_hours, result_format FROM tests ORDER BY id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
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
        }
        return list;
    }

    public void insert(String name, double price, int tatHours, String format) throws SQLException {
        String sql = "INSERT INTO tests (name, price, tat_hours, result_format) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, tatHours);
            ps.setString(4, format);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "TEST_CREATED: " + name, "tests", 0);
    }

    public void update(int id, String name, double price, int tatHours, String format) throws SQLException {
        String sql = "UPDATE tests SET name=?, price=?, tat_hours=?, result_format=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, tatHours);
            ps.setString(4, format);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "TEST_UPDATED: #" + id, "tests", id);
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tests WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        new AuditLogDAO().log(0, "TEST_DELETED: #" + id, "tests", id);
    }
}
