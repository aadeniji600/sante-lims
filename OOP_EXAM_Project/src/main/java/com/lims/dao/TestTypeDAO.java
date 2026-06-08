package com.lims.dao;

import com.lims.model.TestType;
import com.lims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TestTypeDAO {
    
    public static List<TestType> getAllTests() {
        List<TestType> tests = new ArrayList<>();
        String query = "SELECT * FROM test_types"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                TestType test = new TestType();
                test.setId(rs.getInt("id"));
                test.setName(rs.getString("name")); 
                test.setPrice(rs.getDouble("price"));
                test.setTatHours(rs.getInt("tat_hours")); 
                tests.add(test);
            }
        } catch (Exception e) {
            System.err.println("Error fetching tests: " + e.getMessage());
            e.printStackTrace();
        }
        return tests;
    }
}