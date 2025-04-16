package com.samyorBot.database;

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

    public static boolean saveCharacter(Character character) {
        String sql = """
            INSERT INTO characters (
                user_id, funds, culture, name, location, birthdate, affiliation,
                backstory, traits, statistics, abilities, image, tier
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, character.getUserId());
            stmt.setDouble(2, character.getFunds());
            stmt.setString(3, Optional.ofNullable(character.getCulture()).orElse("Not set yet"));
            stmt.setString(4, Optional.ofNullable(character.getName()).orElse("Not set yet"));
            stmt.setString(5, Optional.ofNullable(character.getLocation()).orElse("Not set yet"));
            stmt.setString(6, Optional.ofNullable(character.getBirthdate()).orElse("Not set yet"));
            stmt.setString(7, Optional.ofNullable(character.getAffiliation()).orElse("Not set yet"));
            stmt.setString(8, Optional.ofNullable(character.getBackstory()).orElse("Not set yet"));
            stmt.setString(9, mapper.writeValueAsString(
                    character.getTraits() != null ? character.getTraits() : List.of("Not set yet")));
            stmt.setString(10, mapper.writeValueAsString(
                    character.getStatistics() != null ? character.getStatistics() : Map.of("vit", 0, "int", 0, "aff", 0, "str", 0)));
            stmt.setString(11, mapper.writeValueAsString(
                    character.getAbilities() != null ? character.getAbilities() : List.of("None")));
            stmt.setString(12, Optional.ofNullable(character.getImage()).orElse("Not set yet"));
            stmt.setString(13, Optional.ofNullable(character.getTier()).orElse("Tier 1"));

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    character.setId(keys.getLong(1));
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateCharacterField(long characterId, String field, Object value) {
        List<String> allowedFields = List.of(
                "funds", "culture", "name", "location", "birthdate", "affiliation",
                "backstory", "traits", "statistics", "abilities", "image", "tier"
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
            character.setAffiliation(rs.getString("affiliation"));
            character.setBackstory(rs.getString("backstory"));
            character.setImage(rs.getString("image"));
            character.setTier(rs.getString("tier"));

            character.setTraits(mapper.readValue(rs.getString("traits"), new TypeReference<>() {}));
            character.setStatistics(mapper.readValue(rs.getString("statistics"), new TypeReference<>() {}));
            character.setAbilities(mapper.readValue(rs.getString("abilities"), new TypeReference<>() {}));

            return character;
        } catch (Exception e) {
            throw new SQLException("Failed to parse character data from result set", e);
        }
    }
}
