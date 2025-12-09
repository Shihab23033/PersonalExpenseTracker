package com.personalexpensetracker.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private double budget; // monthly budget

    public User() {}

    public User(int id, String username, String password, String fullname, double budget) {
        this.id = id; this.username = username; this.password = password; this.fullname = fullname; this.budget = budget;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
}
