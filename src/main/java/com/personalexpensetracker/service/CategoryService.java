package com.personalexpensetracker.service;

import com.personalexpensetracker.model.Category;
import com.personalexpensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM Category ORDER BY name";
        try (Connection conn = DBUtil.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new Category(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void add(String name) {
        String sql = "INSERT INTO Category(name) VALUES(?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(int id, String name) {
        String sql = "UPDATE Category SET name=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.setInt(2, id); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Category WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
