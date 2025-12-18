package edu.univ.erp.domain;

public class GradeDisplay {

    private final String courseCode;
    private final String courseTitle;
    private final String gradeComponent; // e.g., "quiz", "midterm"
    private final double score;
    private final String finalGrade; // e.g., "A", "B+"

    public GradeDisplay(String courseCode, String courseTitle, String gradeComponent, double score, String finalGrade) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.gradeComponent = gradeComponent;
        this.score = score;
        this.finalGrade = finalGrade;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getGradeComponent() {
        return gradeComponent;
    }

    public double getScore() {
        return score;
    }

    public String getFinalGrade() {
        // Return "N/A" if the final grade isn't set yet
        return (finalGrade != null) ? finalGrade : "N/A";
    }
}
