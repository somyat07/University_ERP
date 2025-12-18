package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.SwingUtilities;

public class InstructorDashboard {

    // Services
    private final edu.univ.erp.service.NotificationService notifyService = new edu.univ.erp.service.NotificationService();
    private final edu.univ.erp.service.MaintenanceService maintenanceService = new edu.univ.erp.service.MaintenanceService();

    public InstructorDashboard(String username) {
        JFrame f = new JFrame("Instructor Dashboard");
        f.setSize(800, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);

        //  Greeting label
        JLabel greeting = new JLabel("Good Morning, " + username + "! Welcome to IIIT-Delhi.");
        greeting.setForeground(Color.blue);
        greeting.setFont(new Font("Arial", Font.BOLD, 14));
        greeting.setBounds(20, 10, 400, 30);
        f.add(greeting);

        // ALERT LABEL
        JLabel alertLabel = new JLabel("");
        alertLabel.setForeground(Color.RED);
        alertLabel.setFont(new Font("Arial", Font.BOLD, 14));
        alertLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        alertLabel.setBounds(430, 10, 350, 30);
        f.add(alertLabel);

        // CHECK MAINTENANCE STATUS
        if (maintenanceService.isMaintenanceEnabled()) {
            alertLabel.setText("⚠️ SYSTEM UNDER MAINTENANCE");
        } else {
            alertLabel.setText("");
        }

        // Section Title
        JLabel actionsLabel = new JLabel("Instructor Actions:");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionsLabel.setBounds(20, 60, 200, 25);
        f.add(actionsLabel);

        //  Buttons
        JButton mySectionsBtn = new JButton("My 🎓 Sections");
        JButton enterScoresBtn = new JButton("Enter 📝 Scores & Compute Final");
        JButton classStatsBtn = new JButton("View 📊 Class Stats");

        JButton logoutBtn = new JButton("Logout");
        JButton changePassBtn = new JButton("Change Password");

        // Layout
        mySectionsBtn.setBounds(50, 100, 200, 70);
        enterScoresBtn.setBounds(300, 100, 250, 70);
        classStatsBtn.setBounds(50, 200, 200, 70);

        // Bottom Row
        changePassBtn.setBounds(450, 400, 200, 40);
        logoutBtn.setBounds(660, 400, 100, 40);

        // Styles
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setBackground(Color.blue);
        logoutBtn.setForeground(Color.white);

        changePassBtn.setOpaque(true);
        changePassBtn.setBorderPainted(false);
        changePassBtn.setBackground(Color.GRAY);
        changePassBtn.setForeground(Color.white);

        f.add(mySectionsBtn);
        f.add(enterScoresBtn);
        f.add(classStatsBtn);
        f.add(changePassBtn); // Add to frame
        f.add(logoutBtn);

        // Actions
        mySectionsBtn.addActionListener(e -> new MySectionsView(username));
        enterScoresBtn.addActionListener(e -> new ComputeScoreView(username));
        classStatsBtn.addActionListener(e -> new ClassStatsView(username));

        changePassBtn.addActionListener(e -> new ChangePasswordView(username)); // Link to view

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(f, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                f.dispose();
                SwingUtilities.invokeLater(() -> new Main());
            }
        });

        f.setVisible(true);
    }

//    public static void main(String[] args) {
//        new InstructorDashboard("test_instructor");
//    }
}