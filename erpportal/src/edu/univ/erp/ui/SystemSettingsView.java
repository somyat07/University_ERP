package edu.univ.erp.ui;

import edu.univ.erp.data.SettingsData;


import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SystemSettingsView {

    private final SettingsData settingsData = new SettingsData();
    private JFrame f;
    private JTextField regDateTxt;
    private JTextField dropDateTxt;

    public SystemSettingsView() {
        f = new JFrame("System Deadlines");
        f.setSize(500, 350);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        JLabel title = new JLabel("Set Course Deadlines");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(20, 20, 300, 25);
        f.add(title);

        JLabel hint = new JLabel("Format: YYYY-MM-DD (e.g., 2025-12-31)");
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);
        hint.setBounds(20, 50, 400, 20);
        f.add(hint);

        //  Registration Deadline
        JLabel l1 = new JLabel("Registration Deadline:");
        l1.setBounds(20, 90, 150, 25);
        f.add(l1);

        regDateTxt = new JTextField();
        regDateTxt.setBounds(180, 90, 150, 25);
        f.add(regDateTxt);

        //  Drop Deadline
        JLabel l2 = new JLabel("Drop Deadline:");
        l2.setBounds(20, 130, 150, 25);
        f.add(l2);

        dropDateTxt = new JTextField();
        dropDateTxt.setBounds(180, 130, 150, 25);
        f.add(dropDateTxt);

        // Save Button
        JButton saveBtn = new JButton("Save Settings");
        saveBtn.setBounds(180, 190, 150, 40);
        saveBtn.setBackground(Color.BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        f.add(saveBtn);

        // Load current values
        loadCurrentSettings();

        saveBtn.addActionListener(e -> saveSettings());

        f.setVisible(true);
    }

    private void loadCurrentSettings() {
        LocalDate reg = settingsData.getDeadline("registration_deadline");
        if (reg != null) regDateTxt.setText(reg.toString());

        LocalDate drop = settingsData.getDeadline("drop_deadline");
        if (drop != null) dropDateTxt.setText(drop.toString());
    }

    private void saveSettings() {
        try {
            String regStr = regDateTxt.getText().trim();
            String dropStr = dropDateTxt.getText().trim();

            if (!regStr.isEmpty()) {
                LocalDate d = LocalDate.parse(regStr);
                settingsData.setDeadline("registration_deadline", d);
            }
            if (!dropStr.isEmpty()) {
                LocalDate d = LocalDate.parse(dropStr);
                settingsData.setDeadline("drop_deadline", d);
            }

            JOptionPane.showMessageDialog(f, "Deadlines updated successfully!");
            f.dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(f, "Invalid Date Format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}