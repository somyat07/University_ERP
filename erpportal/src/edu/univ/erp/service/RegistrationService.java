package edu.univ.erp.service;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.CourseData;
import edu.univ.erp.data.EnrollmentData;
import edu.univ.erp.data.SettingsData; // <--- ADDED THIS IMPORT
import edu.univ.erp.domain.Section;

public class RegistrationService {

    private final AuthData authData;
    private final CourseData courseData;
    private final EnrollmentData enrollmentData;
    private final MaintenanceService maintenanceService;

    public RegistrationService() {
        this.authData = new AuthData();
        this.courseData = new CourseData();
        this.enrollmentData = new EnrollmentData();
        this.maintenanceService = new MaintenanceService();
    }

    public RegistrationResult registerStudent(String studentUsername, int sectionId) {

        //  Check Maintenance Mode
        if (maintenanceService.isMaintenanceEnabled()) {
            return RegistrationResult.MAINTENANCE_ON;
        }

        // Check Registration Deadline
        SettingsData settings = new SettingsData();
        java.time.LocalDate deadline = settings.getDeadline("registration_deadline");
        if (deadline != null && java.time.LocalDate.now().isAfter(deadline)) {
            return RegistrationResult.DEADLINE_PASSED;
        }

        // Find the Student's ID
        int studentId = authData.getUserIdByUsername(studentUsername);
        if (studentId == -1) {
            return RegistrationResult.STUDENT_NOT_FOUND;
        }

        // Find the Section and its capacity
        Section section = courseData.getSectionById(sectionId);
        if (section == null) {
            return RegistrationResult.SECTION_NOT_FOUND;
        }
        int capacity = section.getCapacity();

        // Check if the section is full
        int currentCount = enrollmentData.getCurrentEnrollmentCount(sectionId);
        if (currentCount >= capacity) {
            return RegistrationResult.SECTION_FULL;
        }

        // Try to enroll the student
        boolean success = enrollmentData.createEnrollment(studentId, sectionId);

        if (success) {
            return RegistrationResult.SUCCESS;
        } else {
            return RegistrationResult.ALREADY_REGISTERED;
        }
    }
}