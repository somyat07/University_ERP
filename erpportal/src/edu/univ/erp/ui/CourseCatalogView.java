package edu.univ.erp.ui;

import edu.univ.erp.service.CourseCatalogService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseCatalogView {

    private final CourseCatalogService catalogService = new CourseCatalogService();

    public CourseCatalogView() {
        JFrame f = new JFrame("Course Catalog");
        f.setSize(900, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        //  Section Title
        JLabel titleLabel = new JLabel("Browse Course Catalog");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        //  Table Setup
        String[] columnNames = {"Course Code", "Course Title", "Credits", "Instructor"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        courseTable.setRowHeight(25);
        courseTable.setAutoCreateRowSorter(true);

        courseTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Code
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(350); // Title
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Credits
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Instructor

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBounds(20, 60, 840, 350);
        f.add(scrollPane);

        // "Close" Button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(760, 420, 100, 40);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        closeButton.addActionListener(e -> f.dispose());

        // Load Data into Table
        loadCourseData(tableModel);

        f.setVisible(true);
    }

    private void loadCourseData(DefaultTableModel model) {
        // 1. Get the list of courses (String arrays) from the service
        List<String[]> courses = catalogService.getCatalogWithInstructors();

        // 2. Loop through the list and add each row
        for (String[] row : courses) {
            model.addRow(row);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new CourseCatalogView());
//    }
}