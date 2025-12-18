package edu.univ.erp.ui;

import edu.univ.erp.service.LoginService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class Main {

    private final LoginService loginService = new LoginService();

    public Main() {
        JFrame user = new JFrame("University ERP Login");
        user.setSize(900, 650);
        user.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        user.setLocationRelativeTo(null);

        // 1. Main Background (Light Gray for contrast)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));

        // 2. Login Card (White box in the center)
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(Color.WHITE);
        // Add padding inside the white box
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Subtle gray border
                new EmptyBorder(40, 50, 40, 50) // Padding: Top, Left, Bottom, Right
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Vertical spacing between items
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // LOGO
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String imagePath = "src/iiitd_logo.jpg";
        File imgFile = new File(imagePath);

        if (imgFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image img = originalIcon.getImage();
            Image newImg = img.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(newImg));
        } else {
            logoLabel.setText("University- ERP");
            logoLabel.setFont(new Font("Serif", Font.BOLD, 28));
            logoLabel.setForeground(new Color(0, 100, 0));
        }

        // Add Logo to Box
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        loginBox.add(logoLabel, gbc);

        //  USERNAME LABEL
        JLabel l1 = new JLabel("Username");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l1.setForeground(Color.DARK_GRAY);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0);
        loginBox.add(l1, gbc);

        // USERNAME FIELD
        JTextField t1 = new JTextField(20);
        t1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t1.setPreferredSize(new Dimension(250, 35)); // Standard height

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0); // Gap after field
        loginBox.add(t1, gbc);

        // PASSWORD LABEL
        JLabel l2 = new JLabel("Password");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l2.setForeground(Color.DARK_GRAY);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 5, 0);
        loginBox.add(l2, gbc);

        //  PASSWORD FIELD
        JPasswordField p1 = new JPasswordField(20);
        p1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p1.setPreferredSize(new Dimension(250, 35)); // Standard height

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 25, 0); // Larger gap before button
        loginBox.add(p1, gbc);

        //  LOGIN BUTTON
        JButton b1 = new JButton("Login");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(0, 102, 204)); // Professional Blue
        b1.setOpaque(true);
        b1.setBorderPainted(false);
        b1.setFocusPainted(false);
        b1.setPreferredSize(new Dimension(250, 40)); // Taller button
        b1.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        loginBox.add(b1, gbc);

        // Add login box to main panel (centers it)
        mainPanel.add(loginBox);
        user.add(mainPanel);

        // Logic
        b1.addActionListener(e -> {
            String username = t1.getText();
            String password = new String(p1.getPassword());
            String role = loginService.login(username, password);

            if (role != null) {
                if (role.equals("LOCKED") || role.equals("LOCKED_NOW")) {
                    JOptionPane.showMessageDialog(user, "Account Locked.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                user.dispose();
                if (role.equals("Admin")) new AdminDashboard(username);
                else if (role.equals("Student")) new StudentDashboard(username);
                else if (role.equals("Instructor")) new InstructorDashboard(username);
            } else {
                JOptionPane.showMessageDialog(user, "Incorrect username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                p1.setText("");
            }
        });

        user.getRootPane().setDefaultButton(b1);
        user.setVisible(true);
    }

   public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> new Main());
   }
}