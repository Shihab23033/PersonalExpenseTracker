package com.personalexpensetracker.model;

import java.time.LocalDate;

public class Expense {
    private int id;
    private Category category;
    private String description;
    private double amount;
    private LocalDate date;
    private boolean recurring;

    public Expense() {}

    public Expense(int id, Category category, String description, double amount, LocalDate date, boolean recurring) {
        this.id = id; this.category = category; this.description = description;
        this.amount = amount; this.date = date; this.recurring = recurring;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
}
