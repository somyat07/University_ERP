package edu.univ.erp.ui;

import edu.univ.erp.service.UserService;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordView {

    private final UserService userService = new UserService();
    private JFrame f;
    private JPasswordField oldPass, newPass;
    private JButton updateBtn, closeBtn;
    private String username;

    public ChangePasswordView(String username) {
        this.username = username;
        f = new JFrame("Change Password");
        f.setSize(400, 300);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBounds(20, 20, 200, 25);
        f.add(title);

        JLabel l1 = new JLabel("Old Password:");
        l1.setBounds(20, 60, 120, 25);
        f.add(l1);
        oldPass = new JPasswordField();
        oldPass.setBounds(140, 60, 200, 25);
        f.add(oldPass);

        JLabel l2 = new JLabel("New Password:");
        l2.setBounds(20, 100, 120, 25);
        f.add(l2);
        newPass = new JPasswordField();
        newPass.setBounds(140, 100, 200, 25);
        f.add(newPass);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(200, 160, 100, 30);
        f.add(updateBtn);

        closeBtn = new JButton("Cancel");
        closeButtonStyle(closeBtn);
        closeBtn.setBounds(80, 160, 100, 30);
        f.add(closeBtn);

        closeBtn.addActionListener(e -> f.dispose());
        updateBtn.addActionListener(e -> handleChange());

        f.setVisible(true);
    }

    private void handleChange() {
        String oldP = new String(oldPass.getPassword());
        String newP = new String(newPass.getPassword());

        if (userService.changePassword(username, oldP, newP)) {
            JOptionPane.showMessageDialog(f, "Password Changed Successfully!");
            f.dispose();
        } else {
            JOptionPane.showMessageDialog(f, "Error: Old password incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeButtonStyle(JButton btn) {
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBackground(Color.blue);
        btn.setForeground(Color.white);
    }
}
