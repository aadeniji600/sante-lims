package com.lims.dao;

import com.lims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    /**
     * DTO: A lightweight nested class tailored specifically for the Result Vault UI.
     * Contains only what the patient needs to see.
     */
    public static class CustomerResultDTO {
        private int resultId;
        private String testName;
        private LocalDateTime validatedAt;
        private String fileUrl;
        
        // Getters required for JavaFX PropertyValueFactory
        public int getResultId() { return resultId; }
        public String getTestName() { return testName; }
        public LocalDateTime getValidatedAt() { return validatedAt; }
        public String getFileUrl() { return fileUrl; }
    }

    /**
     * Executes a 4-table SQL JOIN to find validated results for a specific customer.
     */
    public static List<CustomerResultDTO> getValidatedResultsForCustomer(int customerId) {
        List<CustomerResultDTO> results = new ArrayList<>();
        
        String query = "SELECT r.id, t.name AS test_name, r.validated_at, r.file_url " +
                       "FROM results r " +
                       "JOIN samples s ON r.sample_id = s.id " +
                       "JOIN test_requests tr ON s.request_id = tr.id " +
                       "JOIN test_types t ON tr.test_type_id = t.id " +
                       "WHERE tr.customer_id = ? AND r.is_validated = TRUE " +
                       "ORDER BY r.validated_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CustomerResultDTO dto = new CustomerResultDTO();
                    dto.resultId = rs.getInt("id");
                    dto.testName = rs.getString("test_name");
                    
                    java.sql.Timestamp ts = rs.getTimestamp("validated_at");
                    if (ts != null) {
                        dto.validatedAt = ts.toLocalDateTime();
                    }
                    
                    dto.fileUrl = rs.getString("file_url");
                    results.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer results: " + e.getMessage());
        }
        return results;
    }
}