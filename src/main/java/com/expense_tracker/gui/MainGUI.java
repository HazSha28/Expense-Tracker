package com.expense_tracker.gui;

import javax.swing.*;
import java.awt.*;
import com.expense_tracker.dao.MainDAO;

public class MainGUI extends JFrame {

    private JButton categoryButton;
    private JButton expenseButton;
    private MainDAO mainDAO;

    public MainGUI() {
        this.mainDAO = new MainDAO(); // DAO instance
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setTitle("Expense Tracker - Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // center screen

        categoryButton = new JButton("Manage Categories");
        expenseButton = new JButton("Manage Expenses");

        // Make buttons bigger and more visible
        categoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        expenseButton.setFont(new Font("Arial", Font.BOLD, 16));
        categoryButton.setPreferredSize(new Dimension(220, 80));
        expenseButton.setPreferredSize(new Dimension(220, 80));
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(categoryButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(expenseButton, gbc);
    }

    private void setupEventListeners() {
        categoryButton.addActionListener(e -> {
            // Open Category Management Window
            CategoryGUI categoryGUI = new CategoryGUI(this, mainDAO);
            categoryGUI.setVisible(true);
        });

        expenseButton.addActionListener(e -> {
            // Open Expense Management Window
            ExpenseGUI expenseGUI = new ExpenseGUI(this, mainDAO);
            expenseGUI.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
