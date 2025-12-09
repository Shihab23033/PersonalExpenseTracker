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
import com.personalexpensetracker.util.Session;

public class ExpenseController {

    @FXML private ComboBox<Category> categoryCombo;
    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ComboBox<String> categoryFilterCombo;
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
        setupFilters();
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
        // populate category filter with names (include "All")
        ObservableList<String> catNames = FXCollections.observableArrayList();
        catNames.add("All");
        for (Category c : categories) catNames.add(c.getName());
        categoryFilterCombo.setItems(catNames);
        categoryFilterCombo.getSelectionModel().selectFirst();
    }

    private void setupFilters() {
        // months 1..12
        ObservableList<Integer> months = FXCollections.observableArrayList();
        for (int m = 1; m <= 12; m++) months.add(m);
        monthCombo.setItems(months);
        // years: current year and previous 4 years
        int curYear = LocalDate.now().getYear();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int y = curYear - 4; y <= curYear + 1; y++) years.add(y);
        yearCombo.setItems(years);
        // default to current month/year
        monthCombo.getSelectionModel().select(Integer.valueOf(LocalDate.now().getMonthValue()));
        yearCombo.getSelectionModel().select(Integer.valueOf(curYear));
    }

    private void loadExpenses() {
        List<Expense> list;
        if (Session.getCurrentUser() != null) {
            list = expenseService.getAllForUser(Session.getCurrentUser().getId());
        } else {
            list = expenseService.getAll();
        }
        expenses.setAll(list);
        table.setItems(expenses);
    }

    @FXML
    private void applyFilters() {
        Integer selMonth = monthCombo.getSelectionModel().getSelectedItem();
        Integer selYear = yearCombo.getSelectionModel().getSelectedItem();
        String selCategory = categoryFilterCombo.getSelectionModel().getSelectedItem();

        List<Expense> source;
        if (Session.getCurrentUser() != null) source = expenseService.getAllForUser(Session.getCurrentUser().getId());
        else source = expenseService.getAll();

        List<Expense> filtered = new java.util.ArrayList<>();
        for (Expense e : source) {
            boolean keep = true;
            if (selYear != null && e.getDate() != null) keep = keep && (e.getDate().getYear() == selYear);
            if (selMonth != null && e.getDate() != null) keep = keep && (e.getDate().getMonthValue() == selMonth);
            if (selCategory != null && !"All".equals(selCategory)) {
                String cname = e.getCategory() == null ? "Uncategorized" : e.getCategory().getName();
                keep = keep && selCategory.equals(cname);
            }
            if (keep) filtered.add(e);
        }
        expenses.setAll(filtered);
    }

    @FXML
    private void clearFilters() {
        monthCombo.getSelectionModel().select(Integer.valueOf(LocalDate.now().getMonthValue()));
        yearCombo.getSelectionModel().select(Integer.valueOf(LocalDate.now().getYear()));
        if (categoryFilterCombo.getItems() != null && !categoryFilterCombo.getItems().isEmpty()) categoryFilterCombo.getSelectionModel().selectFirst();
        loadExpenses();
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
            if (Session.getCurrentUser() != null) e.setUserId(Session.getCurrentUser().getId());
            expenseService.add(e);
            clearForm();
            loadExpenses();

            // after adding, check budget for logged-in user
            if (Session.getCurrentUser() != null) {
                int year = date.getYear();
                int month = date.getMonthValue();
                double total = expenseService.getTotalForUserMonth(year, month, Session.getCurrentUser().getId());
                double budget = Session.getCurrentUser().getBudget();
                if (budget > 0 && total > budget) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Budget exceeded: you have spent " + total + " which is over your budget of " + budget, ButtonType.OK);
                    a.showAndWait();
                }
            }
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
            if (Session.getCurrentUser() != null) sel.setUserId(Session.getCurrentUser().getId());
            expenseService.update(sel);
            loadExpenses();

            // check budget after update
            if (Session.getCurrentUser() != null) {
                LocalDate date = sel.getDate();
                int year = date.getYear();
                int month = date.getMonthValue();
                double total = expenseService.getTotalForUserMonth(year, month, Session.getCurrentUser().getId());
                double budget = Session.getCurrentUser().getBudget();
                if (budget > 0 && total > budget) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Budget exceeded: you have spent " + total + " which is over your budget of " + budget, ButtonType.OK);
                    a.showAndWait();
                }
            }
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
