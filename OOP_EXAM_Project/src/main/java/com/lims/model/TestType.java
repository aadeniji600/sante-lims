package com.lims.model;

import java.time.LocalDateTime;

/**
 * TestType.java
 * Represents one row from the test_types table.
 * A TestType is a lab test that the Super Admin defines —
 * e.g. "Blood Count", price: 5000, TAT: 24 hours, format: PDF
 */
public class TestType {

    private int id;
    private String name;
    private double price;
    private int tatHours;         // TAT = Turnaround Time (how long the test takes)
    private String resultFormat;  // use AppConstants.FORMAT_* values
    private int createdBy;        // the user ID of the Super Admin who created it
    private LocalDateTime createdAt;

    // Empty constructor
    public TestType() {}

    // Full constructor
    public TestType(int id, String name, double price, int tatHours,
                    String resultFormat, int createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.tatHours = tatHours;
        this.resultFormat = resultFormat;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getTatHours() { return tatHours; }
    public void setTatHours(int tatHours) { this.tatHours = tatHours; }

    public String getResultFormat() { return resultFormat; }
    public void setResultFormat(String resultFormat) { this.resultFormat = resultFormat; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

