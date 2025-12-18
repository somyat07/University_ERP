package edu.univ.erp.ui;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseCatalogService;
import edu.univ.erp.service.RegistrationResult;
import edu.univ.erp.service.RegistrationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class RegistrationView {

    //  Backend Services
    private final RegistrationService registrationService = new RegistrationService();
    private final CourseCatalogService catalogService = new CourseCatalogService();

    //  UI Components
    private JFrame f;
    private JTable sectionTable;
    private DefaultTableModel tableModel;
    private JButton registerButton;
    private JButton closeButton;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JLabel messageLabel;

    // Student Info
    private String studentUsername;

    public RegistrationView(String studentUsername) {
        this.studentUsername = studentUsername;

        f = new JFrame("Register for a Section");
        f.setSize(1000, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        //  Section Title
        titleLabel = new JLabel("Available Course Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        //  Table Setup
        String[] columnNames = {"Course (Instructor)", "Section ID", "Time", "Room", "Enrolled / Cap"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sectionTable = new JTable(tableModel);
        sectionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        sectionTable.setRowHeight(25);
        sectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionTable.setAutoCreateRowSorter(true); // Sorting enabled

        // Set Column Widths
        setColumnWidths();

        scrollPane = new JScrollPane(sectionTable);
        scrollPane.setBounds(20, 60, 940, 300);
        f.add(scrollPane);

        // Message Label
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 370, 600, 25);
        f.add(messageLabel);

        // "Register" Button
        registerButton = new JButton("Register Selected Section");
        registerButton.setBounds(600, 420, 220, 40);
        f.add(registerButton);

        // "Close" Button
        closeButton = new JButton("Close");
        closeButton.setBounds(480, 420, 100, 40);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        //  Action Listeners
        closeButton.addActionListener(e -> f.dispose());
        registerButton.addActionListener(e -> handleRegistration());

        // Load Data
        loadSectionData();

        f.setVisible(true);
    }

    private void setColumnWidths() {
        TableColumnModel columnModel = sectionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(400);
        columnModel.getColumn(1).setPreferredWidth(70);
        columnModel.getColumn(2).setPreferredWidth(180);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(150);
    }

    private void loadSectionData() {
        List<Course> courses = catalogService.getAllCourses();

        for (Course course : courses) {
            List<Section> sections = catalogService.getSectionsForCourse(course.getCourseId());

            for (Section section : sections) {
                String instructorName = catalogService.getInstructorName(section.getInstructorId());

                if (instructorName == null || instructorName.equalsIgnoreCase("TBA")) {
                    continue;
                }

                int totalCap = section.getCapacity();
                int currentCount = catalogService.getCurrentEnrollment(section.getSectionId());

                String availabilityDisplay = currentCount + " / " + totalCap;

                if (currentCount >= totalCap) {
                    availabilityDisplay += " (FULL)";
                }

                String displayString = String.format("%s: %s (%s)", course.getCode(), course.getTitle(), instructorName);

                Object[] row = {
                        displayString,
                        section.getSectionId(),
                        section.getDayTime(),
                        section.getRoom(),
                        availabilityDisplay
                };
                tableModel.addRow(row);
            }
        }
    }

    private void handleRegistration() {
        int selectedRow = sectionTable.getSelectedRow();

        if (selectedRow == -1) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please select a section from the table first.");
            return;
        }

        // Get Section ID from Column 1
        Object idObj = sectionTable.getValueAt(selectedRow, 1);
        int sectionId = Integer.parseInt(idObj.toString());

        RegistrationResult result = registrationService.registerStudent(studentUsername, sectionId);

        switch (result) {
            case SUCCESS:
                messageLabel.setForeground(new Color(0, 150, 0));
                messageLabel.setText("Success! You are registered for section " + sectionId + ".");

                tableModel.setRowCount(0);
                loadSectionData();
                break;

            case SECTION_FULL:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Registration failed: This section is full.");


                JOptionPane.showMessageDialog(f,
                        "Registration Failed:\nThis section has reached its maximum capacity.",
                        "Section Full",
                        JOptionPane.ERROR_MESSAGE);

                tableModel.setRowCount(0);
                loadSectionData();
                break;

            case ALREADY_REGISTERED:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Registration failed: You are already registered for this section.");
                JOptionPane.showMessageDialog(f, "You are already registered for this section.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                break;

            case MAINTENANCE_ON:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Blocked: System is in Maintenance Mode.");
                JOptionPane.showMessageDialog(f,
                        "System is under maintenance.\nRegistrations are currently disabled.",
                        "Blocked", JOptionPane.WARNING_MESSAGE);
                break;

            case DEADLINE_PASSED:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Registration failed: Deadline passed.");
                JOptionPane.showMessageDialog(f,
                        "The deadline for course registration has passed.\nYou cannot enroll in new courses.",
                        "Deadline Passed", JOptionPane.ERROR_MESSAGE);
                break;

            default:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Registration failed: An unknown error occurred.");
                break;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new RegistrationView("stu1"));
//    }
}