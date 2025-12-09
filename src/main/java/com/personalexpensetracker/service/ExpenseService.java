package com.personalexpensetracker.service;

import com.personalexpensetracker.model.Category;
import com.personalexpensetracker.model.Expense;
import com.personalexpensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseService {

    // CategoryService not required here; queries return category info via joins

    public List<Expense> getAll() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT e.id, e.description, e.amount, e.date, e.is_recurring, c.id as cid, c.name as cname " +
                "FROM Expense e LEFT JOIN Category c ON e.category_id = c.id ORDER BY e.date DESC";
        try (Connection conn = DBUtil.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = new Category(rs.getInt("cid"), rs.getString("cname"));
                Expense ex = new Expense(rs.getInt("id"), cat, rs.getString("description"), rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(), rs.getBoolean("is_recurring"));
                list.add(ex);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Map<String, double[]> getMonthlyCategoryTotals(int year) {
        // Key: category name, Value: array of 12 monthly totals
        Map<String, double[]> categoryMap = new HashMap<>();

        for (Expense e : getAll()) {
            if (e.getDate().getYear() == year) {
                String cat = e.getCategory().getName();
                int month = e.getDate().getMonthValue(); // 1–12
                categoryMap.putIfAbsent(cat, new double[12]);
                categoryMap.get(cat)[month - 1] += e.getAmount();
            }
        }

        return categoryMap;
    }

    public void add(Expense e) {
        String sql = "INSERT INTO Expense (category_id, description, amount, date, is_recurring, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getCategory() == null ? Types.NULL : e.getCategory().getId());
            ps.setString(2, e.getDescription());
            ps.setDouble(3, e.getAmount());
            ps.setDate(4, Date.valueOf(e.getDate()));
            ps.setBoolean(5, e.isRecurring());
            ps.setInt(6, e.getUserId());
            ps.executeUpdate();
            // generated id not used here
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public void update(Expense e) {
        String sql = "UPDATE Expense SET category_id=?, description=?, amount=?, date=?, is_recurring=?, user_id=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getCategory() == null ? Types.NULL : e.getCategory().getId());
            ps.setString(2, e.getDescription());
            ps.setDouble(3, e.getAmount());
            ps.setDate(4, Date.valueOf(e.getDate()));
            ps.setBoolean(5, e.isRecurring());
            ps.setInt(6, e.getUserId());
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public List<Expense> getAllForUser(int userId) {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT e.id, e.description, e.amount, e.date, e.is_recurring, e.user_id, c.id as cid, c.name as cname " +
                "FROM Expense e LEFT JOIN Category c ON e.category_id = c.id WHERE e.user_id=? ORDER BY e.date DESC";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category cat = new Category(rs.getInt("cid"), rs.getString("cname"));
                    Expense ex = new Expense(rs.getInt("id"), cat, rs.getString("description"), rs.getDouble("amount"),
                            rs.getDate("date").toLocalDate(), rs.getBoolean("is_recurring"));
                    ex.setUserId(rs.getInt("user_id"));
                    list.add(ex);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Map<String, Double> getCategoryTotalsForMonth(int year, int month) {
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : getAll()) {
            if (e.getDate().getYear() == year && e.getDate().getMonthValue() == month) {
                String cat = e.getCategory().getName();
                totals.put(cat, totals.getOrDefault(cat, 0.0) + e.getAmount());
            }
        }
        return totals;
    }

    /**
     * Returns category totals for a given month filtered by user id.
     * Requires `user_id` column in Expense table.
     */
    public Map<String, Double> getCategoryTotalsForMonthForUser(int year, int month, int userId) {
        Map<String, Double> totals = new HashMap<>();
        String sql = "SELECT c.name as cname, SUM(e.amount) as total FROM Expense e LEFT JOIN Category c ON e.category_id = c.id " +
                "WHERE YEAR(e.date)=? AND MONTH(e.date)=? AND e.user_id=? GROUP BY c.name";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cname = rs.getString("cname");
                    double total = rs.getDouble("total");
                    totals.put(cname == null ? "Uncategorized" : cname, total);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return totals;
    }

    /**
     * Returns total expense amount for a given user in a specific month/year.
     */
    public double getTotalForUserMonth(int year, int month, int userId) {
        String sql = "SELECT COALESCE(SUM(amount),0) as total FROM Expense WHERE YEAR(date)=? AND MONTH(date)=? AND user_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }



    public void delete(int id) {
        String sql = "DELETE FROM Expense WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // get monthly totals for bar chart (simple aggregation)
    public List<MonthlyTotal> getMonthlyTotals(int year) {
        List<MonthlyTotal> list = new ArrayList<>();
        String sql = "SELECT MONTH(date) as m, SUM(amount) as total FROM Expense WHERE YEAR(date)=? GROUP BY MONTH(date) ORDER BY m";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new MonthlyTotal(rs.getInt("m"), rs.getDouble("total")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static class MonthlyTotal {
        public final int month;
        public final double total;
        public MonthlyTotal(int month, double total) { this.month = month; this.total = total; }
    }
}
