package com.samyorBot.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyorBot.classes.characters.Character;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CharacterDAO {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean saveCharacter(Character c) {
        String sql = """
        INSERT INTO characters
          (user_id, funds, culture, name, location,
           birthdate, deathdate, affiliation, backstory,
           traits, statistics, abilities, image, tier)
        VALUES (?,?,?,?,?,
                ?,       ?,        ?,           ?,
                ?,       ?,          ?,         ?,    ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, c.getUserId());
            ps.setDouble(2, c.getFunds());
            ps.setString(3, c.getCulture());
            ps.setString(4, c.getName());
            ps.setString(5, c.getLocation());

            // 6 & 7: birthdate & deathdate as real SQL Dates
            ps.setDate(6, java.sql.Date.valueOf(c.getBirthdate()));
            ps.setDate(7, java.sql.Date.valueOf(c.getDeathdate()));

            ps.setString(8, c.getAffiliation());
            ps.setString(9, c.getBackstory());

            // 10–12: store JSON arrays/maps as JSON text
            ObjectMapper M = new ObjectMapper();
            ps.setString(10, M.writeValueAsString(c.getTraits()));
            ps.setString(11, M.writeValueAsString(c.getStatistics()));
            ps.setString(12, M.writeValueAsString(c.getAbilities()));

            ps.setString(13, c.getImage());
            ps.setString(14, c.getTier());

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getLong(1));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    public static boolean updateCharacterField(long characterId, String field, Object value) {
        List<String> allowedFields = List.of(
                "funds", "culture", "name", "location", "birthdate", "deathdate",
                "affiliation", "backstory", "traits", "statistics", "abilities", "image", "tier"
        );
        if (!allowedFields.contains(field)) {
            System.err.println("❌ Attempted to update invalid field: " + field);
            return false;
        }

        String sql = "UPDATE characters SET " + field + " = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (value instanceof List<?> || value instanceof Map<?, ?>) {
                stmt.setString(1, mapper.writeValueAsString(value));
            } else if (value instanceof Number) {
                stmt.setObject(1, value);
            } else {
                stmt.setString(1, value.toString());
            }

            stmt.setLong(2, characterId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Character getCharacterById(long id) {
        String sql = "SELECT * FROM characters WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCharacterFromResultSet(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Character> getCharactersByUserId(long userId) {
        List<Character> characters = new ArrayList<>();
        String sql = "SELECT * FROM characters WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                characters.add(extractCharacterFromResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return characters;
    }

    private static Character extractCharacterFromResultSet(ResultSet rs) throws SQLException {
        try {
            Character character = new Character();
            character.setId(rs.getLong("id"));
            character.setUserId(rs.getLong("user_id"));
            character.setFunds(rs.getDouble("funds"));
            character.setCulture(rs.getString("culture"));
            character.setName(rs.getString("name"));
            character.setLocation(rs.getString("location"));
            character.setBirthdate(rs.getString("birthdate"));
            character.setDeathdate(rs.getDate("deathdate").toString());
            character.setAffiliation(rs.getString("affiliation"));
            character.setBackstory(rs.getString("backstory"));
            character.setImage(rs.getString("image"));
            character.setTier(rs.getString("tier"));

            character.setTraits(mapper.readValue(rs.getString("traits"), new TypeReference<List<String>>() {}));
            character.setStatistics(mapper.readValue(rs.getString("statistics"), new TypeReference<Map<String, Integer>>() {}));
            character.setAbilities(mapper.readValue(rs.getString("abilities"), new TypeReference<List<String>>() {}));

            return character;
        } catch (Exception e) {
            throw new SQLException("Failed to parse character data from result set", e);
        }
    }
}
