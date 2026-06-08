package com.lims.model;

import java.time.LocalDateTime;

/**
 * LabResult.java
 * Represents a result uploaded by the Lab Attendant.
 * validated = false means NOT yet visible to the customer.
 * This is a display model — it joins result + request + user + test for easy table display.
 */
public class LabResult {

    private int id;
    private int requestId;
    private String customerName;
    private String testName;
    private String filePath;
    private String resultText;
    private boolean validated;
    private String uploadedAt; // pre-formatted string for display in tables

    public LabResult() {}

    public LabResult(int id, int requestId, String customerName, String testName,
                     String filePath, String resultText, boolean validated, String uploadedAt) {
        this.id = id;
        this.requestId = requestId;
        this.customerName = customerName;
        this.testName = testName;
        this.filePath = filePath;
        this.resultText = resultText;
        this.validated = validated;
        this.uploadedAt = uploadedAt;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public int getRequestId()                    { return requestId; }
    public void setRequestId(int requestId)      { this.requestId = requestId; }

    public String getCustomerName()              { return customerName; }
    public void setCustomerName(String n)        { this.customerName = n; }

    public String getTestName()                  { return testName; }
    public void setTestName(String n)            { this.testName = n; }

    public String getFilePath()                  { return filePath; }
    public void setFilePath(String filePath)     { this.filePath = filePath; }

    public String getResultText()                { return resultText; }
    public void setResultText(String resultText) { this.resultText = resultText; }

    public boolean isValidated()                 { return validated; }
    public void setValidated(boolean validated)  { this.validated = validated; }

    // Used by TableView column to show readable status text
    public String getValidated() { return validated ? "RELEASED" : "PENDING"; }

    public String getUploadedAt()                { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
