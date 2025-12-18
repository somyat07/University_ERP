package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminData {

    public boolean createStudentProfile(int userId, String rollNo, String program, int year) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, rollNo);
            stmt.setString(3, program);
            stmt.setInt(4, year);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean createInstructorProfile(int userId, String department) {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, department);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // COURSE MANAGEMENT

    public boolean createCourse(String code, String title, int credits) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateCourse(int courseId, String code, String title, int credits) {
        String sql = "UPDATE courses SET code = ?, title = ?, credits = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            stmt.setInt(4, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    //  SECTION MANAGEMENT

    public boolean createSection(int courseId, int instructorId, String dayTime, String room, int capacity) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, 'Fall', 2025)";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayTime);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateSection(int sectionId, int instructorId, String dayTime, String room, int capacity) {
        String sql = "UPDATE sections SET instructor_id = ?, day_time = ?, room = ?, capacity = ? WHERE section_id = ?";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            stmt.setString(2, dayTime);
            stmt.setString(3, room);
            stmt.setInt(4, capacity);
            stmt.setInt(5, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // DATA FETCHING

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT i.user_id, u.username, i.department FROM instructors i JOIN auth_db.users_auth u ON i.user_id = u.user_id";
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Instructor(rs.getInt("user_id"), rs.getString("username"), rs.getString("department")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

     // Fetches Omly the Username and Role for the admin list. Restored method for ViewUsers.
    public List<String[]> getAllSystemUsers() {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT username, role FROM auth_db.users_auth ORDER BY role, username";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String user = rs.getString("username");
                String role = rs.getString("role");
                users.add(new String[]{user, role});
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user list: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
}