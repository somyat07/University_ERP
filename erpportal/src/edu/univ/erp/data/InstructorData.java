package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.EnrolledCourseDisplay; // We can reuse this class
import edu.univ.erp.domain.Section;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstructorData {

    public List<EnrolledCourseDisplay> getSectionsForInstructor(int instructorId) {
        List<EnrolledCourseDisplay> assignedSections = new ArrayList<>();

        // SQL query which joins sections and courses
        String sql = "SELECT s.*, c.code, c.title, c.credits " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE s.instructor_id = ?";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);

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

                    // 3. Combine them into our display class
                    assignedSections.add(new EnrolledCourseDisplay(course, section));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching instructor sections: " + e.getMessage());
            e.printStackTrace();
        }
        return assignedSections;
    }
}
