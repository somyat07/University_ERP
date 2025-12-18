package edu.univ.erp.ui;

import edu.univ.erp.domain.Course;
import edu.univ.erp.service.CourseCatalogService;
import edu.univ.erp.service.CourseManagementService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesView {

    private final CourseCatalogService catalogService = new CourseCatalogService();
    private final CourseManagementService adminService = new CourseManagementService();
    private JFrame f;
    private JTable table;
    private DefaultTableModel model;

    // To store Course Objects for editing
    private List<Course> courseList;

    public ManageCoursesView() {
        f = new JFrame("Manage Courses");
        f.setSize(800, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.setLocationRelativeTo(null);

        // Header
        JLabel title = new JLabel("Course Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        f.add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Code", "Title", "Credits"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Hide ID column visually but keep for logic
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        refreshTable();
        f.add(new JScrollPane(table), BorderLayout.CENTER);

        //  Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        JButton addBtn = new JButton("Create New Course");
        styleButton(addBtn, new Color(0, 153, 76)); // Green

        JButton editBtn = new JButton("Edit Selected Course");
        styleButton(editBtn, new Color(0, 102, 204)); // Blue

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, Color.GRAY);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(closeBtn);
        f.add(btnPanel, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(e -> showCourseDialog(null)); // Pass null to create

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(f, "Please select a course to edit.");
                return;
            }
            // Get actual course object using indecs (assuming list order matches table order)
            // If sorting is enabled on table, convertRowIndexToModel is needed.
            // For simplicity here, we rely on the hidden ID column.
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            // Find the course object
            Course selected = courseList.stream().filter(c -> c.getCourseId() == id).findFirst().orElse(null);
            if(selected != null) showCourseDialog(selected);
        });

        closeBtn.addActionListener(e -> f.dispose());

        f.setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        courseList = catalogService.getAllCourses();
        for (Course c : courseList) {
            model.addRow(new Object[]{c.getCourseId(), c.getCode(), c.getTitle(), c.getCredits()});
        }
    }

    // Shows a dialog for both Creating and Editing
    private void showCourseDialog(Course courseToEdit) {
        JDialog d = new JDialog(f, (courseToEdit == null ? "Create Course" : "Edit Course"), true);
        d.setSize(400, 300);
        d.setLayout(null);
        d.setLocationRelativeTo(f);

        JLabel l1 = new JLabel("Code:"); l1.setBounds(30, 30, 80, 25); d.add(l1);
        JTextField codeTxt = new JTextField(); codeTxt.setBounds(120, 30, 200, 25); d.add(codeTxt);

        JLabel l2 = new JLabel("Title:"); l2.setBounds(30, 70, 80, 25); d.add(l2);
        JTextField titleTxt = new JTextField(); titleTxt.setBounds(120, 70, 200, 25); d.add(titleTxt);

        JLabel l3 = new JLabel("Credits:"); l3.setBounds(30, 110, 80, 25); d.add(l3);
        JTextField credTxt = new JTextField(); credTxt.setBounds(120, 110, 200, 25); d.add(credTxt);

        JButton actionBtn = new JButton(courseToEdit == null ? "Create" : "Update");
        actionBtn.setBounds(120, 170, 100, 35);
        actionBtn.setBackground(Color.BLUE); actionBtn.setForeground(Color.WHITE); actionBtn.setOpaque(true); actionBtn.setBorderPainted(false);
        d.add(actionBtn);

        if (courseToEdit != null) {
            codeTxt.setText(courseToEdit.getCode());
            titleTxt.setText(courseToEdit.getTitle());
            credTxt.setText(String.valueOf(courseToEdit.getCredits()));
        }

        actionBtn.addActionListener(e -> {
            boolean success;
            if (courseToEdit == null) {
                success = adminService.createNewCourse(codeTxt.getText(), titleTxt.getText(), credTxt.getText());
            } else {
                success = adminService.updateCourse(courseToEdit.getCourseId(), codeTxt.getText(), titleTxt.getText(), credTxt.getText());
            }

            if (success) {
                JOptionPane.showMessageDialog(d, "Saved Successfully!");
                d.dispose();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(d, "Error saving course. Check inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setVisible(true);
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(160, 40));
    }
}