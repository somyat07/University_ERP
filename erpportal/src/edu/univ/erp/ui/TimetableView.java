package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.StudentData;
import edu.univ.erp.domain.EnrolledCourseDisplay;
import edu.univ.erp.service.CourseCatalogService; // Added Import

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class TimetableView {

    // Backend Helpers
    private final StudentData studentData = new StudentData();
    private final AuthData authData = new AuthData();
    private final CourseCatalogService catalogService = new CourseCatalogService();

    //  UI Components
    private JFrame f;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private JButton closeButton;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JLabel messageLabel;

    //  Student Info
    private int studentId;

    public TimetableView(String studentUsername) {
        this.studentId = authData.getUserIdByUsername(studentUsername);

        f = new JFrame("My Timetable");
        f.setSize(900, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        //  Section Title
        titleLabel = new JLabel("My Weekly Timetable");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        //  Table Setup -
        String[] columnNames = {"Course Code", "Course Title (Instructor)", "Day & Time", "Room"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable = new JTable(tableModel);
        timetableTable.setFont(new Font("Arial", Font.PLAIN, 14));
        timetableTable.setRowHeight(25);
        timetableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timetableTable.setAutoCreateRowSorter(true);

        setColumnWidths();

        scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBounds(20, 60, 840, 350);
        f.add(scrollPane);

        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 420, 500, 25);
        f.add(messageLabel);

        closeButton = new JButton("Close");
        closeButton.setBounds(760, 420, 100, 40); // Moved right
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        closeButton.addActionListener(e -> f.dispose());

        if (studentId != -1) {
            loadTimetableData();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: Could not find student profile.");
        }

        f.setVisible(true);
    }

    private void setColumnWidths() {
        TableColumnModel columnModel = timetableTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(400);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(100);
    }

    private void loadTimetableData() {
        List<EnrolledCourseDisplay> enrolledCourses = studentData.getEnrolledCourseDisplayList(studentId);

        if (enrolledCourses.isEmpty()) {
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText("You are not currently enrolled in any sections.");
            return;
        }

        for (EnrolledCourseDisplay item : enrolledCourses) {
            String instructorName = catalogService.getInstructorName(item.getSection().getInstructorId());

            String titleDisplay;
            if (instructorName == null || instructorName.equalsIgnoreCase("TBA")) {
                titleDisplay = item.getCourse().getTitle();
            } else {
                titleDisplay = String.format("%s (%s)", item.getCourse().getTitle(), instructorName);
            }

            Object[] row = {
                    item.getCourse().getCode(),
                    titleDisplay,
                    item.getSection().getDayTime(),
                    item.getSection().getRoom()
            };
            tableModel.addRow(row);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new TimetableView("stu1"));
//    }}
}