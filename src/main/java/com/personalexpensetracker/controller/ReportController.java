package com.personalexpensetracker.controller;

import com.personalexpensetracker.service.ExpenseService;
import com.personalexpensetracker.util.Session;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Map;

public class ReportController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ComboBox<String> monthCombo;
    @FXML private Label totalLabel;
    @FXML private HBox legendBox;

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

        // hide built-in chart legend because we use a custom legendBox
        if (barChart != null) barChart.setLegendVisible(false);

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


        // Prepare a color palette and map categories to colors
        String[] palette = new String[]{"#4e79a7", "#f28e2b", "#e15759", "#76b7b2", "#59a14f", "#edc949", "#af7aa1", "#ff9da7", "#9c755f", "#bab0ac"};
        java.util.Map<String, String> colorMap = new java.util.HashMap<>();
        int i = 0;

        // Single series for consistent thickness but apply per-bar colors/styles and build custom legend
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        // clear previous legend
        if (legendBox != null) legendBox.getChildren().clear();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            Double total = entry.getValue();
            totalExpense += total;

            // assign color
            String color = palette[i % palette.length];
            colorMap.put(category, color);
            i++;

            XYChart.Data<String, Number> data = new XYChart.Data<>(category, total);
            series.getData().add(data);

            // add legend swatch
            if (legendBox != null) {
                Rectangle sw = new Rectangle(14, 12, Color.web(color));
                sw.getStyleClass().add("legend-swatch");
                Label lbl = new Label(" " + category);
                lbl.getStyleClass().add("legend-label");
                HBox item = new HBox(6, sw, lbl);
                item.getStyleClass().add("legend-item");
                legendBox.getChildren().add(item);
            }

            // style node when available and add label/tooltip
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    // apply color
                    newNode.setStyle(String.format("-fx-bar-fill: %s; -fx-background-radius: 6;", color));

                    Label label = new Label(String.format("%.2f", total));
                    label.getStyleClass().add("bar-label");
                    label.setTranslateY(-18);
                    StackPane stackPane = (StackPane) newNode;
                    stackPane.getChildren().add(label);

                    javafx.scene.control.Tooltip t = new javafx.scene.control.Tooltip(String.format("%s: %.2f", category, total));
                    javafx.scene.control.Tooltip.install(newNode, t);
                }
            });
        }

        barChart.getData().add(series);

        // Chart tuning
        barChart.setCategoryGap(18);
        barChart.setBarGap(6);
        barChart.setAnimated(false);

        totalLabel.setText(String.format("Total Expense: %.2f", totalExpense));
    }
}