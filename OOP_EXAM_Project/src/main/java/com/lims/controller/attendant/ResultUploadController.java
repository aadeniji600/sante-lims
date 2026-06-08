package com.lims.controller.attendant;

import com.lims.dao.RequestDAO;
import com.lims.dao.ResultDAO;
import com.lims.model.LabResult;
import com.lims.model.TestRequest;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;

/** Result Upload and Validation for Lab Attendant. */
public class ResultUploadController {

    @FXML private ComboBox<TestRequest> requestBox;
    @FXML private TextField filePathField;
    @FXML private TextField resultTextField;
    @FXML private TableView<LabResult> table;
    @FXML private TableColumn<LabResult, Integer> colId;
    @FXML private TableColumn<LabResult, Integer> colReq;
    @FXML private TableColumn<LabResult, String>  colCustomer;
    @FXML private TableColumn<LabResult, String>  colTest;
    @FXML private TableColumn<LabResult, String>  colFile;
    @FXML private TableColumn<LabResult, String>  colStatus;
    @FXML private Button validateButton;

    private final ResultDAO resultDao = new ResultDAO();
    private final RequestDAO requestDao = new RequestDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReq.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTest.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colFile.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("validated"));

        colStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("RELEASED".equals(item)
                            ? "-fx-text-fill: #1a7f37; -fx-font-weight: bold;"
                            : "-fx-text-fill: #9a6700; -fx-font-weight: bold;");
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                validateButton.setDisable(sel == null || sel.isValidated()));
        refresh();
    }

    @FXML
    private void onChooseFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select result file (PDF or image)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Reports & Images", "*.pdf", "*.png", "*.jpg"));
        File f = fc.showOpenDialog(filePathField.getScene().getWindow());
        if (f != null) filePathField.setText(f.getAbsolutePath());
    }

    @FXML
    private void onUpload() {
        TestRequest req = requestBox.getValue();
        if (req == null) { warn("Pick the request this result belongs to."); return; }
        String path = filePathField.getText();
        String text = resultTextField.getText();
        if ((path == null || path.isBlank()) && (text == null || text.isBlank())) {
            warn("Attach a file or enter a text/numeric result."); return;
        }
        try { resultDao.upload(req.getId(), path, text); filePathField.clear(); resultTextField.clear(); requestBox.setValue(null); refresh(); }
        catch (Exception e) { error("Could not upload result: " + e.getMessage()); }
    }

    @FXML
    private void onValidate() {
        LabResult sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Select an uploaded result to validate."); return; }
        if (sel.isValidated()) { warn("This result is already released."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Release result #" + sel.getId() + " to " + sel.getCustomerName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null); confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try { resultDao.validate(sel.getId()); refresh(); info("Result released to customer."); }
            catch (Exception e) { error("Could not validate: " + e.getMessage()); }
        }
    }

    @FXML private void onRefresh() { refresh(); }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(resultDao.findAll()));
            requestBox.setItems(FXCollections.observableArrayList(requestDao.findAll()));
            validateButton.setDisable(true);
        } catch (Exception e) { error("Could not load: " + e.getMessage()); }
    }

    private void warn(String m)  { show(Alert.AlertType.WARNING, m); }
    private void info(String m)  { show(Alert.AlertType.INFORMATION, m); }
    private void error(String m) { show(Alert.AlertType.ERROR, m); }
    private void show(Alert.AlertType t, String m) { Alert a = new Alert(t, m); a.setHeaderText(null); a.showAndWait(); }
}
