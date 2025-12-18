package edu.univ.erp.service;

import com.opencsv.CSVWriter;
import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.GradeData;
import edu.univ.erp.domain.GradeDisplay;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TranscriptService {

    private final GradeData gradeData = new GradeData();
    private final AuthData authData = new AuthData();

    public boolean generateTranscript(String studentUsername, File file) {

        // 1. Get Student ID
        int studentId = authData.getUserIdByUsername(studentUsername);
        if (studentId == -1) {
            System.err.println("Cannot generate transcript: Student not found.");
            return false;
        }

        // 2. Fetch all grades
        List<GradeDisplay> allGrades = gradeData.getGradesForStudent(studentId);

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {

            // HEADER
            String[] header = {"Course Code", "Course Title", "Component", "Score", "Final Grade"};
            writer.writeNext(header);

            if (allGrades.isEmpty()) {
                return true;
            }

            // 3. Group Grades by Course
            Map<String, List<GradeDisplay>> courseGroups = new LinkedHashMap<>();
            for (GradeDisplay g : allGrades) {
                String key = g.getCourseCode() + ": " + g.getCourseTitle();
                courseGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(g);
            }

            // 4. Process Each Course
            for (Map.Entry<String, List<GradeDisplay>> entry : courseGroups.entrySet()) {
                List<GradeDisplay> courseGrades = entry.getValue();

                // Get Course Info (Common for all grades in this group)
                String courseCode = courseGrades.get(0).getCourseCode();
                String courseTitle = courseGrades.get(0).getCourseTitle();

                // Remove duplicates
                Map<String, GradeDisplay> uniqueComponents = new TreeMap<>();
                String finalGrade = "N/A";

                for (GradeDisplay g : courseGrades) {
                    if (g.getFinalGrade() != null && !g.getFinalGrade().equals("N/A")) {
                        finalGrade = g.getFinalGrade();
                    }

                    String raw = g.getGradeComponent();
                    String cleanName = raw.split("\\(")[0].trim().toLowerCase();

                    if (uniqueComponents.containsKey(cleanName)) {
                        if (raw.contains("(")) {
                            uniqueComponents.put(cleanName, g);
                        }
                    } else {
                        uniqueComponents.put(cleanName, g);
                    }
                }

                //  WRITE ROWS
                boolean isFirstRow = true; // Flag to print Course Info only once

                for (GradeDisplay g : uniqueComponents.values()) {
                    writer.writeNext(new String[]{
                            isFirstRow ? courseCode : "",  // Write Code only if first row
                            isFirstRow ? courseTitle : "", // Write Title only if first row
                            g.getGradeComponent(),
                            String.valueOf(g.getScore()),
                            "" // Empty Final Grade cell for components
                    });

                    isFirstRow = false; // Set to false for subsequent rows
                }

                // B. Write Summary Row
                writer.writeNext(new String[]{
                        "", // Empty Code
                        "", // Empty Title
                        "FINAL GRADE",
                        "",
                        finalGrade
                });

                // C. Spacer Rows.
                writer.writeNext(new String[]{"", "", "", "", ""});
            }

            return true;

        } catch (IOException e) {
            System.err.println("Error writing transcript CSV file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}