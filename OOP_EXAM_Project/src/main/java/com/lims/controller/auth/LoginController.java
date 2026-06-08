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
    // === TEMPORARY HASH GENERATOR ===

        // ================================

    /**
     * Called when the user clicks "Log In".
     * The method name must match the onAction="#handleLogin" in the FXML.
     */
@FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        // === TEMPORARY HASH GENERATOR ===
        // This is safely INSIDE the method, right after we grab the password
        if (password.equals("password123")) {
            System.out.println("NATIVE BCRYPT HASH: " + authService.hashPassword(password));
        }
        // ================================

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter your email and password.");
            return;
        }

        try {
            User user = authService.login(email, password);
            com.lims.util.SessionManager.setCurrentUser(user);

            switch (user.getRole()) {
                case com.lims.util.AppConstants.ROLE_SUPER_ADMIN ->
                    com.lims.util.SceneManager.switchTo(event, com.lims.util.AppConstants.FXML_ADMIN_DASHBOARD);
                case com.lims.util.AppConstants.ROLE_LAB_ATTENDANT ->
                    com.lims.util.SceneManager.switchTo(event, com.lims.util.AppConstants.FXML_ATTENDANT_DASHBOARD);
                case com.lims.util.AppConstants.ROLE_CUSTOMER ->
                    com.lims.util.SceneManager.switchTo(event, "/fxml/CustomerDashboard.fxml");
            }

        } catch (IllegalArgumentException e) {
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
