package com.lims.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection.java
 *
 * PURPOSE: Provides a single shared method for getting a database connection.
 * It reads settings from db.properties so we never hardcode passwords in code.
 *
 * HOW TO USE (for Members 2 and 3):
 *   Connection conn = DBConnection.getConnection();
 *   // ... do your SQL queries ...
 *   conn.close(); // always close when done!
 *
 * The 'static' keyword on getConnection() means you call it on the class
 * itself, not on an object — no need to do "new DBConnection()".
 */
public class DBConnection {

    // 'private' so no one accidentally creates a DBConnection object
    private DBConnection() {}

    /**
     * Opens and returns a new connection to the PostgreSQL database.
     * Throws a RuntimeException if the connection fails, which will
     * crash the app with a clear error message rather than silently fail.
     */
    public static Connection getConnection() {
        try {
            // Load the properties file from the resources folder
            Properties props = new Properties();
            InputStream input = DBConnection.class
                    .getResourceAsStream("/db.properties");

            if (input == null) {
                throw new RuntimeException("Cannot find db.properties file!");
            }

            props.load(input);

            String url      = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            // DriverManager.getConnection() is the standard Java way
            // to open a database connection
            return DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            // Wrap in RuntimeException so callers don't need to handle it
            // everywhere — the app will crash loudly if DB is unreachable
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }
}
