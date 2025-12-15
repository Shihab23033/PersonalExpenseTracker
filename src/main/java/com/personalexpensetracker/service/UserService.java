package com.personalexpensetracker.service;

import com.personalexpensetracker.model.User;
import com.personalexpensetracker.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple user service. Assumes a `users` table with columns:
 * id INT AUTO_INCREMENT, username VARCHAR, password VARCHAR, fullname VARCHAR, budget DOUBLE
 * NOTE: Passwords are handled in plaintext here for simplicity; consider hashing for production.
 */
public class UserService {

    public User authenticate(String username, String password) {
        String sql = "SELECT id, username, password, fullname, budget FROM users WHERE username=? AND password=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("fullname"), rs.getDouble("budget"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getById(int id) {
        String sql = "SELECT id, username, password, fullname, budget FROM users WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("fullname"), rs.getDouble("budget"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // create user (simple)
    public boolean createUser(String username, String password, String fullname, double budget) {
        String sql = "INSERT INTO users (username, password, fullname, budget) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullname);
            ps.setDouble(4, budget);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Checks whether a username already exists in the users table.
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) as cnt FROM users WHERE username=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt") > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateBudget(int userId, double budget) {
        String sql = "UPDATE users SET budget=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, budget);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
