package com.expense_tracker.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.expense_tracker.dao.MainDAO;

public class ExpenseGUI extends JFrame {

    private MainGUI mainGUI;
    private MainDAO mainDAO;

    public ExpenseGUI(MainGUI mainGUI, MainDAO mainDAO) {
        this.mainGUI = mainGUI;
        this.mainDAO = mainDAO;

        setTitle("Expense Tracker - Expense Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // only closes this window
        setLocationRelativeTo(mainGUI);

        JLabel label = new JLabel("Expense Management Page", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    }
}
