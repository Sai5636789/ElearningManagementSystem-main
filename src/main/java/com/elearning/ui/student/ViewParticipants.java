package com.elearning.ui.student;

import com.elearning.dao.CourseDao;
import com.elearning.dao.EnrollmentDao;
import com.elearning.model.Course;
import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.config.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class ViewParticipants extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(ViewParticipants.class);
    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();

    private final String[] columnNames = {"ID", "Name", "Email", "Role"};
    private JLabel title, courseCbLbl;
    private JComboBox<String> coursesCb;
    private JPanel middlePanel;
    private JTable table;
    private DefaultTableModel model;
    private JButton contactBtn;

    public ViewParticipants() {
        super("View Participants");
        setLayout(new BorderLayout());

        title = new JLabel("Participants", JLabel.CENTER);
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
        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < model.getRowCount()) {
                String role = model.getValueAt(row, 3).toString();
                contactBtn.setEnabled(!"Teacher".equalsIgnoreCase(role));
            }
        });
        pane.setBounds(20, 95, 550, 245);
        middlePanel.add(pane);

        contactBtn = new JButton("Contact");
        contactBtn.setToolTipText("Click to Message the selected Participant.");
        contactBtn.setPreferredSize(new Dimension(50, 40));
        contactBtn.addActionListener(this);
        contactBtn.setEnabled(false);
        add(contactBtn, BorderLayout.SOUTH);

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocation(420, 260);
        setVisible(true);
    }

    private String[] getMyCourses() {
        try {
            int studentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
            return enrollmentDao.findEnrolledCourses(studentId).stream()
                    .map(Course::getName)
                    .toArray(String[]::new);
        } catch (Exception e) {
            log.error("Failed to load student courses", e);
            return new String[0];
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == coursesCb) {
            model.setRowCount(0);
            contactBtn.setEnabled(false);
            Object selected = coursesCb.getSelectedItem();
            if (selected == null) return;
            String courseName = selected.toString();

            try {
                Optional<Course> courseOpt = courseDao.findByName(courseName);
                if (courseOpt.isPresent()) {
                    int currentStudentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);

                    List<User> participants = enrollmentDao.findCourseParticipants(courseOpt.get().getId());
                    for (User u : participants) {
                        // Skip the currently logged-in student themselves
                        if (u.getRole() == Role.STUDENT && u.getId() == currentStudentId) {
                            continue;
                        }
                        String fullName = (u.getFirstName() + " " + (u.getLastName() != null ? u.getLastName() : "")).trim();
                        String roleStr = u.getRole() == Role.TEACHER ? "Teacher" : "Student";
                        model.addRow(new Object[]{u.getId(), fullName, u.getEmail(), roleStr});
                    }
                }
            } catch (Exception e) {
                log.error("Failed to load course participants", e);
                JOptionPane.showMessageDialog(this, "Error loading participants: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (ae.getSource() == contactBtn) {
            int rowNum = table.getSelectedRow();
            if (rowNum != -1) {
                int toId = Integer.parseInt(model.getValueAt(rowNum, 0).toString());
                String toName = model.getValueAt(rowNum, 1).toString();
                int currentStudentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
                new Message(currentStudentId, toName, toId);
            }
        }
    }

    public static void main(String[] args) {
        new ViewParticipants();
    }
}