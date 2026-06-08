package com.lims.controller.admin;

import com.lims.dao.TestDAO;
import com.lims.model.TestType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/** Custom Test Builder: Super Admin defines/edits/deletes test types. */
public class TestBuilderController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField tatField;
    @FXML private ComboBox<String> formatBox;

    @FXML private TableView<TestType> table;
    @FXML private TableColumn<TestType, Integer> colId;
    @FXML private TableColumn<TestType, String>  colName;
    @FXML private TableColumn<TestType, Double>  colPrice;
    @FXML private TableColumn<TestType, Integer> colTat;
    @FXML private TableColumn<TestType, String>  colFormat;

    private final TestDAO dao = new TestDAO();
    private Integer editingId = null;

    @FXML
    public void initialize() {
        formatBox.setItems(FXCollections.observableArrayList("NUMERIC", "TEXT", "PDF", "IMAGE"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTat.setCellValueFactory(new PropertyValueFactory<>("tatHours"));
        colFormat.setCellValueFactory(new PropertyValueFactory<>("resultFormat"));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) loadIntoForm(sel);
        });
        refresh();
    }

    private void loadIntoForm(TestType t) {
        editingId = t.getId();
        nameField.setText(t.getName());
        priceField.setText(String.valueOf(t.getPrice()));
        tatField.setText(String.valueOf(t.getTatHours()));
        formatBox.setValue(t.getResultFormat());
    }

    @FXML
    private void onSave() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String format = formatBox.getValue();
        if (name.isEmpty() || format == null) { warn("Test name and result format are required."); return; }
        double price; int tat;
        try { price = Double.parseDouble(priceField.getText().trim()); if (price < 0) { warn("Price cannot be negative."); return; } }
        catch (Exception e) { warn("Price must be a number, e.g. 5000"); return; }
        try { tat = Integer.parseInt(tatField.getText().trim()); if (tat <= 0) { warn("TAT must be greater than 0."); return; } }
        catch (Exception e) { warn("TAT must be a whole number of hours, e.g. 24"); return; }
        try {
            if (editingId == null) dao.insert(name, price, tat, format);
            else dao.update(editingId, name, price, tat, format);
            onClear(); refresh();
        } catch (Exception e) { error("Could not save test: " + e.getMessage()); }
    }

    @FXML
    private void onDelete() {
        TestType sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Select a test in the table to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete test \"" + sel.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null); confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try { dao.delete(sel.getId()); onClear(); refresh(); }
            catch (Exception e) { error("Could not delete. It may be linked to existing requests.\n\n" + e.getMessage()); }
        }
    }

    @FXML
    private void onClear() {
        editingId = null;
        nameField.clear(); priceField.clear(); tatField.clear();
        formatBox.setValue(null); table.getSelectionModel().clearSelection();
    }

    private void refresh() {
        try { table.setItems(FXCollections.observableArrayList(dao.findAll())); }
        catch (Exception e) { error("Could not load tests: " + e.getMessage()); }
    }

    private void warn(String m)  { show(Alert.AlertType.WARNING, m); }
    private void error(String m) { show(Alert.AlertType.ERROR, m); }
    private void show(Alert.AlertType t, String m) { Alert a = new Alert(t, m); a.setHeaderText(null); a.showAndWait(); }
}
