package edu.univ.erp.domain;

public class Course {

    private int courseId;
    private String code;
    private String title;
    private int credits;

    public Course(int courseId, String code, String title, int credits) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    //  Getters
    // We only need getters for now, as the UI won't be changing course info.

    public int getCourseId() {
        return courseId;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }
}