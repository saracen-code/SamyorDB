package com.samyorBot.database;

import com.samyorBot.classes.cities.City;
import java.sql.*;
import java.util.*;

public class CityDAO {
    public static List<City> getAllCities() throws SQLException {
        String sql = "SELECT id,name,coastal,riverine FROM cities";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<City> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new City(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("coastal"),
                        rs.getBoolean("riverine")
                ));
            }
            return list;
        }
    }

    /** Insert a new city, returns generated ID or –1 on failure */
    public static int insertCity(String name, boolean coastal, boolean riverine) throws SQLException {
        String sql = """
            INSERT INTO cities (name, coastal, riverine)
            VALUES (?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setBoolean(2, coastal);
            ps.setBoolean(3, riverine);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        }
    }
}
