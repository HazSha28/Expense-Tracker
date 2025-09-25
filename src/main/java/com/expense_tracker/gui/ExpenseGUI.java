package com.expense_tracker.gui;

import com.expense_tracker.dao.MainDAO;
import com.expense_tracker.model.Category;
import com.expense_tracker.model.Expense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseGUI extends JFrame {

    private MainGUI mainGUI;
    private MainDAO mainDAO;

    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;      // For adding new expense
    private JComboBox<String> filterComboBox;        // For filtering table
    private JTextField amountField, notesField;
    private JButton addButton, deleteButton;
    private JLabel totalLabel;

    private Map<Integer, String> idToNameMap = new HashMap<>();  // categoryId -> name
    private Map<String, Integer> nameToIdMap = new HashMap<>();  // name -> categoryId

    public ExpenseGUI(MainGUI mainGUI, MainDAO mainDAO) {
        this.mainGUI = mainGUI;
        this.mainDAO = mainDAO;
        initializeComponents();
        loadCategories();   // Populate dropdowns
        loadExpenses();     // Load all expenses initially
    }

    private void initializeComponents() {
        setTitle("Expense Tracker - Expenses");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(mainGUI);

        // Table
        String[] columnNames = {"Expense ID", "Category", "Notes", "Amount", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        expenseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expenseTable);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 7, 10));
        categoryComboBox = new JComboBox<>();
        filterComboBox = new JComboBox<>();
        amountField = new JTextField();
        notesField = new JTextField();
        addButton = new JButton("Add Expense");
        deleteButton = new JButton("Delete Selected");

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(addButton);

        inputPanel.add(new JLabel("Notes:"));
        inputPanel.add(notesField);
        inputPanel.add(new JLabel("Filter by Category:"));
        inputPanel.add(filterComboBox);
        inputPanel.add(deleteButton);

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addExpense());
        deleteButton.addActionListener(e -> deleteSelectedExpense());
        filterComboBox.addActionListener(e -> filterExpenses());
    }

    private void loadCategories() {
        try {
            categoryComboBox.removeAllItems();
            filterComboBox.removeAllItems();
            idToNameMap.clear();
            nameToIdMap.clear();

            List<Category> categories = mainDAO.getAllCategories();
            filterComboBox.addItem("All"); // Option to show all expenses
            for (Category cat : categories) {
                idToNameMap.put(cat.getCatId(), cat.getCatName());
                nameToIdMap.put(cat.getCatName(), cat.getCatId());
                categoryComboBox.addItem(cat.getCatName());
                filterComboBox.addItem(cat.getCatName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addExpense() {
        try {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory == null || !nameToIdMap.containsKey(selectedCategory)) {
                JOptionPane.showMessageDialog(this, "Invalid Category!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int catId = nameToIdMap.get(selectedCategory);

            double amount = Double.parseDouble(amountField.getText().trim());
            String notes = notesField.getText().trim();

            Expense expense = new Expense(0, catId, notes, amount, LocalDateTime.now());
            mainDAO.addExpense(expense);

            JOptionPane.showMessageDialog(this, "Expense Added!");
            amountField.setText("");
            notesField.setText("");
            filterComboBox.setSelectedIndex(0); // Reset filter to All
            loadExpenses();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number input!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding expense: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an expense to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0); // Expense ID is in first column

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            mainDAO.deleteExpense(expenseId);
            JOptionPane.showMessageDialog(this, "Expense deleted.");
            loadExpenses();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExpenses() {
        try {
            tableModel.setRowCount(0);
            double total = 0;

            String filter = (String) filterComboBox.getSelectedItem();
            List<Expense> expenses;
            if (filter != null && !filter.equals("All")) {
                int catId = nameToIdMap.get(filter);
                expenses = mainDAO.getExpensesByCategory(catId);
            } else {
                expenses = mainDAO.getAllExpensesSortedByIdAsc();
            }

            for (Expense exp : expenses) {
                String catName = idToNameMap.getOrDefault(exp.getCatId(), "Unknown");
                tableModel.addRow(new Object[]{
                        exp.getExpId(),
                        catName,
                        exp.getNotes(),
                        exp.getAmount(),
                        exp.getDate()
                });
                total += exp.getAmount();
            }

            totalLabel.setText("Total: " + total);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterExpenses() {
        loadExpenses();
    }
}
