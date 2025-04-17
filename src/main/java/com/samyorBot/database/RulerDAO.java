package com.samyorBot.database;

import com.samyorBot.classes.characters.Ruler;

import java.sql.*;

public class RulerDAO {
    /** Insert or update the ruler record for this user. */
    public static boolean saveRuler(Ruler r) {
        String sql = """
            INSERT INTO rulers
              (user_id, country, military, stewardship, diplomacy, wisdom)
            VALUES (?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE
              country     = VALUES(country),
              military    = VALUES(military),
              stewardship = VALUES(stewardship),
              diplomacy   = VALUES(diplomacy),
              wisdom      = VALUES(wisdom),
              assigned_at = CURRENT_TIMESTAMP
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, r.getUserId());
            ps.setString(2, r.getCountry());
            ps.setInt(3, r.getMilitary());
            ps.setInt(4, r.getStewardship());
            ps.setInt(5, r.getDiplomacy());
            ps.setInt(6, r.getWisdom());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Load a Ruler by userId, or null if none. */
    public static Ruler getRuler(long userId) {
        String sql = "SELECT * FROM rulers WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ruler r = new Ruler(
                            rs.getLong("user_id"),
                            rs.getString("country"),
                            rs.getInt("military"),
                            rs.getInt("stewardship"),
                            rs.getInt("diplomacy"),
                            rs.getInt("wisdom")
                    );
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
