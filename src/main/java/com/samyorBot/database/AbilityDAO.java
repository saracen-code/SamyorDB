package com.samyorBot.database;

import com.samyorBot.classes.characters.Ability;
import com.samyorBot.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbilityDAO {

    public static boolean saveAbility(Ability ability) {
        String sql = """
            INSERT INTO abilities (name, type, damage, description, approved)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ability.getName());
            stmt.setString(2, ability.getType());
            stmt.setInt(3, ability.getDamage());
            stmt.setString(4, ability.getDescription());
            stmt.setBoolean(5, ability.isApproved());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Ability> getUnapprovedAbilities() {
        List<Ability> abilities = new ArrayList<>();
        String sql = "SELECT * FROM abilities WHERE approved = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ability ability = new Ability();
                ability.setId(rs.getInt("id"));
                ability.setName(rs.getString("name"));
                ability.setType(rs.getString("type"));
                ability.setDamage(rs.getInt("damage"));
                ability.setDescription(rs.getString("description"));
                ability.setApproved(rs.getBoolean("approved"));

                abilities.add(ability);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return abilities;
    }

    public static boolean approveAbility(int abilityId) {
        String sql = "UPDATE abilities SET approved = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, abilityId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Ability getAbilityById(int id) {
        String sql = "SELECT * FROM abilities WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Ability ability = new Ability();
                ability.setId(rs.getInt("id"));
                ability.setName(rs.getString("name"));
                ability.setType(rs.getString("type"));
                ability.setDamage(rs.getInt("damage"));
                ability.setDescription(rs.getString("description"));
                ability.setApproved(rs.getBoolean("approved"));
                return ability;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Ability> getApprovedAbilities() {
        List<Ability> abilities = new ArrayList<>();
        String sql = "SELECT * FROM abilities WHERE approved = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ability ability = new Ability();
                ability.setId(rs.getInt("id"));
                ability.setName(rs.getString("name"));
                ability.setType(rs.getString("type"));
                ability.setDamage(rs.getInt("damage"));
                ability.setDescription(rs.getString("description"));
                ability.setApproved(rs.getBoolean("approved"));

                abilities.add(ability);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return abilities;
    }

    public static List<Ability> getRandomApprovedAbilities(int limit) {
        List<Ability> abilities = new ArrayList<>();
        String sql = "SELECT * FROM abilities WHERE approved = TRUE ORDER BY RAND() LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ability ability = new Ability();
                ability.setId(rs.getInt("id"));
                ability.setName(rs.getString("name"));
                ability.setType(rs.getString("type"));
                ability.setDamage(rs.getInt("damage"));
                ability.setDescription(rs.getString("description"));
                ability.setApproved(rs.getBoolean("approved"));

                abilities.add(ability);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return abilities;
    }

}
