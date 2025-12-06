package com.personalexpensetracker.controller;

import com.personalexpensetracker.model.Category;
import com.personalexpensetracker.model.Expense;
import com.personalexpensetracker.service.CategoryService;
import com.personalexpensetracker.service.ExpenseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class ExpenseController {

    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField descField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private CheckBox recurringCheckbox;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private TableView<Expense> table;
    @FXML private TableColumn<Expense, String> colDesc;
    @FXML private TableColumn<Expense, Number> colAmount;
    @FXML private TableColumn<Expense, String> colCategory;
    @FXML private TableColumn<Expense, LocalDate> colDate;

    private final CategoryService categoryService = new CategoryService();
    private final ExpenseService expenseService = new ExpenseService();
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadCategories();
        loadExpenses();
        datePicker.setValue(LocalDate.now());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                categoryCombo.getSelectionModel().select(newV.getCategory());
                descField.setText(newV.getDescription());
                amountField.setText(String.valueOf(newV.getAmount()));
                datePicker.setValue(newV.getDate());
                recurringCheckbox.setSelected(newV.isRecurring());
            }
        });

        // Bind all columns
        colDesc.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescription())
        );

        colAmount.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getAmount())
        );

        colCategory.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory().getName())
        );

        colDate.setCellValueFactory(cell ->
                new javafx.beans.property.ObjectPropertyBase<LocalDate>() {
                    @Override public LocalDate get() {
                        return cell.getValue().getDate();
                    }
                    @Override public Object getBean() { return null; }
                    @Override public String getName() { return "date"; }
                }
        );
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAll();
        categoryCombo.setItems(FXCollections.observableArrayList(categories));
    }

    private void loadExpenses() {
        expenses.setAll(expenseService.getAll());
        table.setItems(expenses);
    }

    @FXML
    private void addExpense() {
        try {
            Category cat = categoryCombo.getSelectionModel().getSelectedItem();
            String desc = descField.getText();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate date = datePicker.getValue();
            boolean recurring = recurringCheckbox.isSelected();

            Expense e = new Expense(0, cat, desc, amount, date, recurring);
            expenseService.add(e);
            clearForm();
            loadExpenses();
        } catch (NumberFormatException ex) { showAlert("Invalid amount"); }
    }

    @FXML
    private void updateExpense() {
        Expense sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Select expense"); return; }
        try {
            sel.setCategory(categoryCombo.getSelectionModel().getSelectedItem());
            sel.setDescription(descField.getText());
            sel.setAmount(Double.parseDouble(amountField.getText()));
            sel.setDate(datePicker.getValue());
            sel.setRecurring(recurringCheckbox.isSelected());
            expenseService.update(sel);
            loadExpenses();
        } catch (NumberFormatException ex) { showAlert("Invalid amount"); }
    }

    @FXML
    private void deleteExpense() {
        Expense sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Select expense"); return; }
        expenseService.delete(sel.getId());
        loadExpenses();
        clearForm();
    }

    private void clearForm() {
        categoryCombo.getSelectionModel().clearSelection();
        descField.clear();
        amountField.clear();
        datePicker.setValue(LocalDate.now());
        recurringCheckbox.setSelected(false);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
