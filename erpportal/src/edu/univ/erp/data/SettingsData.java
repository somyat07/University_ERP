package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SettingsData {

    private static final DateTimeFormatter DB_DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    // MAINTENANCE MODE

    public boolean isMaintenanceModeOn() {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_on'";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return "true".equalsIgnoreCase(rs.getString("setting_value"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean setMaintenanceMode(boolean enable) {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_on', ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = ?";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String val = String.valueOf(enable);
            stmt.setString(1, val);
            stmt.setString(2, val);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    //  ALERTS / NOTIFICATIONS

    public String getMsg() {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = 'alert'";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getString("setting_value");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean setMsg(String msg) {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES ('alert', ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = ?";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, msg);
            stmt.setString(2, msg);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    //  DEADLINES (Registration & Drop)

    public LocalDate getDeadline(String key) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dateStr = rs.getString("setting_value");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        return LocalDate.parse(dateStr, DB_DATE_FMT);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean setDeadline(String key, LocalDate date) {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = ?";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String dateStr = date.format(DB_DATE_FMT);
            stmt.setString(1, key);
            stmt.setString(2, dateStr);
            stmt.setString(3, dateStr);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}