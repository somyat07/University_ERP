package edu.univ.erp.service;

import edu.univ.erp.data.AdminData;
import edu.univ.erp.domain.Instructor;
import java.util.List;

public class CourseManagementService {

    private final AdminData adminData;

    public CourseManagementService() {
        this.adminData = new AdminData();
    }

    public boolean createNewCourse(String code, String title, String creditsStr) {
        if (code == null || code.trim().isEmpty()) return false;
        try {
            int credits = Integer.parseInt(creditsStr);
            if (credits <= 0) return false;
            return adminData.createCourse(code.trim(), title.trim(), credits);
        } catch (NumberFormatException e) { return false; }
    }

    public boolean updateCourse(int courseId, String code, String title, String creditsStr) {
        if (code == null || code.trim().isEmpty()) return false;
        try {
            int credits = Integer.parseInt(creditsStr);
            if (credits <= 0) return false;
            return adminData.updateCourse(courseId, code.trim(), title.trim(), credits);
        } catch (NumberFormatException e) { return false; }
    }

    public List<Instructor> getAllInstructors() {
        return adminData.getAllInstructors();
    }

    public boolean createNewSection(int courseId, int instructorId, String dayTime, String room, String capacityStr) {
        if (dayTime == null || dayTime.trim().isEmpty()) return false;
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) return false;
            return adminData.createSection(courseId, instructorId, dayTime.trim(), room.trim(), capacity);
        } catch (NumberFormatException e) { return false; }
    }

    public boolean updateSection(int sectionId, int instructorId, String dayTime, String room, String capacityStr) {
        if (dayTime == null || dayTime.trim().isEmpty()) return false;
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) return false;
            return adminData.updateSection(sectionId, instructorId, dayTime.trim(), room.trim(), capacity);
        } catch (NumberFormatException e) { return false; }
    }
}