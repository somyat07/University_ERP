package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.EnrolledCourseDisplay; // <-- IMPORT new class
import edu.univ.erp.domain.Section;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.domain.Student; // <-- ADD THIS LINE

public class StudentData {

    public List<EnrolledCourseDisplay> getEnrolledCourseDisplayList(int studentId) {
        List<EnrolledCourseDisplay> enrolledCourses = new ArrayList<>();

        // This SQL query joins 3 tables:
        // 1. enrollments (to find the student)
        // 2. sections (to get section details)
        // 3. courses (to get course details)
        String sql = "SELECT s.*, c.code, c.title, c.credits " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? AND e.status = 'enrolled'";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    // 1. Create the Course object from the row
                    Course course = new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getInt("credits")
                    );

                    // 2. Create the Section object from the row
                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getInt("capacity")
                    );

                    // 3. Combine them into our new display class
                    enrolledCourses.add(new EnrolledCourseDisplay(course, section));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching enrolled sections: " + e.getMessage());
            e.printStackTrace();
        }
        return enrolledCourses;
    }

    public List<Student> getEnrolledStudentsBySection(int sectionId) {
        List<Student> studentList = new ArrayList<>();

        // This query joins students and enrollments
        String sql = "SELECT s.* " +
                "FROM students s " +
                "JOIN enrollments e ON s.user_id = e.student_id " +
                "WHERE e.section_id = ? AND e.status = 'enrolled' " +
                "ORDER BY s.roll_no";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a Student object from the row data
                    Student student = new Student(
                            rs.getInt("user_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
                    studentList.add(student);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching enrolled students: " + e.getMessage());
            e.printStackTrace();
        }
        return studentList;
    }
}