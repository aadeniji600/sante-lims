package com.lims.controller.customer;

import com.lims.util.AppConstants;
import com.lims.util.SceneManager;
import com.lims.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * CustomerDashboardController.java
 *
 * Currently a placeholder — Member 3 will build the full customer
 * dashboard here including:
 *   - Browse and order tests
 *   - View current/past results with TAT countdown
 *   - Download results from the Result Vault
 *
 * The logout method and userLabel are required by CustomerDashboard.fxml
 * and must stay here even when Member 3 expands this controller.
 */
public class CustomerDashboardController {

    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        // Show the logged-in customer's name in the header
        if (userLabel != null && SessionManager.getCurrentUser() != null) {
            userLabel.setText("Welcome, " + SessionManager.getCurrentUser().getName());
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        SessionManager.clearSession();
        SceneManager.switchTo(event, AppConstants.FXML_LOGIN);
    }
}
