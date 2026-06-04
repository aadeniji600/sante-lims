package com.lims.controller.auth;

import com.lims.model.User;
import com.lims.service.AuthService;
import com.lims.util.AppConstants;
import com.lims.util.SceneManager;
import com.lims.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * LoginController.java
 *
 * PURPOSE: Handles all user interaction on the Login screen.
 * The @FXML annotation connects Java fields to elements defined in Login.fxml.
 *
 * FLOW:
 * 1. User types email + password and clicks "Log In"
 * 2. handleLogin() is called
 * 3. AuthService checks credentials
 * 4. On success: save user in SessionManager, navigate to their dashboard
 * 5. On failure: show error message in errorLabel
 */
public class LoginController {

    // @FXML fields are automatically populated by JavaFX from Login.fxml
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // Service that handles the actual login logic
    private final AuthService authService = new AuthService();

    /**
     * Called when the user clicks "Log In".
     * The method name must match the onAction="#handleLogin" in the FXML.
     */
    @FXML
    private void handleLogin(ActionEvent event) {

        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        // Basic input validation before even hitting the database
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter your email and password.");
            return;
        }

        try {
            // Attempt login — throws IllegalArgumentException on failure
            User user = authService.login(email, password);

            // Save the logged-in user so other screens can access them
            SessionManager.setCurrentUser(user);

            // If this is their first login, force a password change
            if (user.isFirstLogin()) {
                SceneManager.switchTo(event, AppConstants.FXML_FORCE_PASSWORD);
                return;
            }

            // Navigate to the correct dashboard based on role
            switch (user.getRole()) {
                case AppConstants.ROLE_SUPER_ADMIN ->
                    SceneManager.switchTo(event, AppConstants.FXML_ADMIN_DASHBOARD);
                case AppConstants.ROLE_LAB_ATTENDANT ->
                    SceneManager.switchTo(event, AppConstants.FXML_ATTENDANT_DASHBOARD);
                case AppConstants.ROLE_CUSTOMER ->
                    SceneManager.switchTo(event, AppConstants.FXML_CUSTOMER_DASHBOARD);
            }

        } catch (IllegalArgumentException e) {
            // Show the error message (e.g. "Incorrect password")
            showError(e.getMessage());
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        SceneManager.switchTo(event, AppConstants.FXML_REGISTER);
    }

    // Helper to show the error label with a message
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
