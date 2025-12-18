package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollmentData {

    public int getCurrentEnrollmentCount(int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND status = 'enrolled'";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean createEnrollment(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() != 1062) e.printStackTrace();
            return false;
        }
    }

    public boolean dropEnrollment(int studentId, int sectionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getERPConnection();
            conn.setAutoCommit(false);

            String findSql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(findSql)) {
                selectStmt.setInt(1, studentId);
                selectStmt.setInt(2, sectionId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // Not enrolled
                    }
                    int enrollmentId = rs.getInt("enrollment_id");

                    // 2. Delete Grades
                    String delGrades = "DELETE FROM grades WHERE enrollment_id = ?";
                    try (PreparedStatement delG = conn.prepareStatement(delGrades)) {
                        delG.setInt(1, enrollmentId);
                        delG.executeUpdate();
                    }

                    // 3. Delete Enrollment
                    String delEnroll = "DELETE FROM enrollments WHERE enrollment_id = ?";
                    try (PreparedStatement delE = conn.prepareStatement(delEnroll)) {
                        delE.setInt(1, enrollmentId);
                        int rows = delE.executeUpdate();

                        conn.commit(); // Commit the transaction
                        return rows > 0;
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }
}