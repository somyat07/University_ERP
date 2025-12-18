package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.SwingUtilities;

public class AdminDashboard {

    private final edu.univ.erp.service.MaintenanceService maintenanceService = new edu.univ.erp.service.MaintenanceService();
    private final edu.univ.erp.service.NotificationService notifyService = new edu.univ.erp.service.NotificationService();

    private JLabel alertLabel;

    public AdminDashboard(String username) {
        JFrame f = new JFrame("Admin Dashboard");
        f.setSize(800, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);

        JLabel greeting = new JLabel("Good Morning, " + username + "! Welcome to IIIT-Delhi.");
        greeting.setForeground(Color.blue);
        greeting.setFont(new Font("Arial", Font.BOLD, 14));
        greeting.setBounds(20, 10, 400, 30);
        f.add(greeting);

        JButton settingsBtn = new JButton("Set 📅 Deadlines");
        settingsBtn.setBounds(50, 270, 200, 70); // Adjust layout as needed
        f.add(settingsBtn);

        settingsBtn.addActionListener(e -> new SystemSettingsView());

        // ALERT LABEL
        alertLabel = new JLabel("");
        alertLabel.setForeground(Color.RED);
        alertLabel.setFont(new Font("Arial", Font.BOLD, 14));
        alertLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        alertLabel.setBounds(430, 10, 350, 30);
        f.add(alertLabel);

        // Initial Check
        refreshAlertLabel();

        JLabel actionsLabel = new JLabel("Admin Actions:");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionsLabel.setBounds(20, 50, 200, 25);
        f.add(actionsLabel);

        JButton addUsersBtn = new JButton("Add Users");
        JButton viewUsersBtn = new JButton("View All Users");
        JButton manageCoursesBtn = new JButton("Create/Edit Courses");
        JButton notifBtn = new JButton("Post Alert");

        JButton assignInstructorBtn = new JButton("Assign Instructor");
        JButton maintenanceBtn = new JButton("Toggle Maintenance");

        JButton logoutBtn = new JButton("Logout");
        JButton changePassBtn = new JButton("Change Password");

        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setBackground(Color.blue);
        logoutBtn.setForeground(Color.white);

        changePassBtn.setOpaque(true);
        changePassBtn.setBorderPainted(false);
        changePassBtn.setBackground(Color.GRAY);
        changePassBtn.setForeground(Color.white);

        addUsersBtn.setBounds(50, 90, 200, 70);
        viewUsersBtn.setBounds(300, 90, 200, 70);
        notifBtn.setBounds(550, 90, 200, 70);

        manageCoursesBtn.setBounds(50, 180, 200, 70);
        assignInstructorBtn.setBounds(300, 180, 200, 70);
        maintenanceBtn.setBounds(550, 180, 200, 70);

        changePassBtn.setBounds(450, 400, 200, 40);
        logoutBtn.setBounds(660, 400, 100, 40);

        f.add(addUsersBtn);
        f.add(viewUsersBtn);
        f.add(manageCoursesBtn);
        f.add(assignInstructorBtn);
        f.add(maintenanceBtn);
        f.add(notifBtn);
        f.add(changePassBtn);
        f.add(logoutBtn);

        addUsersBtn.addActionListener(e -> {
            if (isMaintenanceActive(f)) return;
            new AddUserView();
        });

        viewUsersBtn.addActionListener(e -> new ViewUsers());

        manageCoursesBtn.addActionListener(e -> {
            if (isMaintenanceActive(f)) return;
            new ManageCoursesView();
        });

        assignInstructorBtn.addActionListener(e -> {
            if (isMaintenanceActive(f)) return;
            new ManageSectionsView();
        });

        // TOGGLE LOGIC
        maintenanceBtn.addActionListener(e -> {
            boolean isNowOn = maintenanceService.toggleMaintenanceMode();

            if (isNowOn) {
                JOptionPane.showMessageDialog(f, "Maintenance Mode is now ON.", "System Status", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(f, "Maintenance Mode is now OFF.", "System Status", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshAlertLabel();
        });

        notifBtn.addActionListener(e -> {
            String current = notifyService.get();
            String newMsg = JOptionPane.showInputDialog(f, "Enter alert message:", current);
            if (newMsg != null) {
                notifyService.post(newMsg);
                JOptionPane.showMessageDialog(f, "Posted!");

            }
        });

        changePassBtn.addActionListener(e -> new ChangePasswordView(username));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(f, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                f.dispose();
                SwingUtilities.invokeLater(() -> new Main());
            }
        });

        f.setVisible(true);
    }

    private void refreshAlertLabel() {
        if (maintenanceService.isMaintenanceEnabled()) {
            alertLabel.setText("⚠️ SYSTEM UNDER MAINTENANCE");
        } else {
            alertLabel.setText(""); // Clear text completely
        }
    }

    private boolean isMaintenanceActive(JFrame parentFrame) {
        if (maintenanceService.isMaintenanceEnabled()) {
            JOptionPane.showMessageDialog(parentFrame, "System is in Maintenance Mode.\nAction Blocked.", "Blocked", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

//    public static void main(String[] args) {
//        new AdminDashboard("test_admin");
//    }
}