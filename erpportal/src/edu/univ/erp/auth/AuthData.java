package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthData {

    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection(); // Connects to Auth DB
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 to indicate user was not found
    }

    public int createUser(String username, String role, String passwordHash) {
        String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";

        // We need Statement.RETURN_GENERATED_KEYS to get the new ID back
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, passwordHash);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the new user_id
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";

        try (java.sql.Connection conn = edu.univ.erp.data.DatabaseConnector.getAuthConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (java.sql.SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Gets the current password hash for a user (to verify old password).

    public String getPasswordHash(int userId) {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id = ?";
        try (java.sql.Connection conn = edu.univ.erp.data.DatabaseConnector.getAuthConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("password_hash");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}