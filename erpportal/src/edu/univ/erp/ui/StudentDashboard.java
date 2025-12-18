package edu.univ.erp.ui;

import edu.univ.erp.service.TranscriptService;
import javax.swing.*;
import java.awt.*;
import javax.swing.JFileChooser;
import java.io.File;

public class StudentDashboard {

    private final edu.univ.erp.service.NotificationService notifyService = new edu.univ.erp.service.NotificationService();
    private final edu.univ.erp.service.MaintenanceService maintenanceService = new edu.univ.erp.service.MaintenanceService();
    private final TranscriptService transcriptService = new TranscriptService();

    private JFrame f;

    public StudentDashboard(String username) {
        f = new JFrame("Student Dashboard");
        f.setSize(800, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);

        //  Greeting
        JLabel greeting = new JLabel("Good Morning, " + username + "! Welcome to IIIT-Delhi.");
        greeting.setForeground(Color.blue);
        greeting.setFont(new Font("Arial", Font.BOLD, 14));
        greeting.setBounds(20, 10, 560, 30);
        f.add(greeting);

        //  ALERT LABEL
        JLabel alertLabel = new JLabel("");
        alertLabel.setForeground(Color.RED);
        alertLabel.setFont(new Font("Arial", Font.BOLD, 14));
        alertLabel.setBounds(20, 35, 700, 25);
        f.add(alertLabel);

        // Only Show Message if Maintenance is ON
        if (maintenanceService.isMaintenanceEnabled()) {
            alertLabel.setText("⚠️ SYSTEM UNDER MAINTENANCE");
        } else {
            alertLabel.setText(""); // Empty if maintenance is OFF
        }

        //  Section Title
        JLabel actionsLabel = new JLabel("Student Actions:");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionsLabel.setBounds(20, 60, 200, 25);
        f.add(actionsLabel);

        //  Buttons
        JButton browseBtn = new JButton("Browse Course");
        JButton registerBtn = new JButton("Register Course");
        JButton dropBtn = new JButton("Drop Course");
        JButton timetableBtn = new JButton("Timetable ");
        JButton gradesBtn = new JButton("View Grades");
        JButton transcriptBtn = new JButton("Download Transcript");

        JButton logoutBtn = new JButton("Logout");
        JButton changePassBtn = new JButton("Change Password");

        // Styles
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setBackground(Color.blue);
        logoutBtn.setForeground(Color.white);

        changePassBtn.setOpaque(true);
        changePassBtn.setBorderPainted(false);
        changePassBtn.setBackground(Color.GRAY);
        changePassBtn.setForeground(Color.white);

        //  Layout
        browseBtn.setBounds(50, 100, 200, 70);
        registerBtn.setBounds(300, 100 , 200, 70);
        gradesBtn.setBounds(550, 100, 200, 70);

        dropBtn.setBounds(50, 200, 200, 70);
        timetableBtn.setBounds(300, 200 , 200, 70);
        transcriptBtn.setBounds(550, 200, 200, 70);

        changePassBtn.setBounds(450, 400, 200, 40);
        logoutBtn.setBounds(660, 400, 100, 40);

        f.add(browseBtn);
        f.add(registerBtn);
        f.add(dropBtn);
        f.add(timetableBtn);
        f.add(gradesBtn);
        f.add(transcriptBtn);
        f.add(changePassBtn);
        f.add(logoutBtn);

        // Listeners
        browseBtn.addActionListener(e -> new CourseCatalogView());
        registerBtn.addActionListener(e -> new RegistrationView(username));
        dropBtn.addActionListener(e -> new DropCourseView(username));
        timetableBtn.addActionListener(e -> new TimetableView(username));
        gradesBtn.addActionListener(e -> new GradesView(username));
        transcriptBtn.addActionListener(e -> handleDownloadTranscript(username));
        changePassBtn.addActionListener(e -> new ChangePasswordView(username));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(f, "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                f.dispose();
                SwingUtilities.invokeLater(() -> new Main());
            }
        });

        f.setVisible(true);
    }

    private void handleDownloadTranscript(String username) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript As...");

        int userSelection = fileChooser.showSaveDialog(f);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            boolean success = transcriptService.generateTranscript(username, fileToSave);

            if (success) {
                JOptionPane.showMessageDialog(f,
                        "Transcript saved successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Transcript Saved", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(f, "Error: Could not save the transcript.",
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new StudentDashboard("stu1"));
//    }
}