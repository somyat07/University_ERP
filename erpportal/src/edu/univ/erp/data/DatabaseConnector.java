package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    // Connection details for the Auth DB
    private static final String AUTH_DB_URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String DB_USER = "root"; // Or your MySQL username
    private static final String DB_PASSWORD = "somya123"; // MySQL password !!

    // Connection details for the ERP DB
    private static final String ERP_DB_URL = "jdbc:mysql://localhost:3306/erp_db";
    // We can use the same username and password for both databases

    public static Connection getAuthConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }
        return DriverManager.getConnection(AUTH_DB_URL, DB_USER, DB_PASSWORD);
    }

    public static Connection getERPConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }
        return DriverManager.getConnection(ERP_DB_URL, DB_USER, DB_PASSWORD);
    }

    // A simple test method
    public static void main(String[] args) {
        try (Connection authConn = getAuthConnection();
             Connection erpConn = getERPConnection()) {

            if (authConn != null && !authConn.isClosed()) {
                System.out.println("Successfully connected to Auth DB!");
            }
            if (erpConn != null && !erpConn.isClosed()) {
                System.out.println("Successfully connected to ERP DB!");
            }
        } catch (SQLException e) {
            System.err.println("Connection Failed!");
            e.printStackTrace();
        }
    }
}
