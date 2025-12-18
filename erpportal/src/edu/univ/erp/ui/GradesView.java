package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.GradeData;
import edu.univ.erp.domain.GradeDisplay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GradesView {

    // Backend Helpers
    private final GradeData gradeData = new GradeData();
    private final AuthData authData = new AuthData();

    // UI Components
    private JFrame f;
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private JButton closeButton;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JLabel messageLabel;

    //  Student Info
    private int studentId;

    public GradesView(String studentUsername) {
        this.studentId = authData.getUserIdByUsername(studentUsername);

        f = new JFrame("My Grades");
        f.setSize(900, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        // Section Title
        titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        // Table Setup
        String[] columnNames = {"Course / Component", "Score", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        gradesTable.setRowHeight(25);

        // Set Column Widths
        setColumnWidths();

        scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBounds(20, 60, 840, 350); // Match frame width
        f.add(scrollPane);

        //  Message Label
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 420, 500, 25);
        f.add(messageLabel);

        // "Close" Button
        closeButton = new JButton("Close");
        closeButton.setBounds(760, 420, 100, 40); // Moved to right
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        closeButton.addActionListener(e -> f.dispose());

        // Load Data
        if (studentId != -1) {
            loadGradesData();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: Could not find student profile.");
        }

        f.setVisible(true);
    }

    private void setColumnWidths() {
        TableColumnModel columnModel = gradesTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(500);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(100);
    }

    private void loadGradesData() {
        List<GradeDisplay> allGrades = gradeData.getGradesForStudent(studentId);

        if (allGrades.isEmpty()) {
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText("No grades have been entered yet.");
            return;
        }

        // 1. Group grades by Course Name
        Map<String, List<GradeDisplay>> courseGroups = new LinkedHashMap<>();

        for (GradeDisplay g : allGrades) {
            String courseKey = g.getCourseCode() + ": " + g.getCourseTitle();
            courseGroups.computeIfAbsent(courseKey, k -> new ArrayList<>()).add(g);
        }

        // 2. Process each course group
        for (Map.Entry<String, List<GradeDisplay>> entry : courseGroups.entrySet()) {
            String courseHeader = entry.getKey();
            List<GradeDisplay> rawCourseGrades = entry.getValue();

            // Filter Duplicates & Find Final Grade
            Map<String, GradeDisplay> uniqueComponents = new TreeMap<>();
            String finalGrade = "N/A";

            for (GradeDisplay g : rawCourseGrades) {
                if (g.getFinalGrade() != null && !g.getFinalGrade().equals("N/A")) {
                    finalGrade = g.getFinalGrade();
                }

                String raw = g.getGradeComponent();
                String key = raw.split("\\(")[0].trim().toLowerCase();

                if (uniqueComponents.containsKey(key)) {
                    if (raw.contains("(")) uniqueComponents.put(key, g);
                } else {
                    uniqueComponents.put(key, g);
                }
            }

            // A. Course Header Row
            tableModel.addRow(new Object[]{courseHeader.toUpperCase(), "", ""});

            // B. Component Rows
            for (GradeDisplay g : uniqueComponents.values()) {
                tableModel.addRow(new Object[]{
                        "      " + g.getGradeComponent(),
                        g.getScore(),
                        ""
                });
            }

            // C. Final Grade Summary Row
            tableModel.addRow(new Object[]{
                    "      FINAL GRADE",
                    "",
                    finalGrade
            });

            // D. Blank Spacer Row
            tableModel.addRow(new Object[]{"", "", ""});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradesView("stu1"));
    }
}