package com.samyorBot.database;

import com.samyorBot.classes.companies.Company;
import com.samyorBot.database.DatabaseConnection;

import java.sql.*;

public class CompanyDAO {

    public static boolean isUserInCompany(String userId) {
        String sql = "SELECT 1 FROM company_members WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static Company getCompanyByMember(String userId) {
        String sql = """
        SELECT c.id, c.name, c.owner_id
        FROM companies c
        JOIN company_members m ON c.id = m.company_id
        WHERE m.user_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Company company = new Company();
                company.setId(rs.getString("id"));
                company.setName(rs.getString("name"));
                company.setOwnerId(rs.getString("owner_id"));
                return company;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean doesCompanyExist(String name) {
        String sql = "SELECT 1 FROM companies WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static int createCompany(String name, String ownerId) {
        String sql = "INSERT INTO companies (name, owner_id, funds, building_ids) VALUES (?, ?, 0, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, ownerId);
            stmt.setString(3, "[]"); // Empty building list as JSON
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void addUserToCompany(String userId, int companyId, String role) {
        String sql = "INSERT INTO company_members (user_id, company_id, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setInt(2, companyId);
            stmt.setString(3, role);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCompany(int companyId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Begin transaction (optional, for safety)
            conn.setAutoCommit(false);

            // Delete all members of the company (this may be handled automatically by ON DELETE CASCADE)
            String deleteMembersSql = "DELETE FROM company_members WHERE company_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteMembersSql)) {
                stmt.setInt(1, companyId);
                stmt.executeUpdate();
            }

            // Delete the company itself
            String deleteCompanySql = "DELETE FROM companies WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteCompanySql)) {
                stmt.setInt(1, companyId);
                stmt.executeUpdate();
            }

            // Commit transaction
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteCompanyIfOwner(String userId) {
        String getCompanySql = "SELECT id FROM companies WHERE owner_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getCompanySql)) {

            getStmt.setString(1, userId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                int companyId = rs.getInt("id");

                String deleteSql = "DELETE FROM companies WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, companyId);
                    deleteStmt.executeUpdate();
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // User is not an owner or deletion failed
    }
}
