package com.expense_tracker.dao;

import com.expense_tracker.model.Category;
import com.expense_tracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainDAO {

    private static final String ADD_CATEGORY = "INSERT INTO category (name) VALUES (?)";
private static final String GET_ALL_CATEGORIES = "SELECT * FROM category ORDER BY category_id";
private static final String DELETE_CATEGORY = "DELETE FROM category WHERE category_id = ?";

public int getNextAvailableId() throws SQLException {
    List<Category> cats = getAllCategories();
    int next = 1;
    for (Category c : cats) {
        if (c.getCatId() != next) break;
        next++;
    }
    return next;
}

public void addCategory(Category cat) throws SQLException {
    try (Connection conn = DatabaseConnection.getDBConnection();
         PreparedStatement ps = conn.prepareStatement(ADD_CATEGORY)) {
        ps.setString(1, cat.getCatName());
        ps.executeUpdate();
    }
}

public List<Category> getAllCategories() throws SQLException {
    List<Category> list = new ArrayList<>();
    try (Connection conn = DatabaseConnection.getDBConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(GET_ALL_CATEGORIES)) {
        while (rs.next()) {
            int id = rs.getInt("category_id"); // corrected column name
            String name = rs.getString("name");
            list.add(new Category(id, name));
        }
    }
    return list;
}

public void deleteCategory(int categoryId) throws SQLException {
    try (Connection conn = DatabaseConnection.getDBConnection();
         PreparedStatement ps = conn.prepareStatement(DELETE_CATEGORY)) {
        ps.setInt(1, categoryId);
        ps.executeUpdate();
    }

}

public void updateCategory(int catId, String newName) throws SQLException {
    String sql = "UPDATE category SET name = ? WHERE category_id = ?";
    try (Connection conn = DatabaseConnection.getDBConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, newName);
        ps.setInt(2, catId);
        ps.executeUpdate();
    }
}

}
