package com.lims.controller.customer;

import com.lims.model.TestType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class TestBrowserController implements Initializable {

    @FXML private TableView<TestType> testTable;
    @FXML private TableColumn<TestType, String> nameCol;
    @FXML private TableColumn<TestType, Double> priceCol;
    @FXML private TableColumn<TestType, Integer> tatCol;
    @FXML private Button orderButton;

    private ObservableList<TestType> testList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map columns to the TestType class fields
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // FIXED: Using "tatHours" to match the property in TestType.java
        tatCol.setCellValueFactory(new PropertyValueFactory<>("tatHours"));

        // 2. Load data from the database
        loadAvailableTests();
    }

    private void loadAvailableTests() {
        testList.clear(); // Clear any old data
        // Fetch fresh data from the database via the DAO
        testList.addAll(com.lims.dao.TestTypeDAO.getAllTests());
        // Bind the data to the UI table
        testTable.setItems(testList);
    }

    @FXML
    private void handlePlaceOrder() {
        TestType selectedTest = testTable.getSelectionModel().getSelectedItem();
        
        if (selectedTest == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a test from the table to place an order.");
            return;
        }

        // 1. Get the currently logged-in Customer from the SessionManager
        com.lims.model.User currentUser = com.lims.util.SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Session Error", "You must be logged in to place an order.");
            return;
        }

        // 2. Insert the order into the database
        // FIXED: Using getTatHours() instead of getStandardTurnaroundTime()
        boolean success = com.lims.dao.TestRequestDAO.createOrder(
            currentUser.getId(), 
            selectedTest.getId(), 
            selectedTest.getTatHours() 
        );

        if (success) {
            // 3. System Integration: Use Member 1's AuditLogDAO to maintain the immutable log
            com.lims.dao.AuditLogDAO auditLog = new com.lims.dao.AuditLogDAO();
            String logMessage = "Customer ordered " + selectedTest.getName() + " (Unpaid)";
            auditLog.log(currentUser.getId(), logMessage, "test_requests", currentUser.getId());

            // 4. Requirement 2.4: Display Sante Diagnostics bank account details
            // FIXED: Using getTatHours() here as well
            String bankDetails = "Order placed successfully!\n\n" +
                                 "Total Due: ₦" + String.format("%.2f", selectedTest.getPrice()) + "\n" +
                                 "Bank: GTBank\n" +
                                 "Account Name: Sante Diagnostics Ltd\n" +
                                 "Account Number: 0123456789\n\n" +
                                 "Please transfer the exact amount. Results will be ready in " + 
                                 selectedTest.getTatHours() + " hours after payment validation.";
                                 
            showAlert(Alert.AlertType.INFORMATION, "Payment Instructions", bankDetails);
        } else {
            showAlert(Alert.AlertType.ERROR, "System Error", "Failed to process your order. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // ==========================================
    // NAVIGATION ROUTING
    // ==========================================
    @FXML
    private void goToDashboard(javafx.event.ActionEvent event) {
        com.lims.util.SceneManager.switchTo(event, "/fxml/CustomerDashboard.fxml");
    }

    @FXML
    private void goToTestBrowser(javafx.event.ActionEvent event) {
        com.lims.util.SceneManager.switchTo(event, "/fxml/TestBrowser.fxml");
    }

    @FXML
    private void goToResultVault(javafx.event.ActionEvent event) {
        com.lims.util.SceneManager.switchTo(event, "/fxml/ResultVault.fxml");
    }
    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        // 1. Clear the user session
        com.lims.util.SessionManager.setCurrentUser(null);
        
        // 2. Route back to the Login Screen
        com.lims.util.SceneManager.switchTo(event, com.lims.util.AppConstants.FXML_LOGIN);
    }
}

