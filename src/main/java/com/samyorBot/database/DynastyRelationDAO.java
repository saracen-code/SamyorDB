package com.samyorBot.database;

import java.sql.*;
import java.util.*;

public class DynastyRelationDAO {

    public static void addRelation(int parentId, int childId) {
        String sql = "INSERT INTO dynasty_relations (parent_id, child_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, parentId);
            stmt.setInt(2, childId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<Long, List<Long>> getRelationsByDynasty(int dynastyId) {
        Map<Long, List<Long
                >> tree = new HashMap<>();

        String sql = """
            SELECT dr.parent_id, dr.child_id
            FROM dynasty_relations dr
            JOIN dynasty_members dm ON dm.character_id = dr.parent_id
            WHERE dm.dynasty_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dynastyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long parent = rs.getLong("parent_id");
                long child = rs.getLong("child_id");
                tree.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
                tree.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tree;
    }
}
