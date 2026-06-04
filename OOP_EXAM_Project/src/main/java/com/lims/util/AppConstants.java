package com.lims.util;

/**
 * AppConstants.java
 *
 * PURPOSE: This file holds all fixed string and number values used across
 * the entire project. Instead of writing "PAID" or "SUPER_ADMIN" directly
 * in your code (which is error-prone), always use these constants.
 *
 * HOW TO USE (for Members 2 and 3):
 *   Instead of:  if (status.equals("paid"))
 *   Write:       if (status.equals(AppConstants.STATUS_PAID))
 *
 * This way if we ever rename a value, we only change it in one place.
 */
public class AppConstants {

    // ---------------------------------------------------------------
    // USER ROLES
    // These match exactly what is stored in the 'role' column in the
    // users table of the database.
    // ---------------------------------------------------------------
    public static final String ROLE_SUPER_ADMIN    = "SUPER_ADMIN";
    public static final String ROLE_LAB_ATTENDANT  = "LAB_ATTENDANT";
    public static final String ROLE_CUSTOMER       = "CUSTOMER";

    // ---------------------------------------------------------------
    // PAYMENT STATUS
    // Used in the test_requests table and in the UI queues
    // ---------------------------------------------------------------
    public static final String STATUS_PAID   = "PAID";
    public static final String STATUS_UNPAID = "UNPAID";

    // ---------------------------------------------------------------
    // SAMPLE LIFECYCLE STAGES
    // Lab Attendant uses these to track where a sample is in the process
    // ---------------------------------------------------------------
    public static final String SAMPLE_COLLECTED   = "COLLECTED";
    public static final String SAMPLE_PROCESSING  = "PROCESSING";
    public static final String SAMPLE_VALIDATED   = "VALIDATED";

    // ---------------------------------------------------------------
    // RESULT FORMATS
    // Defined by Super Admin when creating a test type
    // ---------------------------------------------------------------
    public static final String FORMAT_NUMERIC = "NUMERIC";
    public static final String FORMAT_TEXT    = "TEXT";
    public static final String FORMAT_PDF     = "PDF";
    public static final String FORMAT_IMAGE   = "IMAGE";

    // ---------------------------------------------------------------
    // FXML SCREEN PATHS
    // Every screen file has its path registered here.
    // Members 2 and 3: add your FXML paths here when you create them.
    // ---------------------------------------------------------------
    public static final String FXML_LOGIN             = "/fxml/Login.fxml";
    public static final String FXML_REGISTER          = "/fxml/Register.fxml";
    public static final String FXML_FORCE_PASSWORD    = "/fxml/ForcePassword.fxml";
    public static final String FXML_ADMIN_DASHBOARD   = "/fxml/AdminDashboard.fxml";
    public static final String FXML_ATTENDANT_DASHBOARD = "/fxml/AttendantDashboard.fxml";
    public static final String FXML_CUSTOMER_DASHBOARD = "/fxml/CustomerDashboard.fxml";

    // ---------------------------------------------------------------
    // APP CONFIG
    // ---------------------------------------------------------------
    public static final String APP_TITLE = "Santé Diagnostics LIMS";
    public static final int    APP_WIDTH  = 1200;
    public static final int    APP_HEIGHT = 750;
}
