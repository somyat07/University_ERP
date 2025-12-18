package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthData;
import edu.univ.erp.data.InstructorData;
import edu.univ.erp.domain.EnrolledCourseDisplay;
import edu.univ.erp.domain.GradeStat;
import edu.univ.erp.service.GradingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassStatsView {

    //  Backend Helpers
    private final InstructorData instructorData = new InstructorData();
    private final AuthData authData = new AuthData();
    private final GradingService gradingService = new GradingService();

    // UI Components
    private JFrame f;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JButton closeButton, loadStatsButton;
    private JLabel titleLabel, selectSectionLabel, messageLabel;
    private JScrollPane scrollPane;
    private JComboBox<String> sectionComboBox;

    // Instructor Info
    private int instructorId;

    // Map to store section ID from the combo box
    private Map<Integer, Integer> comboIndexToSectionIdMap = new HashMap<>();

    public ClassStatsView(String instructorUsername) {
        this.instructorId = authData.getUserIdByUsername(instructorUsername);

        f = new JFrame("Class Statistics");
        f.setSize(800, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null); // Manual layout
        f.setLocationRelativeTo(null); // Center window

        // Title
        titleLabel = new JLabel("View Class Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 300, 25);
        f.add(titleLabel);

        // Section Selector
        selectSectionLabel = new JLabel("Select your section:");
        selectSectionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selectSectionLabel.setBounds(20, 60, 150, 25);
        f.add(selectSectionLabel);

        sectionComboBox = new JComboBox<>();
        sectionComboBox.setBounds(160, 60, 400, 25);
        f.add(sectionComboBox);

        loadStatsButton = new JButton("Load Stats");
        loadStatsButton.setBounds(570, 60, 150, 25);
        f.add(loadStatsButton);

        // Stats Table
        String[] columnNames = {"Component", "Average Score", "Max Score", "Min Score", "Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        statsTable.setRowHeight(25);

        scrollPane = new JScrollPane(statsTable);
        scrollPane.setBounds(20, 100, 740, 300);
        f.add(scrollPane);

        // Message Label
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBounds(20, 410, 600, 25);
        f.add(messageLabel);

        // Close Button
        closeButton = new JButton("Close");
        closeButton.setBounds(660, 410, 100, 40);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.blue);
        closeButton.setForeground(Color.white);
        f.add(closeButton);

        // Listeners
        closeButton.addActionListener(e -> f.dispose());
        loadStatsButton.addActionListener(e -> loadStatsForSelectedSection());

        // Initial Load
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
            sectionComboBox.addItem("No sections assigned.");
            loadStatsButton.setEnabled(false);
            return;
        }

        int index = 0;
        for (EnrolledCourseDisplay item : sections) {
            String display = String.format("%s (Section %d)",
                    item.getCourse().getTitle(), item.getSection().getSectionId());
            sectionComboBox.addItem(display);
            comboIndexToSectionIdMap.put(index, item.getSection().getSectionId());
            index++;
        }
    }

    private void loadStatsForSelectedSection() {
        // Clear table
        tableModel.setRowCount(0);

        int selectedIndex = sectionComboBox.getSelectedIndex();
        if (!comboIndexToSectionIdMap.containsKey(selectedIndex)) return;
        int sectionId = comboIndexToSectionIdMap.get(selectedIndex);

        // Call the service to get the stats list
        List<GradeStat> stats = gradingService.getClassStats(sectionId);

        if (stats.isEmpty()) {
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText("No grades found for this section.");
            return;
        }

        // Populate table
        for (GradeStat stat : stats) {
            Object[] row = {
                    stat.getComponent(),
                    String.format("%.2f", stat.getAverage()), // Format to 2 decimal places
                    stat.getMaxScore(),
                    stat.getMinScore(),
                    stat.getCount()
            };
            tableModel.addRow(row);
        }

        messageLabel.setForeground(new Color(0, 150, 0));
        messageLabel.setText("Loaded statistics for " + stats.size() + " components.");
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ClassStatsView("inst1"));
//    }
}