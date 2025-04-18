package com.samyorBot.database;

import com.samyorBot.classes.cities.City;
import com.samyorBot.classes.cities.ConnectionType;
import java.sql.*;
import java.util.*;

public class ConnectionDAO {
    public record Conn(int fromId, int toId, double weight, ConnectionType type) {}

    public static List<Conn> getAllConnections() throws SQLException {
        String sql = "SELECT from_city,to_city,weight,type FROM city_connections";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Conn> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Conn(
                        rs.getInt("from_city"),
                        rs.getInt("to_city"),
                        rs.getDouble("weight"),
                        ConnectionType.valueOf(rs.getString("type"))
                ));
            }
            return list;
        }
    }
}
