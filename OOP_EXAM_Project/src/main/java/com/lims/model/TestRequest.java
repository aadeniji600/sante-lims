package com.lims.model;

import java.time.LocalDateTime;

/**
 * TestRequest.java
 * Represents one row from the test_requests table.
 * Created when a Customer places an order for a test.
 * Tracks who ordered it, what test, and whether they have paid.
 *
 * Has TWO constructors:
 * 1. The original constructor — used when saving/loading from DB with LocalDateTime
 * 2. The display constructor — used by RequestDAO when joining with users+tests tables
 *    for showing in Member 2's table views (includes customerName, testName as strings)
 */
public class TestRequest {

    private int id;
    private int customerId;
    private int testTypeId;
    private String paymentStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime expectedReadyAt;
    private String customerName;
    private String testName;
    private double price;
    private String requestedAtDisplay; // formatted string version for table display

    // Empty constructor
    public TestRequest() {}

    // Original constructor — used when working with LocalDateTime dates
    public TestRequest(int id, int customerId, int testTypeId, String paymentStatus,
                       LocalDateTime requestedAt, LocalDateTime expectedReadyAt) {
        this.id = id;
        this.customerId = customerId;
        this.testTypeId = testTypeId;
        this.paymentStatus = paymentStatus;
        this.requestedAt = requestedAt;
        this.expectedReadyAt = expectedReadyAt;
    }

    // Display constructor — used by RequestDAO for Member 2's table views
    // Includes customerName and testName from JOIN queries
    public TestRequest(int id, int customerId, String customerName, int testTypeId,
                       String testName, double price, String paymentStatus, String requestedAtDisplay) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.testTypeId = testTypeId;
        this.testName = testName;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.requestedAtDisplay = requestedAtDisplay;
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

    // Single getRequestedAt() — returns display string if available, otherwise converts LocalDateTime
    public String getRequestedAt() {
        if (requestedAtDisplay != null) return requestedAtDisplay;
        if (requestedAt != null) return requestedAt.toString();
        return "";
    }

    // Setter still accepts LocalDateTime for the original save/load flow
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getExpectedReadyAt() { return expectedReadyAt; }
    public void setExpectedReadyAt(LocalDateTime expectedReadyAt) { this.expectedReadyAt = expectedReadyAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "#" + id + " - " + customerName + " - " + testName;
    }
}