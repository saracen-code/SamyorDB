package com.samyorBot.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            // Attempt to connect to the hosted database
            Connection connection = DatabaseConnection.getConnection();
            System.out.println("Database connection successful!");

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
