package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.InstructorData;
import edu.univ.erp.data.StudentData;
import edu.univ.erp.domain.EnrolledCourseDisplay;
import edu.univ.erp.domain.GradeDisplay;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.GradingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputeScoreView {

    private final InstructorData instructorData = new InstructorData();
    private final StudentData studentData = new StudentData();
    private final AuthData authData = new AuthData();
    private final GradingService gradingService = new GradingService();

    private JFrame f;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JButton closeButton, loadStudentsButton, saveGradesButton;
    private JButton computeFinalGradesButton, exportButton;
    private JLabel titleLabel, selectSectionLabel, messageLabel;
    private JComboBox<String> sectionComboBox;

    private int instructorId;
    private Map<Integer, Integer> comboIndexToSectionIdMap = new HashMap<>();

    public ComputeScoreView(String instructorUsername) {
        this.instructorId = authData.getUserIdByUsername(instructorUsername);

        f = new JFrame("Compute Scores");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        titleLabel = new JLabel("Enter Scores & Compute Final Grade");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        selectSectionLabel = new JLabel("Select your section:");
        selectSectionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selectSectionLabel.setBounds(20, 60, 150, 25);
        f.add(selectSectionLabel);

        sectionComboBox = new JComboBox<>();
        sectionComboBox.setBounds(160, 60, 400, 25);
        f.add(sectionComboBox);

        loadStudentsButton = new JButton("Load Students");
        loadStudentsButton.setBounds(570, 60, 150, 25);
        f.add(loadStudentsButton);

        String[] columnNames = {"Roll No / Info", "Student ID", "Component", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Score is editable
            }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentsTable.setRowHeight(25);
        studentsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        studentsTable.getColumnModel().removeColumn(studentsTable.getColumnModel().getColumn(1));

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBounds(20, 100, 740, 380);
        f.add(scrollPane);

        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 490, 600, 25);
        f.add(messageLabel);

        computeFinalGradesButton = new JButton("Compute Final Grades");
        computeFinalGradesButton.setBounds(540, 520, 220, 40);
        f.add(computeFinalGradesButton);

        saveGradesButton = new JButton("Save Component Grades");
        saveGradesButton.setBounds(310, 520, 220, 40);
        f.add(saveGradesButton);

        exportButton = new JButton("Export CSV");
        exportButton.setBounds(190, 520, 110, 40);
        f.add(exportButton);

        closeButton = new JButton("Close");
        closeButton.setBounds(80, 520, 100, 40);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        closeButton.addActionListener(e -> f.dispose());
        loadStudentsButton.addActionListener(e -> loadStudentsForSelectedSection());
        saveGradesButton.addActionListener(e -> handleSaveGrades());
        computeFinalGradesButton.addActionListener(e -> handleComputeFinalGrades());
        exportButton.addActionListener(e -> handleExportCSV());

        if (instructorId != -1) {
            loadSectionsIntoComboBox();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: Could not find instructor profile.");
        }

        f.setVisible(true);
    }

    private void loadSectionsIntoComboBox() {
        List<EnrolledCourseDisplay> sections = instructorData.getSectionsForInstructor(instructorId);
        if (sections.isEmpty()) {
            sectionComboBox.addItem("You are not assigned to any sections.");
            loadStudentsButton.setEnabled(false);
            return;
        }
        int comboIndex = 0;
        for (EnrolledCourseDisplay item : sections) {
            String displayText = String.format("%s (Section %d)", item.getCourse().getTitle(), item.getSection().getSectionId());
            sectionComboBox.addItem(displayText);
            comboIndexToSectionIdMap.put(comboIndex, item.getSection().getSectionId());
            comboIndex++;
        }
    }

    private void loadStudentsForSelectedSection() {
        tableModel.setRowCount(0);
        int selectedComboIndex = sectionComboBox.getSelectedIndex();
        if (!comboIndexToSectionIdMap.containsKey(selectedComboIndex)) return;
        int sectionId = comboIndexToSectionIdMap.get(selectedComboIndex);

        List<Student> students = studentData.getEnrolledStudentsBySection(sectionId);
        if (students.isEmpty()) {
            messageLabel.setText("No students are enrolled in this section.");
            return;
        }

        for (Student student : students) {
            List<GradeDisplay> existingGrades = gradingService.getGradesForEnrollment(student.getUserId(), sectionId);
            Map<String, String> scoreMap = new HashMap<>();
            String finalGrade = "N/A";

            for (GradeDisplay g : existingGrades) {
                scoreMap.put(g.getGradeComponent().toLowerCase(), String.valueOf(g.getScore()));
                if (g.getFinalGrade() != null && !g.getFinalGrade().equals("N/A")) {
                    finalGrade = g.getFinalGrade();
                }
            }

            tableModel.addRow(new Object[]{"Student: " + student.getRollNo(), student.getUserId(), "---", "---"});

            String quizScore = scoreMap.getOrDefault("quiz (20)", "");
            tableModel.addRow(new Object[]{"", student.getUserId(), "quiz (20)", quizScore});

            String midScore = scoreMap.getOrDefault("midsem (30)", "");
            tableModel.addRow(new Object[]{"", student.getUserId(), "midsem (30)", midScore});

            String endScore = scoreMap.getOrDefault("endsem (50)", "");
            tableModel.addRow(new Object[]{"", student.getUserId(), "endsem (50)", endScore});

            tableModel.addRow(new Object[]{"FINAL GRADE:", student.getUserId(), finalGrade, ""});
            tableModel.addRow(new Object[]{"", "", "", ""});
        }
        messageLabel.setText("Loaded " + students.size() + " students.");
    }

    private void handleSaveGrades() {
        if (studentsTable.isEditing()) {
            studentsTable.getCellEditor().stopCellEditing();
        }

        int selectedComboIndex = sectionComboBox.getSelectedIndex();
        if (!comboIndexToSectionIdMap.containsKey(selectedComboIndex)) return;
        int sectionId = comboIndexToSectionIdMap.get(selectedComboIndex);

        int gradesSaved = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object idObj = tableModel.getValueAt(i, 1); // We can stilll get ID from model even if column is hidden
            if (idObj == null || idObj.toString().isEmpty()) continue;
            int studentId = Integer.parseInt(idObj.toString());

            Object compObj = tableModel.getValueAt(i, 2);
            Object scoreObj = tableModel.getValueAt(i, 3);
            if (compObj == null || scoreObj == null) continue;

            String component = compObj.toString().trim();
            String scoreStr = scoreObj.toString().trim();

            if (component.equals("---") || component.startsWith("FINAL") || component.isEmpty()) continue;
            if (scoreStr.isEmpty()) continue;

            // Check return message
            String result = gradingService.saveGrade(studentId, sectionId, component, scoreStr);

            if (result.equals("Success")) {
                gradesSaved++;
            } else {
                // Show Error Popup immediately
                JOptionPane.showMessageDialog(f, result, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop processing to let instructor fix it
            }
        }
        messageLabel.setForeground(new Color(0, 150, 0));
        messageLabel.setText("Saved " + gradesSaved + " grade entries.");
    }

    private void handleComputeFinalGrades() {
        if (studentsTable.isEditing()) {
            studentsTable.getCellEditor().stopCellEditing();
        }

        int selectedComboIndex = sectionComboBox.getSelectedIndex();
        if (!comboIndexToSectionIdMap.containsKey(selectedComboIndex)) return;
        int sectionId = comboIndexToSectionIdMap.get(selectedComboIndex);

        List<Student> students = studentData.getEnrolledStudentsBySection(sectionId);
        int gradesComputed = 0;

        for (Student student : students) {
            String res = gradingService.computeFinalGrade(student.getUserId(), sectionId);
            if (!res.equals("Error")) gradesComputed++;
        }
        messageLabel.setForeground(new Color(0, 150, 0));
        messageLabel.setText("Computed " + gradesComputed + " final grades.");
        loadStudentsForSelectedSection();
    }

    private void handleExportCSV() {
        int selectedComboIndex = sectionComboBox.getSelectedIndex();
        if (!comboIndexToSectionIdMap.containsKey(selectedComboIndex)) return;
        int sectionId = comboIndexToSectionIdMap.get(selectedComboIndex);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Gradebook As...");
        if (fileChooser.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
            boolean success = gradingService.exportGradebookToCSV(sectionId, fileChooser.getSelectedFile());
            if (success) JOptionPane.showMessageDialog(f, "Export successful!");
            else JOptionPane.showMessageDialog(f, "Export failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ComputeScoreView("inst1"));
//    }
}