package com.lims.controller.admin;

import com.lims.util.AppConstants;
import com.lims.util.SceneManager;
import com.lims.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class AdminDashboardController {

    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        if (userLabel != null && SessionManager.getCurrentUser() != null) {
            userLabel.setText(SessionManager.getCurrentUser().getName()
                    + "  (" + SessionManager.getCurrentUser().getRole() + ")");
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        SessionManager.clearSession();
        SceneManager.switchTo(event, AppConstants.FXML_LOGIN);
    }
}