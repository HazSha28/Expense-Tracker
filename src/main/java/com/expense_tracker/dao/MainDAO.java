package com.expense_tracker.dao;

import com.expense_tracker.model.Category;
import com.expense_tracker.model.Expense;
import com.expense_tracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainDAO {

    // ------------------ CATEGORY QUERIES ------------------
    private static final String ADD_CATEGORY = "INSERT INTO category (name) VALUES (?)";
    private static final String GET_ALL_CATEGORIES = "SELECT * FROM category ORDER BY category_id";
    private static final String DELETE_CATEGORY = "DELETE FROM category WHERE category_id = ?";
    private static final String UPDATE_CATEGORY = "UPDATE category SET name = ? WHERE category_id = ?";

    // ------------------ EXPENSE QUERIES ------------------
    private static final String ADD_EXPENSE =
            "INSERT INTO expense (category_id, notes, amt, date_time) VALUES (?, ?, ?, ?)";
    private static final String DELETE_EXPENSE =
            "DELETE FROM expense WHERE expense_id = ?";

    // ================= CATEGORY METHODS =================
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
                int id = rs.getInt("category_id");
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

    public void updateCategory(int categoryId, String newName) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_CATEGORY)) {
            ps.setString(1, newName);
            ps.setInt(2, categoryId);
            ps.executeUpdate();
        }
    }

    // ================= EXPENSE METHODS =================
    public void addExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(ADD_EXPENSE)) {
            ps.setInt(1, expense.getCatId());
            ps.setString(2, expense.getNotes());
            ps.setDouble(3, expense.getAmount());
            ps.setTimestamp(4, Timestamp.valueOf(expense.getDate()));
            ps.executeUpdate();
        }
    }

    /** Get all expenses sorted by expense_id ascending */
    public List<Expense> getAllExpensesSortedByIdAsc() throws SQLException {
        List<Expense> list = new ArrayList<>();
        String query = "SELECT * FROM expense ORDER BY expense_id ASC"; // ascending order
        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Expense exp = new Expense(
                        rs.getInt("expense_id"),
                        rs.getInt("category_id"),
                        rs.getString("notes"),
                        rs.getDouble("amt"),
                        rs.getTimestamp("date_time").toLocalDateTime()
                );
                list.add(exp);
            }
        }
        return list;
    }

    /** Get expenses filtered by category, sorted by expense_id ascending */
    public List<Expense> getExpensesByCategory(int categoryId) throws SQLException {
        List<Expense> list = new ArrayList<>();
        String query = "SELECT * FROM expense WHERE category_id = ? ORDER BY expense_id ASC";
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expense exp = new Expense(
                            rs.getInt("expense_id"),
                            rs.getInt("category_id"),
                            rs.getString("notes"),
                            rs.getDouble("amt"),
                            rs.getTimestamp("date_time").toLocalDateTime()
                    );
                    list.add(exp);
                }
            }
        }
        return list;
    }

    public void deleteExpense(int expId) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_EXPENSE)) {
            ps.setInt(1, expId);
            ps.executeUpdate();
        }
    }
}
