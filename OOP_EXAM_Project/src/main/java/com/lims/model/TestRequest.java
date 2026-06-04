package com.lims.model;

import java.time.LocalDateTime;

/**
 * TestRequest.java
 * Represents one row from the test_requests table.
 * Created when a Customer places an order for a test.
 * Tracks who ordered it, what test, and whether they have paid.
 */
public class TestRequest {

    private int id;
    private int customerId;       // ID of the Customer who placed the order
    private int testTypeId;       // ID of the TestType they ordered
    private String paymentStatus; // use AppConstants.STATUS_PAID or STATUS_UNPAID
    private LocalDateTime requestedAt;
    private LocalDateTime expectedReadyAt; // calculated from TAT when order is placed

    // Empty constructor
    public TestRequest() {}

    // Full constructor
    public TestRequest(int id, int customerId, int testTypeId, String paymentStatus,
                       LocalDateTime requestedAt, LocalDateTime expectedReadyAt) {
        this.id = id;
        this.customerId = customerId;
        this.testTypeId = testTypeId;
        this.paymentStatus = paymentStatus;
        this.requestedAt = requestedAt;
        this.expectedReadyAt = expectedReadyAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getTestTypeId() { return testTypeId; }
    public void setTestTypeId(int testTypeId) { this.testTypeId = testTypeId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getExpectedReadyAt() { return expectedReadyAt; }
    public void setExpectedReadyAt(LocalDateTime expectedReadyAt) { this.expectedReadyAt = expectedReadyAt; }
}
