package com.lims.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

 
public class DBConnection {

    private DBConnection() {}
    
    public static Connection getConnection() {
        try {
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

            return DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }
}
