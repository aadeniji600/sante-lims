package com.lims.controller.customer;

import com.lims.dao.ResultDAO.CustomerResultDTO;
import com.lims.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ResultVaultController implements Initializable {

    @FXML private TableView<CustomerResultDTO> resultTable;
    @FXML private TableColumn<CustomerResultDTO, String> testNameCol;
    @FXML private TableColumn<CustomerResultDTO, LocalDateTime> dateCol;

    private ObservableList<CustomerResultDTO> resultList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map columns to the DTO properties
        testNameCol.setCellValueFactory(new PropertyValueFactory<>("testName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("validatedAt"));

        // 2. Format the Date Column
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        dateCol.setCellFactory(column -> {
            return new TableCell<CustomerResultDTO, LocalDateTime>() {
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

        // 3. Load the data
        loadVaultData();
    }

    private void loadVaultData() {
        resultList.clear();
        com.lims.model.User currentUser = SessionManager.getCurrentUser();
        
        if (currentUser != null) {
            resultList.addAll(com.lims.dao.ResultDAO.getValidatedResultsForCustomer(currentUser.getId()));
        }
        resultTable.setItems(resultList);
    }

    @FXML
    private void handleViewDocument(ActionEvent event) {
        CustomerResultDTO selectedResult = resultTable.getSelectionModel().getSelectedItem();

        if (selectedResult == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a medical report from the vault to view.");
            return;
        }

        String filePath = selectedResult.getFileUrl();
        if (filePath == null || filePath.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "File Missing", "The secure file path for this result is empty or corrupted.");
            return;
        }

        try {
            // System.getProperty("user.dir") dynamically grabs the root folder of whoever is running the app
            File document = new File(System.getProperty("user.dir"), filePath);
            
            if (document.exists()) {
                // The Native OS Bridge: Tells Windows/Mac to open the file with the default viewer
                Desktop.getDesktop().open(document);
            } else {
                showAlert(Alert.AlertType.ERROR, "File Not Found", "The physical document could not be located on the server: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Failed to open the document: " + e.getMessage());
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


