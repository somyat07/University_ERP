package edu.univ.erp.domain;


public class EnrolledCourseDisplay {

    private final Course course;
    private final Section section;

    public EnrolledCourseDisplay(Course course, Section section) {
        this.course = course;
        this.section = section;
    }

    // Getters so the UI can retrieve the objects
    public Course getCourse() {
        return course;
    }

    public Section getSection() {
        return section;
    }
}