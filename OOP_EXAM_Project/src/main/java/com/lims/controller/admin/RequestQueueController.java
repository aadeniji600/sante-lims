package com.lims.controller.admin; // change to com.lims.controller.attendant for the second file

import com.lims.dao.RequestDAO;
import com.lims.model.TestRequest;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/** Test Request Queue — shared by Super Admin and Lab Attendant. */
public class RequestQueueController {

    @FXML private TableView<TestRequest> table;
    @FXML private TableColumn<TestRequest, Integer> colId;
    @FXML private TableColumn<TestRequest, String>  colCustomer;
    @FXML private TableColumn<TestRequest, String>  colTest;
    @FXML private TableColumn<TestRequest, Double>  colPrice;
    @FXML private TableColumn<TestRequest, String>  colPayment;
    @FXML private TableColumn<TestRequest, String>  colRequested;
    @FXML private Button markPaidButton;

    private final RequestDAO dao = new RequestDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTest.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colRequested.setCellValueFactory(new PropertyValueFactory<>("requestedAt"));

        colPayment.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("PAID".equals(item)
                            ? "-fx-text-fill: #1a7f37; -fx-font-weight: bold;"
                            : "-fx-text-fill: #b3261e; -fx-font-weight: bold;");
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                markPaidButton.setDisable(sel == null || "PAID".equals(sel.getPaymentStatus())));
        refresh();
    }

    @FXML
    private void onMarkPaid() {
        TestRequest sel = table.getSelectionModel().getSelectedItem();
        if (sel == null || "PAID".equals(sel.getPaymentStatus())) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirm payment for request #" + sel.getId() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null); confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try { dao.markPaid(sel.getId()); refresh(); }
            catch (Exception e) { new Alert(Alert.AlertType.ERROR, "Could not update: " + e.getMessage()).showAndWait(); }
        }
    }

    @FXML private void onRefresh() { refresh(); }

    private void refresh() {
        try { table.setItems(FXCollections.observableArrayList(dao.findAll())); markPaidButton.setDisable(true); }
        catch (Exception e) { new Alert(Alert.AlertType.ERROR, "Could not load: " + e.getMessage()).showAndWait(); }
    }
}
