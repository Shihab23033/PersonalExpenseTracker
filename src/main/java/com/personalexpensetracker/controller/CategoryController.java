package com.personalexpensetracker.controller;

import com.personalexpensetracker.model.Category;
import com.personalexpensetracker.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CategoryController {

    @FXML private TextField nameField;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private ListView<Category> listView;

    private final CategoryService service = new CategoryService();
    private final ObservableList<Category> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        refresh();
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) nameField.setText(newV.getName());
        });
    }

    @FXML
    private void addCategory() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showAlert("Name required"); return; }
        service.add(name);
        nameField.clear();
        refresh();
    }

    @FXML
    private void updateCategory() {
        Category sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Select category"); return; }
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showAlert("Name required"); return; }
        service.update(sel.getId(), name);
        refresh();
    }

    @FXML
    private void deleteCategory() {
        Category sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Select category"); return; }
        service.delete(sel.getId());
        nameField.clear();
        refresh();
    }

    private void refresh() {
        data.setAll(service.getAll());
        listView.setItems(data);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
