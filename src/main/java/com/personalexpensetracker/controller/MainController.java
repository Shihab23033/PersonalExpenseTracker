package com.personalexpensetracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import com.personalexpensetracker.util.Session;

import java.io.IOException;

public class MainController {

    @FXML private StackPane mainContent;

    @FXML
    public void initialize() {
        // show login first to select user
        loadView("login.fxml");
    }

    @FXML
    private void onShowCategory() {
        if (Session.getCurrentUser() == null) loadView("login.fxml");
        else loadView("category.fxml");
    }

    @FXML
    private void onShowExpense() {
        if (Session.getCurrentUser() == null) loadView("login.fxml");
        else loadView("expense.fxml");
    }

    @FXML
    private void onShowReport() {
        if (Session.getCurrentUser() == null) loadView("login.fxml");
        else loadView("report.fxml");
    }

    @FXML
    private void onShowDashboard() {
        if (Session.getCurrentUser() == null) loadView("login.fxml");
        else loadView("dashboard.fxml");
    }

    private void loadView(String fxmlName) {
        try {
            Node node = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/" + fxmlName));
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
