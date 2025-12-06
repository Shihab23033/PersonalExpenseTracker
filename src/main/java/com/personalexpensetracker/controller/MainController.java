package com.personalexpensetracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane mainContent;

    @FXML
    public void initialize() {
        loadView("category.fxml"); // default view
    }

    @FXML
    private void onShowCategory() { loadView("category.fxml"); }

    @FXML
    private void onShowExpense() { loadView("expense.fxml"); }

    @FXML
    private void onShowReport() { loadView("report.fxml"); }

    private void loadView(String fxmlName) {
        try {
            Node node = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/" + fxmlName));
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleDarkMode(ActionEvent event) {
    }
}
