package edu.univ.erp.data;

import edu.univ.erp.domain.GradeDisplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeData {

    public List<GradeDisplay> getGradesForStudent(int studentId) {
        List<GradeDisplay> grades = new ArrayList<>();

        // This SQL query joins 4 tables to get all the info we need
        String sql = "SELECT c.code, c.title, g.component, g.score, g.final_grade " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY c.code, g.component";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create the new GradeDisplay object from the row
                    GradeDisplay grade = new GradeDisplay(
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("component"),
                            rs.getDouble("score"),
                            rs.getString("final_grade")
                    );
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching student grades: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }

    public boolean saveOrUpdateGrade(int studentId, int sectionId, String component, double score) {

        String sql = "INSERT INTO grades (enrollment_id, component, score) " +
                "SELECT enrollment_id, ?, ? FROM enrollments " +
                "WHERE student_id = ? AND section_id = ? " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score)";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the parameters for the query
            stmt.setString(1, component);    // component
            stmt.setDouble(2, score);      // score
            stmt.setInt(3, studentId);     // student_id
            stmt.setInt(4, sectionId);     // section_id

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error saving grade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<GradeDisplay> getGradesForEnrollment(int studentId, int sectionId) {
        List<GradeDisplay> grades = new ArrayList<>();

        String sql = "SELECT c.code, c.title, g.component, g.score, g.final_grade " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? AND e.section_id = ? " +
                "ORDER BY g.component";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new GradeDisplay(
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("component"),
                            rs.getDouble("score"),
                            rs.getString("final_grade")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching student grades for enrollment: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }

    public boolean updateFinalGradeForEnrollment(int studentId, int sectionId, String finalGrade) {

        String sql = "UPDATE grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "SET g.final_grade = ? " +
                "WHERE e.student_id = ? AND e.section_id = ?";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, finalGrade);
            stmt.setInt(2, studentId);
            stmt.setInt(3, sectionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if any grade was updated

        } catch (SQLException e) {
            System.err.println("Database error updating final grade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<edu.univ.erp.domain.GradeStat> getStatsForSection(int sectionId) {
        java.util.List<edu.univ.erp.domain.GradeStat> stats = new java.util.ArrayList<>();

        String sql = "SELECT component, AVG(score), MAX(score), MIN(score), COUNT(*) " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "WHERE e.section_id = ? " +
                "GROUP BY component";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(new edu.univ.erp.domain.GradeStat(
                            rs.getString(1), // component
                            rs.getDouble(2), // AVG
                            rs.getDouble(3), // MAX
                            rs.getDouble(4), // MIN
                            rs.getInt(5)     // COUNT
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching stats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }
}
