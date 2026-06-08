package com.lims.controller.customer;

import com.lims.model.TestRequest;
import com.lims.model.User;
import com.lims.util.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class CustomerDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label tatTimerLabel;
    @FXML private TableView<TestRequest> ordersTable;
    @FXML private TableColumn<TestRequest, Integer> orderIdCol;
    @FXML private TableColumn<TestRequest, String> statusCol;
    @FXML private TableColumn<TestRequest, LocalDateTime> readyAtCol;

    private Timeline countdownTimeline;
    private ObservableList<TestRequest> orderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
            loadUserData(currentUser.getId());
        }

        // Map columns
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        readyAtCol.setCellValueFactory(new PropertyValueFactory<>("expectedReadyAt"));

        // Format the Expected Ready Time column to look human-readable
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        readyAtCol.setCellFactory(column -> {
            return new TableCell<TestRequest, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });
    }

    private void loadUserData(int customerId) {
        orderList.clear();
        orderList.addAll(com.lims.dao.TestRequestDAO.getCustomerOrders(customerId));
        ordersTable.setItems(orderList);
    }

    @FXML
    private void handleOrderSelection() {
        TestRequest selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null && selectedOrder.getExpectedReadyAt() != null) {
            startTimer(selectedOrder.getExpectedReadyAt());
        }
    }

    private void startTimer(LocalDateTime expectedTime) {
        if (countdownTimeline != null) {
            countdownTimeline.stop(); // Stop any previous timer
        }

        // Update the UI every 1 second
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(expectedTime) || now.isEqual(expectedTime)) {
                tatTimerLabel.setText("Result Ready for Validation!");
                tatTimerLabel.setStyle("-fx-text-fill: #27ae60;"); // Green
                countdownTimeline.stop();
                return;
            }

            long hours = ChronoUnit.HOURS.between(now, expectedTime);
            long minutes = ChronoUnit.MINUTES.between(now, expectedTime) % 60;
            long seconds = ChronoUnit.SECONDS.between(now, expectedTime) % 60;

            // Updated Timer Format String
            tatTimerLabel.setText(String.format("Time Remaining: %dhr %dmin %dsec", hours, minutes, seconds));
            tatTimerLabel.setStyle("-fx-text-fill: #e67e22;"); // Orange
        }));
        
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
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
