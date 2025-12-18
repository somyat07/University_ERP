package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.InstructorData;
import edu.univ.erp.domain.EnrolledCourseDisplay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;


public class MySectionsView {

    //  Backend Helpers
    private final InstructorData instructorData = new InstructorData();
    private final AuthData authData = new AuthData();

    // UI Components
    private JFrame f;
    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private JButton closeButton;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JLabel messageLabel;

    // Instructor Info
    private int instructorId;

    public MySectionsView(String instructorUsername) {
        this.instructorId = authData.getUserIdByUsername(instructorUsername);

        f = new JFrame("My Assigned Sections");
        f.setSize(900, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        //  Section Title
        titleLabel = new JLabel("My Assigned Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        // Table Setup
        String[] columnNames = {"Course Code", "Course Title", "Section ID", "Day & Time", "Room", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sectionsTable = new JTable(tableModel);
        sectionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        sectionsTable.setRowHeight(25);

        //  Make table sortable
        sectionsTable.setAutoCreateRowSorter(true);

        setColumnWidths();

        scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBounds(20, 60, 840, 350);
        f.add(scrollPane);

        // Message Label (for errors)
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 420, 500, 25);
        f.add(messageLabel);

        // "Close" Button
        closeButton = new JButton("Close");
        closeButton.setBounds(760, 420, 100, 40); // Adjusted position
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        // Action Listeners
        closeButton.addActionListener(e -> f.dispose());

        //  Load Data into Table
        if (instructorId != -1) {
            loadAssignedSections();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: Could not find instructor profile.");
        }

        f.setVisible(true);
    }

    private void setColumnWidths() {
        TableColumnModel columnModel = sectionsTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(200);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(80);
    }

    private void loadAssignedSections() {
        // 1. Get the list of assigned sections
        List<EnrolledCourseDisplay> sections = instructorData.getSectionsForInstructor(instructorId);

        if (sections.isEmpty()) {
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText("You are not currently assigned to any sections.");
            return;
        }

        for (EnrolledCourseDisplay item : sections) {
            // 2. Add this item to the table
            Object[] row = {
                    item.getCourse().getCode(),
                    item.getCourse().getTitle(),
                    item.getSection().getSectionId(),
                    item.getSection().getDayTime(),
                    item.getSection().getRoom(),
                    item.getSection().getCapacity()
            };
            tableModel.addRow(row);
        }
    }


//    public static void main(String[] args) {
//        // Test with "inst1"
//        SwingUtilities.invokeLater(() -> new MySectionsView("inst1"));
//    }
}