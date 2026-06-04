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
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource(fxmlPath)
            );

            Scene scene = new Scene(loader.load(), AppConstants.APP_WIDTH, AppConstants.APP_HEIGHT);

            // Get the current window from the event source and swap the scene
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Could not load screen: " + fxmlPath, e);
        }
    }
}
