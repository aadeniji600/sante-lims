package com.lims.model;

import java.time.LocalDateTime;

/**
 * Sample.java
 * Represents one row from the samples table.
 * A Sample is the physical specimen collected from the customer.
 * The Lab Attendant updates its status as it moves through the lab.
 *
 * Status flow:  COLLECTED → PROCESSING → VALIDATED
 * Use AppConstants.SAMPLE_* values for the status field.
 */
public class Sample {

    private int id;
    private int requestId;        // which TestRequest this sample belongs to
    private String status;        // COLLECTED, PROCESSING, or VALIDATED
    private LocalDateTime collectedAt;
    private LocalDateTime processedAt;  // set when status changes to PROCESSING
    private LocalDateTime validatedAt;  // set when status changes to VALIDATED

    // Empty constructor
    public Sample() {}

    // Full constructor
    public Sample(int id, int requestId, String status, LocalDateTime collectedAt,
                  LocalDateTime processedAt, LocalDateTime validatedAt) {
        this.id = id;
        this.requestId = requestId;
        this.status = status;
        this.collectedAt = collectedAt;
        this.processedAt = processedAt;
        this.validatedAt = validatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
}
