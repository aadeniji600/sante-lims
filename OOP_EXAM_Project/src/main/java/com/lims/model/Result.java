package com.lims.model;

import java.time.LocalDateTime;

/**
 * Result.java
 * Represents one row from the results table.
 * A Result is uploaded by the Lab Attendant after processing a sample.
 *
 * IMPORTANT: isValidated must be TRUE before the customer can see their result.
 * The Lab Attendant manually validates it as a quality check before release.
 */
public class Result {

    private int id;
    private int sampleId;         // which sample this result belongs to
    private String fileUrl;       // file path to the PDF or image (if applicable)
    private String resultText;    // for NUMERIC or TEXT format results
    private boolean isValidated;  // FALSE until Lab Attendant approves it
    private int validatedBy;      // user ID of the Lab Attendant who validated it
    private LocalDateTime validatedAt;
    private LocalDateTime uploadedAt;

    // Empty constructor
    public Result() {}

    // Full constructor
    public Result(int id, int sampleId, String fileUrl, String resultText,
                  boolean isValidated, int validatedBy,
                  LocalDateTime validatedAt, LocalDateTime uploadedAt) {
        this.id = id;
        this.sampleId = sampleId;
        this.fileUrl = fileUrl;
        this.resultText = resultText;
        this.isValidated = isValidated;
        this.validatedBy = validatedBy;
        this.validatedAt = validatedAt;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSampleId() { return sampleId; }
    public void setSampleId(int sampleId) { this.sampleId = sampleId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getResultText() { return resultText; }
    public void setResultText(String resultText) { this.resultText = resultText; }

    public boolean isValidated() { return isValidated; }
    public void setValidated(boolean validated) { isValidated = validated; }

    public int getValidatedBy() { return validatedBy; }
    public void setValidatedBy(int validatedBy) { this.validatedBy = validatedBy; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
