package com.lims.controller.auth;

import com.lims.dao.AuditLogDAO;
import com.lims.dao.UserDAO;
import com.lims.model.User;
import com.lims.service.AuthService;
import com.lims.util.AppConstants;
import com.lims.util.SceneManager;
import com.lims.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * ForcePasswordController.java
 *
 * PURPOSE: Forces the user to set a new password on their first login.
 *
 * WHO SEES THIS SCREEN:
 * - The default Super Admin (Admin@123 must be changed immediately)
 * - Any Lab Attendant or Super Admin account created by the Super Admin
 *   (they are given a temporary password and must change it on first login)
 *
 * FLOW:
 * 1. User types new password + confirmation
 * 2. We validate (not empty, both match, min length)
 * 3. Hash the new password with BCrypt
 * 4. Update in database + set is_first_login = FALSE
 * 5. Navigate to their correct dashboard
 */
public class ForcePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO         = new UserDAO();
    private final AuthService authService = new AuthService();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @FXML
    private void handleSavePassword(ActionEvent event) {

        String newPassword     = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in both fields.");
            return;
        }

        if (newPassword.length() < 8) {
            showError("Password must be at least 8 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Get the currently logged in user from SessionManager
        User currentUser = SessionManager.getCurrentUser();

        // Hash the new password and update it in the database
        String newHash = authService.hashPassword(newPassword);
        userDAO.updatePassword(currentUser.getId(), newHash);

        // Update the session object too so the rest of the app sees the change
        currentUser.setPasswordHash(newHash);
        currentUser.setFirstLogin(false);
        SessionManager.setCurrentUser(currentUser);

        // Log this action in the audit trail
        auditLogDAO.log(
            currentUser.getId(),
            "User changed password on first login",
            "users",
            currentUser.getId()
        );

        // Navigate to the correct dashboard based on their role
        switch (currentUser.getRole()) {
            case AppConstants.ROLE_SUPER_ADMIN ->
                SceneManager.switchTo(event, AppConstants.FXML_ADMIN_DASHBOARD);
            case AppConstants.ROLE_LAB_ATTENDANT ->
                SceneManager.switchTo(event, AppConstants.FXML_ATTENDANT_DASHBOARD);
            case AppConstants.ROLE_CUSTOMER ->
                SceneManager.switchTo(event, AppConstants.FXML_CUSTOMER_DASHBOARD);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
