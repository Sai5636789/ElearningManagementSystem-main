package com.elearning.ui.teacher;

import com.elearning.dao.CourseDao;
import com.elearning.model.Course;
import com.elearning.config.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewMyCourses extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(ViewMyCourses.class);
    private final CourseDao courseDao = new CourseDao();

    private final String[] columnNames = {"Course ID", "Name", "Subject"};
    private JLabel title;
    private JTable table;
    private DefaultTableModel model;
    private JButton deleteButton;

    public ViewMyCourses() {
        super("View My Courses");
        setLayout(new BorderLayout());

        title = new JLabel("View My Courses", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(22.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

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
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setRowHeight(30);
        add(pane, BorderLayout.CENTER);

        deleteButton = new JButton("Delete");
        deleteButton.setHorizontalAlignment(JButton.CENTER);
        deleteButton.addActionListener(this);
        deleteButton.setToolTipText("Click to Delete Selected Course.");
        deleteButton.setPreferredSize(new Dimension(20, 50));
        add(deleteButton, BorderLayout.SOUTH);

        loadMyCourses();

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 360);
        setLocation(420, 260);
        setVisible(true);
    }

    private void loadMyCourses() {
        model.setRowCount(0);
        try {
            int teacherId = com.elearning.config.SessionContext.getUserIdOrDefault(TeacherLogin.currentTeacherID);
            List<Course> courses = courseDao.findByTeacher(teacherId);
            for (Course c : courses) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getName(),
                        c.getSubjectName() != null ? c.getSubjectName() : ""
                });
            }
        } catch (Exception e) {
            log.error("Failed to load teacher courses", e);
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == deleteButton) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int courseId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean ok = courseDao.deleteCourse(courseId);
                        if (ok) {
                            JOptionPane.showMessageDialog(this, "Selected Course deleted successfully");
                            loadMyCourses();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete course", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception exception) {
                        log.error("Failed to delete course", exception);
                        JOptionPane.showMessageDialog(this, "Error: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new ViewMyCourses();
    }
}