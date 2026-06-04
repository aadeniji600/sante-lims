package com.lims.controller.auth;

import com.lims.dao.UserDAO;
import com.lims.model.User;
import com.lims.service.AuthService;
import com.lims.service.EmailService;
import com.lims.util.AppConstants;
import com.lims.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Random;

/**
 * RegisterController.java
 *
 * PURPOSE: Handles customer self-registration.
 *
 * FLOW:
 * 1. Customer fills in name, email, password, confirm password
 * 2. We validate the input (no empty fields, passwords match)
 * 3. Check the email isn't already registered
 * 4. Hash the password with BCrypt
 * 5. Save the new user to the database (is_verified = FALSE)
 * 6. Generate a 6-digit verification code and email it to them
 * 7. Show success message telling them to check their email
 */
public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private final UserDAO userDAO       = new UserDAO();
    private final AuthService authService = new AuthService();
    private final EmailService emailService = new EmailService();

    @FXML
    private void handleRegister(ActionEvent event) {

        String name            = nameField.getText().trim();
        String email           = emailField.getText().trim();
        String password        = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // --- Input Validation ---
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // --- Check if email is already registered ---
        if (userDAO.findByEmail(email) != null) {
            showError("An account with this email already exists.");
            return;
        }

        // --- Create and save the new user ---
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPasswordHash(authService.hashPassword(password));
        newUser.setRole(AppConstants.ROLE_CUSTOMER);
        newUser.setFirstLogin(false);   // customers set their own password on register
        newUser.setVerified(false);     // must verify email before logging in

        userDAO.save(newUser);

        // --- Generate a 6-digit verification code and email it ---
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        // Save the code — for simplicity we store it in the DB as a temp password update
        // In a full system this would go in a separate verification_tokens table
        userDAO.saveVerificationCode(email, verificationCode);

        // Send the verification email
        emailService.sendVerificationEmail(email, name, verificationCode);

        // --- Show success message ---
        hideError();
        showSuccess("Account created! Check your email for a 6-digit verification code, " +
                    "then use it to verify your account before logging in.");
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        SceneManager.switchTo(event, AppConstants.FXML_LOGIN);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }
}
