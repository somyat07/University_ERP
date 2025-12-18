package edu.univ.erp.service;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.EnrollmentData;

public class DropService {

    private final AuthData authData;
    private final EnrollmentData enrollmentData;
    private final MaintenanceService maintenanceService;

    public DropService() {
        this.authData = new AuthData();
        this.enrollmentData = new EnrollmentData();
        this.maintenanceService = new MaintenanceService();
    }

    public DropResult dropStudent(String studentUsername, int sectionId) {

        if (maintenanceService.isMaintenanceEnabled()) {
            return DropResult.MAINTENANCE_ON;
        }
        edu.univ.erp.data.SettingsData settings = new edu.univ.erp.data.SettingsData();
        java.time.LocalDate deadline = settings.getDeadline("drop_deadline");
        if (deadline != null && java.time.LocalDate.now().isAfter(deadline)) {
            return DropResult.DEADLINE_PASSED;
        }

        int studentId = authData.getUserIdByUsername(studentUsername);
        if (studentId == -1) {
            return DropResult.STUDENT_NOT_FOUND;
        }

        boolean success = enrollmentData.dropEnrollment(studentId, sectionId);

        if (success) {
            return DropResult.SUCCESS;
        } else {
            return DropResult.NOT_ENROLLED;
        }
    }
}