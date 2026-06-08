package com.lims.controller.attendant;

import com.lims.dao.RequestDAO;
import com.lims.dao.SampleDAO;
import com.lims.model.Sample;
import com.lims.model.TestRequest;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/** Sample Lifecycle Tracking for Lab Attendant. */
public class SampleTrackingController {

    @FXML private ComboBox<TestRequest> paidRequestBox;
    @FXML private TableView<Sample> table;
    @FXML private TableColumn<Sample, Integer> colId;
    @FXML private TableColumn<Sample, Integer> colReq;
    @FXML private TableColumn<Sample, String>  colCustomer;
    @FXML private TableColumn<Sample, String>  colTest;
    @FXML private TableColumn<Sample, String>  colStatus;
    @FXML private TableColumn<Sample, String>  colUpdated;
    @FXML private ComboBox<String> statusBox;

    private final SampleDAO sampleDao = new SampleDAO();
    private final RequestDAO requestDao = new RequestDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReq.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTest.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colUpdated.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        statusBox.setItems(FXCollections.observableArrayList(SampleDAO.STATUSES));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) statusBox.setValue(sel.getStatus());
        });
        refresh();
    }

    @FXML
    private void onRegister() {
        TestRequest req = paidRequestBox.getValue();
        if (req == null) { warn("Pick a paid request to collect a sample for."); return; }
        try { sampleDao.register(req.getId()); refresh(); }
        catch (Exception e) { error("Could not register sample: " + e.getMessage()); }
    }

    @FXML
    private void onUpdateStatus() {
        Sample sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Select a sample in the table first."); return; }
        String status = statusBox.getValue();
        if (status == null) { warn("Pick a status."); return; }
        try { sampleDao.updateStatus(sel.getId(), status); refresh(); }
        catch (Exception e) { error("Could not update status: " + e.getMessage()); }
    }

    @FXML private void onRefresh() { refresh(); }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(sampleDao.findAll()));
            paidRequestBox.setItems(FXCollections.observableArrayList(requestDao.findPaidWithoutSample()));
        } catch (Exception e) { error("Could not load samples: " + e.getMessage()); }
    }

    private void warn(String m)  { show(Alert.AlertType.WARNING, m); }
    private void error(String m) { show(Alert.AlertType.ERROR, m); }
    private void show(Alert.AlertType t, String m) { Alert a = new Alert(t, m); a.setHeaderText(null); a.showAndWait(); }
}
