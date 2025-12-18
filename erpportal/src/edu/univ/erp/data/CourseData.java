package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Handles all the databases operations for Courses and Sections.

public class CourseData {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        // SQL query to get all courses, ordered by their code
        String sql = "SELECT * FROM courses ORDER BY code";

        // try-with-resources to automatically close the connection and statement
        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Loop through every row in the result
            while (rs.next()) {
                // Create a new Course object from the row data
                Course course = new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                );
                // Add the new object to our list
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching courses: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

    public List<Section> getSectionsForCourse(int courseId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ? ORDER BY section_id";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the first parameter (?) in the SQL query to the courseId
            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a new Section object from the row data
                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getInt("capacity")
                    );
                    sections.add(section);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching sections: " + e.getMessage());
            e.printStackTrace();
        }
        return sections;
    }

    public String getInstructorName(int instructorId) {
        String sql = "SELECT username FROM auth_db.users_auth WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "TBA"; // Return "To Be Announced" if not found
    }

    public List<String[]> getCoursesWithInstructors() {
        List<String[]> catalog = new ArrayList<>();

        //  GROUP BY c.course_id: Ensures one row per course.
        // GROUP_CONCAT(...): Joins multiple instructor names into one string (e.g., "inst1, pankaj").

        String sql = "SELECT c.code, c.title, c.credits, " +
                "GROUP_CONCAT(DISTINCT u.username ORDER BY u.username SEPARATOR ', ') as instructors " +
                "FROM courses c " +
                "LEFT JOIN sections s ON c.course_id = s.course_id " +
                "LEFT JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
                "GROUP BY c.course_id, c.code, c.title, c.credits " +
                "ORDER BY c.code";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("code");
                String title = rs.getString("title");
                String credits = String.valueOf(rs.getInt("credits"));
                String instructor = rs.getString("instructors");

                // If no instructor assigned (NULL result from GROUP_CONCAT), show TBA
                if (instructor == null || instructor.isEmpty()) {
                    instructor = "TBA";
                }

                catalog.add(new String[]{code, title, credits, instructor});
            }
        } catch (SQLException e) {
            System.err.println("Error fetching catalog: " + e.getMessage());
            e.printStackTrace();
        }
        return catalog;
    }

    public Section getSectionById(int sectionId) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = DatabaseConnector.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create and return the Section object
                    return new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getInt("capacity")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching section by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if the section wasn't found
    }
}