package com.personalexpensetracker.controller;

import com.personalexpensetracker.model.User;
import com.personalexpensetracker.service.UserService;
import com.personalexpensetracker.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) { messageLabel.setText("Enter username & password"); return; }

        User u = userService.authenticate(username, password);
        if (u == null) {
            messageLabel.setText("Invalid credentials");
            return;
        }

        // set session
        Session.setCurrentUser(u);

        // replace mainContent with dashboard
        try {
            StackPane mainContent = (StackPane) usernameField.getScene().lookup("#mainContent");
            if (mainContent != null) {
                Node loaded = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/dashboard.fxml"));
                mainContent.getChildren().setAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreateAccount() {
        try {
            StackPane mainContent = (StackPane) usernameField.getScene().lookup("#mainContent");
            if (mainContent != null) {
                javafx.scene.Node loaded = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/signup.fxml"));
                mainContent.getChildren().setAll(loaded);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
