package com.expense_tracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String URL="jdbc:mysql://localhost:3306/expensetracker";
    private static String USERNAME="root";
    private static String PASSWORD="Open05##";
    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println("JDBC Driver not found: "+e);
        }
    }
    public static Connection getDBConnection() throws SQLException{
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }
}

