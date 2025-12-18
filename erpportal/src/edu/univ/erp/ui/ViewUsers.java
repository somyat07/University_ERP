package edu.univ.erp.ui;

import edu.univ.erp.data.AdminData;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewUsers {

    private final AdminData adminData = new AdminData();

    public ViewUsers() {
        JFrame f = new JFrame("All System Users");
        f.setSize(500, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        JLabel title = new JLabel("All Registered Users");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(20, 20, 400, 25);
        f.add(title);

        String[] columns = {"Username", "Role"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 60, 440, 350);
        f.add(scroll);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(180, 420, 100, 40);
        closeBtn.setOpaque(true);
        closeBtn.setBorderPainted(false);
        closeBtn.setBackground(Color.blue);
        closeBtn.setForeground(Color.white);
        f.add(closeBtn);

        closeBtn.addActionListener(e -> f.dispose());

        loadData(model);
        f.setVisible(true);
    }

    private void loadData(DefaultTableModel model) {
        List<String[]> users = adminData.getAllSystemUsers();
        for (String[] row : users) {
            model.addRow(row);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ViewUsers::new);
//    }
}