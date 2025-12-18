package edu.univ.erp.service;

import edu.univ.erp.data.CourseData;
import edu.univ.erp.data.EnrollmentData; // <--- IMPORT THIS
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

import java.util.List;

public class CourseCatalogService {

    private final CourseData coursedata;
    private final EnrollmentData enrollmentData;

    public CourseCatalogService() {
        this.coursedata = new CourseData();
        this.enrollmentData = new EnrollmentData();
    }

    public List<Course> getAllCourses() {
        return coursedata.getAllCourses();
    }

    public List<Section> getSectionsForCourse(int courseId) {
        return coursedata.getSectionsForCourse(courseId);
    }

    public String getInstructorName(int instructorId) {
        return coursedata.getInstructorName(instructorId);
    }

    public List<String[]> getCatalogWithInstructors() {
        return coursedata.getCoursesWithInstructors();
    }

    public Section getSectionById(int sectionId) {
        return coursedata.getSectionById(sectionId);
    }

    public int getCurrentEnrollment(int sectionId) {
        return enrollmentData.getCurrentEnrollmentCount(sectionId);
    }
}