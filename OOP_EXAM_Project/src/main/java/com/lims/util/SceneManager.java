package com.lims.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SceneManager.java
 *
 * PURPOSE: Switches the current screen to a different FXML screen.
 *
 * HOW TO USE (for Members 2 and 3 — switching screens after an action):
 *
 *   // After login succeeds, navigate to the admin dashboard:
 *   SceneManager.switchTo(event, AppConstants.FXML_ADMIN_DASHBOARD);
 *
 *   // The 'event' parameter comes from the button's onAction method — pass it in.
 */
public class SceneManager {

public static void switchTo(javafx.event.ActionEvent event, String fxmlPath) {
        try {
            // 1. Get the current active window (Stage)
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // 2. Record the exact current dimensions and maximized state
            boolean isMaximized = stage.isMaximized();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            // 3. Load the new FXML file
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);

            // 4. Force the window to retain its exact previous state
            if (isMaximized) {
                stage.setMaximized(true);
            } else {
                // Only set width/height if it's not maximized, otherwise it glitches
                if (!Double.isNaN(currentWidth) && !Double.isNaN(currentHeight)) {
                    stage.setWidth(currentWidth);
                    stage.setHeight(currentHeight);
                }
            }

            stage.show();
        } catch (Exception e) {
            System.err.println("Error switching to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
