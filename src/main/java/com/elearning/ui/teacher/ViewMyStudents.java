package com.elearning.ui.teacher;

import com.elearning.dao.CourseDao;
import com.elearning.dao.EnrollmentDao;
import com.elearning.model.Course;
import com.elearning.model.User;
import com.elearning.config.SessionContext;
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

public class ViewMyStudents extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(ViewMyStudents.class);
    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();

    private final String[] columnNames = {"Student ID", "First Name", "Last Name", "Email", "Gender"};
    private JLabel title, courseCbLbl;
    private JComboBox<String> coursesCb;
    private JPanel middlePanel;
    private JTable table;
    private DefaultTableModel model;

    public ViewMyStudents() {
        super("View My Students");
        setLayout(new BorderLayout());

        title = new JLabel("View My Students", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(22.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

        middlePanel = new JPanel();
        middlePanel.setLayout(null);
        add(middlePanel, BorderLayout.CENTER);

        courseCbLbl = new JLabel("Select Course");
        courseCbLbl.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        courseCbLbl.setBounds(80, 50, 120, 28);
        courseCbLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseCbLbl);

        coursesCb = new JComboBox<>(getMyCourses());
        coursesCb.setSelectedIndex(-1);
        coursesCb.setBounds(200, 50, 140, 28);
        coursesCb.addActionListener(this);
        middlePanel.add(coursesCb);

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY);
        JScrollPane pane = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pane.setBounds(20, 110, 550, 250);
        middlePanel.add(pane);

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocation(420, 260);
        setVisible(true);
    }

    private String[] getMyCourses() {
        try {
            int teacherId = com.elearning.config.SessionContext.getUserIdOrDefault(TeacherLogin.currentTeacherID);
            return courseDao.findByTeacher(teacherId).stream()
                    .map(Course::getName)
                    .toArray(String[]::new);
        } catch (Exception e) {
            log.error("Failed to load teacher courses", e);
            return new String[0];
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == coursesCb) {
            model.setRowCount(0);
            Object selected = coursesCb.getSelectedItem();
            if (selected == null) return;
            String courseName = selected.toString();

            try {
                Optional<Course> courseOpt = courseDao.findByName(courseName);
                if (courseOpt.isPresent()) {
                    List<User> students = enrollmentDao.findEnrolledStudents(courseOpt.get().getId());
                    for (User s : students) {
                        model.addRow(new Object[]{
                                s.getId(),
                                s.getFirstName(),
                                s.getLastName(),
                                s.getEmail(),
                                s.getGender() != null ? s.getGender() : ""
                        });
                    }
                }
            } catch (Exception e) {
                log.error("Failed to load course participants", e);
                JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new ViewMyStudents();
    }
}