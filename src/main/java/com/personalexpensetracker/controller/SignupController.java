package com.personalexpensetracker.controller;

import com.personalexpensetracker.model.User;
import com.personalexpensetracker.service.UserService;
import com.personalexpensetracker.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private TextField fullnameField;
    @FXML private TextField budgetField;
    @FXML private TextField passwordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void onCreate() {
        String username = usernameField.getText().trim();
        String fullname = fullnameField.getText().trim();
        String pass = passwordField.getText().trim();
        double budget = 0.0;
        try { budget = Double.parseDouble(budgetField.getText().trim()); } catch (Exception ignored) {}

        if (username.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Username and password required");
            return;
        }

        boolean ok = userService.createUser(username, pass, fullname, budget);
        if (!ok) {
            messageLabel.setText("Unable to create user (maybe username exists)");
            return;
        }

        // authenticate and set session
        User u = userService.authenticate(username, pass);
        if (u != null) {
            Session.setCurrentUser(u);
            // load dashboard into mainContent
            try {
                StackPane mainContent = (StackPane) usernameField.getScene().lookup("#mainContent");
                if (mainContent != null) {
                    Node loaded = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/dashboard.fxml"));
                    mainContent.getChildren().setAll(loaded);
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
