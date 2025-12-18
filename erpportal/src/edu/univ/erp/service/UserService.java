package edu.univ.erp.service;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.auth.PasswordService;
import edu.univ.erp.data.AdminData;


public class UserService {

    private final AuthData authData;
    private final AdminData adminData;
    private final PasswordService passwordService;

    public UserService() {
        this.authData = new AuthData();
        this.adminData = new AdminData();
        this.passwordService = new PasswordService();
    }

    public boolean addStudent(String username, String rawPassword, String rollNo, String program, int year) {
        // 1. Hash the password
        String passwordHash = passwordService.hashPassword(rawPassword);

        // 2. Create Auth entry
        int userId = authData.createUser(username, "Student", passwordHash);

        if (userId == -1) {
            System.err.println("Failed to create Auth user for student.");
            return false;
        }

        // 3. Create ERP profile using the ID we just got
        boolean profileCreated = adminData.createStudentProfile(userId, rollNo, program, year);

        if (!profileCreated) {
            System.err.println("Created Auth user (" + userId + ") but failed to create Student profile.");
            return false;
        }

        return true;
    }

    public boolean addInstructor(String username, String rawPassword, String department) {
        // 1. Hash the password
        String passwordHash = passwordService.hashPassword(rawPassword);

        // 2. Create Auth entry
        int userId = authData.createUser(username, "Instructor", passwordHash);

        if (userId == -1) {
            System.err.println("Failed to create Auth user for instructor.");
            return false;
        }

        // 3. Create ERP profile
        boolean profileCreated = adminData.createInstructorProfile(userId, department);

        if (!profileCreated) {
            System.err.println("Created Auth user (" + userId + ") but failed to create Instructor profile.");
            return false;
        }

        return true;
    }

    public boolean changePassword(String username, String oldRawPass, String newRawPass) {
        int userId = authData.getUserIdByUsername(username);
        if (userId == -1) return false;

        // 1. Verify Old Password
        String currentHash = authData.getPasswordHash(userId);
        if (!passwordService.checkPassword(oldRawPass, currentHash)) {
            System.err.println("Change Password Failed: Old password incorrect.");
            return false;
        }

        // 2. Hash New Password
        String newHash = passwordService.hashPassword(newRawPass);

        // 3. Update DB
        return authData.updatePassword(userId, newHash);
    }

    public boolean addAdmin(String username, String rawPassword) {
        // 1. Hash the password
        String passwordHash = passwordService.hashPassword(rawPassword);

        // 2. Create Auth entry
        int userId = authData.createUser(username, "Admin", passwordHash);

        if (userId == -1) {
            System.err.println("Failed to create Auth user for Admin.");
            return false;
        }

        return true;
    }
}