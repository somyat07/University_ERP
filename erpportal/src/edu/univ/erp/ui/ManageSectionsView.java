package edu.univ.erp.ui;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseCatalogService;
import edu.univ.erp.service.CourseManagementService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSectionsView {

    private final CourseCatalogService catalogService = new CourseCatalogService();
    private final CourseManagementService adminService = new CourseManagementService();
    private JFrame f;
    private JTable table;
    private DefaultTableModel model;

    public ManageSectionsView() {
        f = new JFrame("Manage Sections & Assignments");
        f.setSize(900, 600);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.setLocationRelativeTo(null);

        JLabel title = new JLabel("Section Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        f.add(title, BorderLayout.NORTH);

        String[] cols = {"Sec ID", "Course", "Instructor", "Time", "Room", "Cap"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        refreshTable();
        f.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton addBtn = new JButton("Create New Section");
        styleButton(addBtn, new Color(0, 153, 76));

        JButton editBtn = new JButton("Edit Selected Section");
        styleButton(editBtn, new Color(0, 102, 204));

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, Color.GRAY);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(closeBtn);
        f.add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showSectionDialog(null));

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(f, "Please select a section to edit.");
                return;
            }
            int secId = Integer.parseInt(table.getValueAt(row, 0).toString());
            Section selected = catalogService.getSectionById(secId); // Helper needed
            if(selected != null) showSectionDialog(selected);
        });

        closeBtn.addActionListener(e -> f.dispose());
        f.setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Course> courses = catalogService.getAllCourses();
        for (Course c : courses) {
            List<Section> sections = catalogService.getSectionsForCourse(c.getCourseId());
            for (Section s : sections) {
                String instrName = catalogService.getInstructorName(s.getInstructorId());
                model.addRow(new Object[]{
                        s.getSectionId(),
                        c.getCode(),
                        instrName,
                        s.getDayTime(),
                        s.getRoom(),
                        s.getCapacity()
                });
            }
        }
    }

    private void showSectionDialog(Section sectionToEdit) {
        JDialog d = new JDialog(f, (sectionToEdit == null ? "Create Section" : "Edit Section"), true);
        d.setSize(500, 400);
        d.setLayout(null);
        d.setLocationRelativeTo(f);

        JLabel l1 = new JLabel("Course:"); l1.setBounds(30, 30, 100, 25); d.add(l1);
        JComboBox<CourseItem> courseBox = new JComboBox<>(); courseBox.setBounds(140, 30, 300, 25); d.add(courseBox);

        JLabel l2 = new JLabel("Instructor:"); l2.setBounds(30, 70, 100, 25); d.add(l2);
        JComboBox<InstructorItem> instrBox = new JComboBox<>(); instrBox.setBounds(140, 70, 300, 25); d.add(instrBox);

        JLabel l3 = new JLabel("Day & Time:"); l3.setBounds(30, 110, 100, 25); d.add(l3);
        JTextField timeTxt = new JTextField(); timeTxt.setBounds(140, 110, 200, 25); d.add(timeTxt);

        JLabel l4 = new JLabel("Room:"); l4.setBounds(30, 150, 100, 25); d.add(l4);
        JTextField roomTxt = new JTextField(); roomTxt.setBounds(140, 150, 150, 25); d.add(roomTxt);

        JLabel l5 = new JLabel("Capacity:"); l5.setBounds(30, 190, 100, 25); d.add(l5);
        JTextField capTxt = new JTextField(); capTxt.setBounds(140, 190, 80, 25); d.add(capTxt);

        // Populate Dropdowns
        List<Course> courses = catalogService.getAllCourses();
        for(Course c : courses) {
            CourseItem item = new CourseItem(c);
            courseBox.addItem(item);
            if(sectionToEdit != null && c.getCourseId() == sectionToEdit.getCourseId()) courseBox.setSelectedItem(item);
        }

        List<Instructor> instructors = adminService.getAllInstructors();
        for(Instructor i : instructors) {
            InstructorItem item = new InstructorItem(i);
            instrBox.addItem(item);
            if(sectionToEdit != null && i.getUserId() == sectionToEdit.getInstructorId()) instrBox.setSelectedItem(item);
        }

        if(sectionToEdit != null) {
            timeTxt.setText(sectionToEdit.getDayTime());
            roomTxt.setText(sectionToEdit.getRoom());
            capTxt.setText(String.valueOf(sectionToEdit.getCapacity()));
            courseBox.setEnabled(false); // Prevent changing course when editing section (optional constraint)
        }

        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(180, 260, 120, 40);
        saveBtn.setBackground(Color.BLUE); saveBtn.setForeground(Color.WHITE); saveBtn.setOpaque(true); saveBtn.setBorderPainted(false);
        d.add(saveBtn);

        saveBtn.addActionListener(e -> {
            CourseItem cItem = (CourseItem) courseBox.getSelectedItem();
            InstructorItem iItem = (InstructorItem) instrBox.getSelectedItem();
            if(cItem == null || iItem == null) return;

            boolean success;
            if(sectionToEdit == null) {
                success = adminService.createNewSection(cItem.c.getCourseId(), iItem.i.getUserId(), timeTxt.getText(), roomTxt.getText(), capTxt.getText());
            } else {
                success = adminService.updateSection(sectionToEdit.getSectionId(), iItem.i.getUserId(), timeTxt.getText(), roomTxt.getText(), capTxt.getText());
            }

            if(success) {
                JOptionPane.showMessageDialog(d, "Saved!");
                d.dispose();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(d, "Error saving.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setVisible(true);
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(160, 40));
    }

    // Wrapper classes for Dropdowns
    class CourseItem { Course c; CourseItem(Course c){this.c=c;} public String toString(){return c.getCode()+" - "+c.getTitle();} }
    class InstructorItem { Instructor i; InstructorItem(Instructor i){this.i=i;} public String toString(){return i.getUsername();} }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ManageSectionsView::new);
//    }
}