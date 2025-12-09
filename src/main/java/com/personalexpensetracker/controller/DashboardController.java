package com.personalexpensetracker.controller;

import com.personalexpensetracker.model.User;
import com.personalexpensetracker.service.ExpenseService;
import com.personalexpensetracker.service.UserService;
import com.personalexpensetracker.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label budgetLabel;
    @FXML private Label spentLabel;
    @FXML private Label balanceLabel;
    @FXML private Label warningLabel;

    private final ExpenseService expenseService = new ExpenseService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        refreshView();
    }

    private void refreshView() {
        User u = Session.getCurrentUser();
        if (u == null) return;

        welcomeLabel.setText(u.getFullname());
        budgetLabel.setText(String.format("%.2f", u.getBudget()));

        // compute total spent for current month for this user
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        double total = expenseService.getCategoryTotalsForMonthForUser(year, month, u.getId()).values().stream().mapToDouble(Double::doubleValue).sum();
        spentLabel.setText(String.format("%.2f", total));

        double balance = u.getBudget() - total;
        balanceLabel.setText(String.format("%.2f", balance));

        if (u.getBudget() > 0 && total / u.getBudget() >= 0.8) {
            warningLabel.setText("Warning: you have reached 80% of your monthly budget!");
        } else {
            warningLabel.setText("");
        }
    }

    public void onEditBudget() {
        User u = Session.getCurrentUser();
        if (u == null) return;

        TextInputDialog dlg = new TextInputDialog(String.valueOf(u.getBudget()));
        dlg.setHeaderText("Edit Monthly Budget");
        dlg.setContentText("Enter new monthly budget:");
        Optional<String> res = dlg.showAndWait();
        if (res.isPresent()) {
            try {
                double newBudget = Double.parseDouble(res.get());
                boolean ok = userService.updateBudget(u.getId(), newBudget);
                if (ok) {
                    // update session and refresh
                    u.setBudget(newBudget);
                    Session.setCurrentUser(u);
                    refreshView();
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Budget updated", ButtonType.OK);
                    a.showAndWait();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Unable to update budget", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Invalid number", ButtonType.OK);
                a.showAndWait();
            }
        }
    }

    public void onGoExpenses() {
        try {
            StackPane mainContent = (StackPane) welcomeLabel.getScene().lookup("#mainContent");
            if (mainContent != null) {
                javafx.scene.Node loaded = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/expense.fxml"));
                mainContent.getChildren().setAll(loaded);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void onLogout() {
        Session.clear();
        try {
            StackPane mainContent = (StackPane) welcomeLabel.getScene().lookup("#mainContent");
            if (mainContent != null) {
                javafx.scene.Node loaded = FXMLLoader.load(getClass().getResource("/com/personalexpensetracker/fxml/login.fxml"));
                mainContent.getChildren().setAll(loaded);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
