package com.lims;

import com.lims.util.AppConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApp.java
 *
 * PURPOSE: This is where the JavaFX application starts.
 * It loads the Login screen and opens the main window.
 *
 * JavaFX requires you to extend Application and implement start().
 * The launch() call in main() is what kicks everything off.
 *
 * Members 2 and 3 don't need to touch this file.
 * Screen navigation is handled by SceneManager (below).
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the Login screen FXML file
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(AppConstants.FXML_LOGIN)
        );

        // Create the scene with the size defined in AppConstants
        Scene scene = new Scene(loader.load(), AppConstants.APP_WIDTH, AppConstants.APP_HEIGHT);

        primaryStage.setTitle(AppConstants.APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // this calls start() above
    }
}
