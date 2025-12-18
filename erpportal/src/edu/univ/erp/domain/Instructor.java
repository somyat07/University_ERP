package edu.univ.erp.domain;

public class Instructor {
    private int userId;
    private String username; // Joined from users_auth
    private String department;

    public Instructor(int userId, String username, String department) {
        this.userId = userId;
        this.username = username;
        this.department = department;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return username + " (" + department + ")";
    }
}
