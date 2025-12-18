package edu.univ.erp.service;

import edu.univ.erp.data.GradeData;
import edu.univ.erp.data.StudentData;
import edu.univ.erp.domain.GradeDisplay;
import edu.univ.erp.domain.GradeStat;
import edu.univ.erp.domain.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GradingService {

    private final GradeData gradeData;
    private final MaintenanceService maintenanceService;
    private final StudentData studentData;

    public GradingService() {
        this.gradeData = new GradeData();
        this.maintenanceService = new MaintenanceService();
        this.studentData = new StudentData();
    }

    private double parseScore(String scoreStr) {
        try {
            double score = Double.parseDouble(scoreStr);
            return (score < 0) ? -1.0 : score;
        } catch (NumberFormatException e) { return -1.0; }
    }

    public String saveGrade(int studentId, int sectionId, String component, String scoreStr) {
        if (maintenanceService.isMaintenanceEnabled()) return "System is in Maintenance Mode.";
        if (component == null || component.trim().isEmpty()) return "Invalid component.";

        double score = parseScore(scoreStr);
        if (score == -1.0) return "Invalid score number.";

        if (component.contains("(") && component.contains(")")) {
            try {
                String maxStr = component.substring(component.indexOf("(") + 1, component.indexOf(")"));
                double maxScore = Double.parseDouble(maxStr);
                if (score > maxScore) return "Error: Score " + score + " exceeds max (" + maxScore + ") for " + component;
            } catch (Exception e) { }
        }

        return gradeData.saveOrUpdateGrade(studentId, sectionId, component.trim(), score) ? "Success" : "Database Error";
    }

    public String computeFinalGrade(int studentId, int sectionId) {
        if (maintenanceService.isMaintenanceEnabled()) return "Error";
        List<GradeDisplay> grades = gradeData.getGradesForEnrollment(studentId, sectionId);

        double finalScore = calculateTotalScore(grades);
        if (finalScore == -1.0) return "Error";

        String letterGrade = convertScoreToLetterGrade(finalScore);
        boolean success = gradeData.updateFinalGradeForEnrollment(studentId, sectionId, letterGrade);
        return success ? letterGrade : "Error";
    }

    private double calculateTotalScore(List<GradeDisplay> grades) {
        Map<String, Double> scoreMap = new HashMap<>();
        for (GradeDisplay grade : grades) {
            String rawComp = grade.getGradeComponent().toLowerCase();
            String simpleComp = rawComp.split("\\(")[0].trim();
            scoreMap.put(simpleComp, grade.getScore());
        }

        if (!scoreMap.containsKey("quiz") || !scoreMap.containsKey("midsem") || !scoreMap.containsKey("endsem")) {
            return -1.0;
        }
        return scoreMap.get("quiz") + scoreMap.get("midsem") + scoreMap.get("endsem");
    }

    private String convertScoreToLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public List<GradeStat> getClassStats(int sectionId) {
        List<Student> students = studentData.getEnrolledStudentsBySection(sectionId);

        // 1. Containers for data
        Map<String, List<Double>> componentScores = new TreeMap<>();
        List<Double> classTotalScores = new ArrayList<>(); // To store total score of each student

        // 2. Iterate through every student
        for (Student s : students) {
            List<GradeDisplay> grades = gradeData.getGradesForEnrollment(s.getUserId(), sectionId);

            // Normalize grades for this student (handle duplicates)
            Map<String, Double> uniqueStudentGrades = new HashMap<>();
            double studentTotal = 0; // Sum for this specific student

            for (GradeDisplay g : grades) {
                String key = g.getGradeComponent().split("\\(")[0].trim().toLowerCase();
                String displayKey = key.substring(0, 1).toUpperCase() + key.substring(1);
                uniqueStudentGrades.put(displayKey, g.getScore());
            }

            // Add components to class lists AND
            // calculate student total
            for (Map.Entry<String, Double> entry : uniqueStudentGrades.entrySet()) {
                componentScores.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
                studentTotal += entry.getValue();
            }

            // Only add total if the student has grades
            if (!uniqueStudentGrades.isEmpty()) {
                classTotalScores.add(studentTotal);
            }
        }

        // 3. Calculate Stats for Components
        List<GradeStat> finalStats = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : componentScores.entrySet()) {
            finalStats.add(calculateStatRow(entry.getKey(), entry.getValue()));
        }

        // 4. Calculate Stats for total scoree
        if (!classTotalScores.isEmpty()) {
            finalStats.add(calculateStatRow("Total Score", classTotalScores));
        }

        return finalStats;
    }

    // Helper to calculate math for a list of numbers
    private GradeStat calculateStatRow(String name, List<Double> values) {
        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (Double val : values) {
            sum += val;
            if (val > max) max = val;
            if (val < min) min = val;
        }

        double avg = (values.isEmpty()) ? 0 : (sum / values.size());
        return new GradeStat(name, avg, max, min, values.size());
    }

    public List<GradeDisplay> getGradesForEnrollment(int studentId, int sectionId) {
        return gradeData.getGradesForEnrollment(studentId, sectionId);
    }

    public boolean exportGradebookToCSV(int sectionId, java.io.File file) {
        List<edu.univ.erp.domain.Student> students = studentData.getEnrolledStudentsBySection(sectionId);

        if (students.isEmpty()) return false;

        try (com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new java.io.FileWriter(file))) {
            String[] header = {"Roll No", "Component", "Score / Grade"};
            writer.writeNext(header);

            for (edu.univ.erp.domain.Student s : students) {
                List<GradeDisplay> grades = gradeData.getGradesForEnrollment(s.getUserId(), sectionId);

                if (grades.isEmpty()) {
                    writer.writeNext(new String[]{s.getRollNo(), "(No Data)", "-"});
                    writer.writeNext(new String[]{"", "", ""});
                    continue;
                }

                Map<String, GradeDisplay> uniqueGrades = new TreeMap<>();
                String finalGrade = "N/A";

                for (GradeDisplay g : grades) {
                    if (g.getFinalGrade() != null && !g.getFinalGrade().equals("N/A")) finalGrade = g.getFinalGrade();
                    String raw = g.getGradeComponent();
                    String key = raw.split("\\(")[0].trim().toLowerCase();
                    if (uniqueGrades.containsKey(key)) {
                        if (raw.contains("(")) uniqueGrades.put(key, g);
                    } else {
                        uniqueGrades.put(key, g);
                    }
                }

                for (GradeDisplay g : uniqueGrades.values()) {
                    writer.writeNext(new String[]{s.getRollNo(), g.getGradeComponent(), String.valueOf(g.getScore())});
                }
                writer.writeNext(new String[]{s.getRollNo(), "FINAL GRADE", finalGrade});
                writer.writeNext(new String[]{"", "", ""});
            }
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}