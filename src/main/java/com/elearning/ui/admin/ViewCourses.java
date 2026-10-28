package com.elearning.ui.admin;

import com.elearning.dao.CourseDao;
import com.elearning.dao.SubjectDao;
import com.elearning.model.Course;
import com.elearning.model.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class ViewCourses extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(ViewCourses.class);
    private final CourseDao courseDao = new CourseDao();
    private final SubjectDao subjectDao = new SubjectDao();

    private final String[] columnNames = {"Course", "Subject", "Teacher Name", "Teacher Email"};
    private JTable table;
    private DefaultTableModel model;
    private JLabel title, cbLbl;
    private JComboBox<String> filterBySubjectCb;
    private JPanel middlePanel;

    public ViewCourses() {
        super("View Courses");
        setLayout(new BorderLayout());

        title = new JLabel("Courses", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(25.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

        middlePanel = new JPanel();
        middlePanel.setLayout(null);
        add(middlePanel, BorderLayout.CENTER);

        cbLbl = new JLabel("Filter By Subject");
        cbLbl.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        cbLbl.setBounds(80, 25, 120, 28);
        cbLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(cbLbl);

        filterBySubjectCb = new JComboBox<>(getSubjects());
        filterBySubjectCb.setBounds(200, 25, 140, 28);
        filterBySubjectCb.addActionListener(this);
        middlePanel.add(filterBySubjectCb);

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY);
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(20, 95, 550, 245);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        middlePanel.add(pane);

        loadCourses("");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600, 450);
        setLocation(420, 260);
        setVisible(true);
    }

    private String[] getSubjects() {
        try {
            List<Subject> subjects = subjectDao.findAll();
            String[] names = new String[subjects.size() + 1];
            names[0] = "";
            for (int i = 0; i < subjects.size(); i++) {
                names[i + 1] = subjects.get(i).getName();
            }
            return names;
        } catch (Exception e) {
            log.error("Failed to load subjects", e);
            return new String[]{""};
        }
    }

    private void loadCourses(String subjectName) {
        model.setRowCount(0);
        try {
            Integer subjectId = null;
            if (subjectName != null && !subjectName.trim().isEmpty()) {
                Optional<Subject> s = subjectDao.findByName(subjectName.trim());
                if (s.isPresent()) {
                    subjectId = s.get().getId();
                }
            }
            List<Course> courses = courseDao.findAll(subjectId);
            for (Course c : courses) {
                model.addRow(new Object[]{
                        c.getName(),
                        c.getSubjectName() != null ? c.getSubjectName() : "",
                        c.getTeacherName() != null ? c.getTeacherName() : "",
                        c.getTeacherEmail() != null ? c.getTeacherEmail() : ""
                });
            }
        } catch (Exception e) {
            log.error("Failed to load courses", e);
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == filterBySubjectCb) {
            Object selected = filterBySubjectCb.getSelectedItem();
            String subjectName = selected != null ? selected.toString() : "";
            loadCourses(subjectName);
        }
    }

    public static void main(String[] args) {
        new ViewCourses();
    }
}