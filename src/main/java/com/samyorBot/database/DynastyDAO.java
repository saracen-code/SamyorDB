package com.samyorBot.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynastyDAO {

    public class DynastyRelationDAO {

        public static void addRoot(long characterId) {
            String sql = "INSERT INTO dynasty_relations (parent_id, child_id) VALUES (NULL, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, characterId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean deleteDynastyById(int dynastyId, long requesterId) {
        String sql = "DELETE FROM dynasties WHERE id = ? AND creator_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dynastyId);
            stmt.setLong(2, requesterId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addCharacterToDynastyWithParent(long childId, long parentId) {
        int dynastyId = getDynastyIdByCharacter(parentId);
        if (dynastyId == -1) return false;

        return addCharacterToDynasty(dynastyId, childId);
    }

    public static Map<Integer, String> getDynastyIdNameMapByCreator(long creatorId) {
        Map<Integer, String> dynasties = new HashMap<>();
        String sql = "SELECT id, name FROM dynasties WHERE creator_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, creatorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dynasties.put(rs.getInt("id"), rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dynasties;
    }



    public static boolean createDynasty(String name, long creatorId) {
        String sql = "INSERT INTO dynasties (name, creator_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setLong(2, creatorId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getDynastyIdByCharacter(long characterId) {
        String sql = "SELECT dynasty_id FROM dynasty_members WHERE character_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("dynasty_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    public static int createDynastyAndReturnId(String name, long creatorId) {
        String sql = "INSERT INTO dynasties (name, creator_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setLong(2, creatorId);
            int affected = stmt.executeUpdate();

            if (affected == 0) return -1;

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static boolean addCharacterToDynasty(int dynastyId, long characterId) {
        String sql = "INSERT INTO dynasty_members (dynasty_id, character_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dynastyId);
            stmt.setLong(2, characterId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Ignore duplicate entry
            if (!e.getMessage().contains("Duplicate")) e.printStackTrace();
            return false;
        }
    }

    public static boolean removeCharacterFromDynasty(long characterId) {
        String sql = "DELETE FROM dynasty_members WHERE character_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, characterId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Long> getAllCharacterIdsInDynasty(int dynastyId) {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT character_id FROM dynasty_members WHERE dynasty_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dynastyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("character_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public static List<Integer> getDynastiesByCreator(long creatorId) {
        List<Integer> dynasties = new ArrayList<>();
        String sql = "SELECT id FROM dynasties WHERE creator_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, creatorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dynasties.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dynasties;
    }
}
