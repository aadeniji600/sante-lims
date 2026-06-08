package com.lims.util;

public class AppConstants {

    public static final String ROLE_SUPER_ADMIN    = "SUPER_ADMIN";
    public static final String ROLE_LAB_ATTENDANT  = "LAB_ATTENDANT";
    public static final String ROLE_CUSTOMER       = "CUSTOMER";

    public static final String STATUS_PAID   = "PAID";
    public static final String STATUS_UNPAID = "UNPAID";

    public static final String SAMPLE_COLLECTED   = "COLLECTED";
    public static final String SAMPLE_PROCESSING  = "PROCESSING";
    public static final String SAMPLE_VALIDATED   = "VALIDATED";

    public static final String FORMAT_NUMERIC = "NUMERIC";
    public static final String FORMAT_TEXT    = "TEXT";
    public static final String FORMAT_PDF     = "PDF";
    public static final String FORMAT_IMAGE   = "IMAGE";

    public static final String FXML_LOGIN             = "/fxml/Login.fxml";
    public static final String FXML_REGISTER          = "/fxml/Register.fxml";
    public static final String FXML_FORCE_PASSWORD    = "/fxml/ForcePassword.fxml";
    public static final String FXML_ADMIN_DASHBOARD   = "/fxml/AdminDashboard.fxml";
    public static final String FXML_ATTENDANT_DASHBOARD = "/fxml/AttendantDashboard.fxml";
    public static final String FXML_CUSTOMER_DASHBOARD = "/fxml/CustomerDashboard.fxml";

    public static final String APP_TITLE = "Santé Diagnostics LIMS";
    public static final int    APP_WIDTH  = 1200;
    public static final int    APP_HEIGHT = 750;
    
    public static final String FXML_SUPER_ADMIN_HOME   = "/fxml/SuperAdminHome.fxml";
    public static final String FXML_ATTENDANT_HOME     = "/fxml/LabAttendantHome.fxml";
}
