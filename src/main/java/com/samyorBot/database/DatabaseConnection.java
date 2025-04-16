package com.samyorBot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Update with your actual database details
    private static final String URL = "jdbc:mysql://na03-sql.pebblehost.com:3306/customer_900636_samyordata"; // Replace with your actual database name
    private static final String USER = "customer_900636_samyordata"; // Replace with your MySQL username
    private static final String PASSWORD = "co^9bHzxC2YT2@I.2@Awm!JL"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}
