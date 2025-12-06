package com.personalexpensetracker.model;

import java.time.LocalDate;

public class RecurringExpense {
    private int id;
    private int expenseId;
    private String frequency; // e.g., "MONTHLY"
    private LocalDate startDate;
    private LocalDate endDate;

    public RecurringExpense() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getExpenseId() { return expenseId; }
    public void setExpenseId(int expenseId) { this.expenseId = expenseId; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
