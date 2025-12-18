package edu.univ.erp.ui;

import edu.univ.erp.service.UserService;

import javax.swing.*;
import java.awt.*;

public class AddUserView {

    private final UserService userService = new UserService();

    private JFrame f;
    private JComboBox<String> roleComboBox;
    private JTextField userText, passText;
    private JTextField rollText, programText, yearText;
    private JTextField deptText; // Instructor field
    private JLabel rollLabel, programLabel, yearLabel, deptLabel; // Labels to toggle
    private JButton addButton, closeButton;

    public AddUserView() {
        f = new JFrame("Add New User");
        f.setSize(600, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Add New User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 200, 25);
        f.add(titleLabel);

        // Role Selection
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(20, 60, 80, 25);
        f.add(roleLabel);

        // "Admin" to the list
        String[] roles = {"Student", "Instructor", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(120, 60, 200, 25);
        f.add(roleComboBox);

        // Common Credentials
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 100, 80, 25);
        f.add(userLabel);

        userText = new JTextField();
        userText.setBounds(120, 100, 200, 25);
        f.add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 140, 80, 25);
        f.add(passLabel);

        passText = new JTextField();
        passText.setBounds(120, 140, 200, 25);
        f.add(passText);

        // separator line
        JSeparator sep = new JSeparator();
        sep.setBounds(20, 180, 540, 2);
        f.add(sep);

        // Student Specific Fields
        rollLabel = new JLabel("Roll No:");
        rollLabel.setBounds(20, 200, 80, 25);
        f.add(rollLabel);

        rollText = new JTextField();
        rollText.setBounds(120, 200, 200, 25);
        f.add(rollText);

        programLabel = new JLabel("Program:");
        programLabel.setBounds(20, 240, 80, 25);
        f.add(programLabel);

        programText = new JTextField();
        programText.setBounds(120, 240, 200, 25);
        f.add(programText);

        yearLabel = new JLabel("Year:");
        yearLabel.setBounds(20, 280, 80, 25);
        f.add(yearLabel);

        yearText = new JTextField();
        yearText.setBounds(120, 280, 200, 25);
        f.add(yearText);

        //  Instructor Specific Fields (Hidden by default)
        deptLabel = new JLabel("Department:");
        deptLabel.setBounds(20, 200, 100, 25);
        deptLabel.setVisible(false);
        f.add(deptLabel);

        deptText = new JTextField();
        deptText.setBounds(120, 200, 200, 25);
        deptText.setVisible(false);
        f.add(deptText);

        //  Buttons
        addButton = new JButton("Add User");
        addButton.setBounds(350, 400, 200, 40);
        f.add(addButton);

        closeButton = new JButton("Close");
        closeButton.setBounds(230, 400, 100, 40);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        // Toggle fields when role changes
        roleComboBox.addActionListener(e -> updateFieldsVisibility());

        closeButton.addActionListener(e -> f.dispose());
        addButton.addActionListener(e -> handleAddUser());

        f.setVisible(true);
    }

    private void updateFieldsVisibility() {
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if ("Student".equals(selectedRole)) {
            // Show Student, Hide others
            showStudentFields(true);
            showInstructorFields(false);
        } else if ("Instructor".equals(selectedRole)) {
            // Show Instructor, Hide others
            showStudentFields(false);
            showInstructorFields(true);
        } else {
            // Admin: Hide everything except login
            showStudentFields(false);
            showInstructorFields(false);
        }
    }

    private void showStudentFields(boolean show) {
        rollLabel.setVisible(show);
        rollText.setVisible(show);
        programLabel.setVisible(show);
        programText.setVisible(show);
        yearLabel.setVisible(show);
        yearText.setVisible(show);
    }

    private void showInstructorFields(boolean show) {
        deptLabel.setVisible(show);
        deptText.setVisible(show);
    }

    private void handleAddUser() {
        String username = userText.getText();
        String password = passText.getText();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(f, "Username and Password are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = false;

        if ("Student".equals(role)) {
            String roll = rollText.getText();
            String prog = programText.getText();
            String yearStr = yearText.getText();

            if (roll.isEmpty() || prog.isEmpty() || yearStr.isEmpty()) {
                JOptionPane.showMessageDialog(f, "All Student fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int year = Integer.parseInt(yearStr);
                success = userService.addStudent(username, password, roll, prog, year);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(f, "Year must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } else if ("Instructor".equals(role)) {
            String dept = deptText.getText();
            if (dept.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Department is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            success = userService.addInstructor(username, password, dept);

        } else if ("Admin".equals(role)) {
            //  Handle Admin creation
            success = userService.addAdmin(username, password);
        }

        if (success) {
            JOptionPane.showMessageDialog(f, "User '" + username + "' (" + role + ") added successfully!");
            f.dispose();
        } else {
            JOptionPane.showMessageDialog(f, "Failed to add user. Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(AddUserView::new);
//    }
}