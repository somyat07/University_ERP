package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.StudentData;
import edu.univ.erp.domain.EnrolledCourseDisplay;
import edu.univ.erp.service.DropResult;
import edu.univ.erp.service.DropService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class DropCourseView {

    //  Backend Services
    private final DropService dropService = new DropService();
    private final StudentData studentData = new StudentData();
    private final AuthData authData = new AuthData();

    // UI Components
    private JFrame f;
    private JTable enrolledTable;
    private DefaultTableModel tableModel;
    private JButton dropButton;
    private JButton closeButton;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JLabel messageLabel;

    //  Student Info
    private String studentUsername;
    private int studentId;

    public DropCourseView(String studentUsername) {
        this.studentUsername = studentUsername;
        this.studentId = authData.getUserIdByUsername(studentUsername);

        f = new JFrame("Drop a Course");
        f.setSize(900, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        // Section Title
        titleLabel = new JLabel("My Enrolled Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        // Table Setup
        String[] columnNames = {"Course", "Section ID", "Time", "Room"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrolledTable = new JTable(tableModel);
        enrolledTable.setFont(new Font("Arial", Font.PLAIN, 14));
        enrolledTable.setRowHeight(25);
        enrolledTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrolledTable.setAutoCreateRowSorter(true);

        // Set Column Widths
        setColumnWidths();

        scrollPane = new JScrollPane(enrolledTable);
        scrollPane.setBounds(20, 60, 840, 300);
        f.add(scrollPane);

        // Message Label
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 370, 500, 25);
        f.add(messageLabel);

        // "Drop" Button
        dropButton = new JButton("Drop Selected Section");
        dropButton.setBounds(540, 420, 220, 40);
        f.add(dropButton);

        // "Close" Button
        closeButton = new JButton("Close");
        closeButton.setBounds(420, 420, 100, 40);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        // Action Listeners
        closeButton.addActionListener(e -> f.dispose());
        dropButton.addActionListener(e -> handleDrop());

        // Load Data
        if (studentId != -1) {
            loadEnrolledCourses();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: Could not find student profile.");
        }

        f.setVisible(true);
    }

    private void setColumnWidths() {
        TableColumnModel columnModel = enrolledTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(400);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(80);
    }

    private void loadEnrolledCourses() {
        List<EnrolledCourseDisplay> enrolledCourses = studentData.getEnrolledCourseDisplayList(studentId);

        for (EnrolledCourseDisplay item : enrolledCourses) {
            Object[] row = {
                    item.getCourse().getCode() + ": " + item.getCourse().getTitle(),
                    item.getSection().getSectionId(),
                    item.getSection().getDayTime(),
                    item.getSection().getRoom()
            };
            tableModel.addRow(row);
        }
    }

    private void handleDrop() {
        int selectedRow = enrolledTable.getSelectedRow();

        if (selectedRow == -1) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please select a section to drop.");
            return;
        }

        // Get ID from the view (handles sorting correctly)
        Object idObj = enrolledTable.getValueAt(selectedRow, 1);
        int sectionId = Integer.parseInt(idObj.toString());

        int confirm = JOptionPane.showConfirmDialog(f,
                "Are you sure you want to drop Section " + sectionId + "?",
                "Confirm Drop", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DropResult result = dropService.dropStudent(studentUsername, sectionId);

        switch (result) {
            case SUCCESS:
                messageLabel.setForeground(new Color(0, 150, 0));
                messageLabel.setText("Success! You have been dropped from section " + sectionId + ".");
                int modelRow = enrolledTable.convertRowIndexToModel(selectedRow);
                tableModel.removeRow(modelRow);
                break;
            case NOT_ENROLLED:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Error: You are not enrolled in this section.");
                break;
            case MAINTENANCE_ON:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Blocked: System is in Maintenance Mode.");
                JOptionPane.showMessageDialog(f,
                        "System is under maintenance.\nDropping courses is currently disabled.",
                        "Blocked", JOptionPane.WARNING_MESSAGE);
                break;

            case DEADLINE_PASSED:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Drop failed: Deadline passed.");
                JOptionPane.showMessageDialog(f,
                        "The deadline for dropping courses has passed.\nYou cannot drop this course.",
                        "Deadline Passed", JOptionPane.ERROR_MESSAGE);
                break;

            default:
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Error: An unknown error occurred.");
                break;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new DropCourseView("stu1"));
//    }
}