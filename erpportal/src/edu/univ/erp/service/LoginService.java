package edu.univ.erp.service;

import edu.univ.erp.auth.PasswordService;
import edu.univ.erp.data.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {

    private final PasswordService passwordService = new PasswordService();
    private static final int MAX_ATTEMPTS = 3; // Lock after 3 tries

    public String login(String username, String password) {
        String sql = "SELECT user_id, role, password_hash, status, failed_attempts FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String status = rs.getString("status");
                    int failures = rs.getInt("failed_attempts");
                    String storedHash = rs.getString("password_hash").trim();
                    String role = rs.getString("role");

                    // 1. Check if already locked
                    if ("locked".equalsIgnoreCase(status)) {
                        System.out.println("Login blocked: Account is locked.");
                        return "LOCKED"; // Special signal to UI
                    }

                    // 2. Verify a Password
                    if (passwordService.checkPassword(password, storedHash)) {
                        // Success! Reset failures to 0
                        resetFailures(userId);
                        return role;
                    } else {
                        // if failed then Increment counter
                        failures++;
                        System.out.println("Login failed. Attempt " + failures + " of " + MAX_ATTEMPTS);

                        if (failures >= MAX_ATTEMPTS) {
                            lockAccount(userId);
                            return "LOCKED_NOW"; // User just got locked
                        } else {
                            incrementFailure(userId, failures);
                            return null;
                        }
                    }
                } else {
                    System.out.println("Login failed: User not found.");
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //  Helper Methods for Database Updates

    private void incrementFailure(int userId, int newCount) {
        updateUserColumn(userId, "failed_attempts", String.valueOf(newCount));
    }

    private void resetFailures(int userId) {
        // We reset failed_attempts to 0 AND update last_login timestamp
        String sql = "UPDATE users_auth SET failed_attempts = 0, last_login = NOW() WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void lockAccount(int userId) {
        // Set status to 'locked'
        String sql = "UPDATE users_auth SET status = 'locked' WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Generic helper to avoid repeating code
    private void updateUserColumn(int userId, String column, String value) {
        String sql = "UPDATE users_auth SET " + column + " = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // failed_attempts is INT, status is STRING.
            // For simplicity in this helper, we'll just use hardcoded queries in the specific methods above.
            // But for incrementFailure:
            stmt.setInt(1, Integer.parseInt(value));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}