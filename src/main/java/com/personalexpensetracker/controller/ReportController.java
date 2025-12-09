package com.personalexpensetracker.controller;

import com.personalexpensetracker.service.ExpenseService;
import com.personalexpensetracker.util.Session;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Map;

public class ReportController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ComboBox<String> monthCombo;
    @FXML private Label totalLabel;

    private final ExpenseService expenseService = new ExpenseService();

    @FXML
    public void initialize() {
        int currentYear = Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();

        // Populate year dropdown (default = current year)
        yearCombo.getSelectionModel().select(Integer.valueOf(currentYear));
        yearCombo.getItems().addAll(currentYear - 2, currentYear - 1, currentYear);


        // Populate month dropdown with names (default = current month)
        for (Month m : Month.values()) {
            String name = m.name().substring(0,1) + m.name().substring(1).toLowerCase();
            monthCombo.getItems().add(name);
        }
        monthCombo.getSelectionModel().select(currentMonth - 1);

        yearCombo.setOnAction(e -> loadChart());
        monthCombo.setOnAction(e -> loadChart());

        loadChart();
    }

    private void loadChart() {
        Integer year = yearCombo.getValue();
        String monthName = monthCombo.getValue();
        if (year == null || monthName == null) return;

        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;

        barChart.getData().clear();
        Map<String, Double> categoryTotals;
        if (Session.getCurrentUser() != null) {
            categoryTotals = expenseService.getCategoryTotalsForMonthForUser(year, month, Session.getCurrentUser().getId());
        } else {
            categoryTotals = expenseService.getCategoryTotalsForMonth(year, month);
        }

        double totalExpense = 0.0;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            Double total = entry.getValue();
            totalExpense += total;

            // Each category gets its own series -> distinct color + legend entry
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(category);

            XYChart.Data<String, Number> data = new XYChart.Data<>(category, total);
            series.getData().add(data);

            // Add label above bar
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Label label = new Label(String.valueOf(total));
                    label.getStyleClass().add("bar-label");
                    StackPane stackPane = (StackPane) newNode;
                    stackPane.getChildren().add(label);
                }
            });

            barChart.getData().add(series);
        }

        totalLabel.setText("Total Expense: " + totalExpense);
    }
}