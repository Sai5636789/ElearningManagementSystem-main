package com.elearning.ui.student;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import com.elearning.dao.DBConnection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class EnrollCourse extends JFrame implements ActionListener{
    JLabel title, subjectCbLbl, courseCbLbl, courseDescriptionLbl;
    JTextArea courseDescription;
    JComboBox subjectsCb, coursesCb;
    JButton enrollBtn;
    JPanel middlePanel;
    JScrollPane scroll;
    public EnrollCourse(){
        super("Enroll Course");
        setLayout(new BorderLayout());
        
        title = new JLabel("Enroll Course", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont (22.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);
        
        middlePanel = new JPanel();
        middlePanel.setLayout(null);
        add(middlePanel, BorderLayout.CENTER);
        
        subjectCbLbl = new JLabel("Select Subject");
        subjectCbLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        subjectCbLbl.setBounds(80, 30, 120, 28);
        subjectCbLbl.setHorizontalAlignment(JLabel.CENTER);
        middlePanel.add(subjectCbLbl);

        subjectsCb = new JComboBox(this.getSubjects());
        subjectsCb.setSelectedIndex(-1);
        subjectsCb.setBounds(200, 30, 140, 28);
        subjectsCb.addActionListener(this);
        middlePanel.add(subjectsCb);
        
        courseCbLbl = new JLabel("Select Course");
        courseCbLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseCbLbl.setBounds(80, 80, 120, 28);
        courseCbLbl.setHorizontalAlignment(JLabel.CENTER);
        middlePanel.add(courseCbLbl);
        
        
        coursesCb = new JComboBox();
        coursesCb.setSelectedIndex(-1);
        coursesCb.setBounds(200, 80, 140, 28);
        coursesCb.addActionListener(this);
        middlePanel.add(coursesCb);
        
        
        courseDescriptionLbl = new JLabel("Course Description");
        courseDescriptionLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseDescriptionLbl.setBounds(80, 130, 140, 28);
        courseDescriptionLbl.setHorizontalAlignment(JLabel.CENTER);
        middlePanel.add(courseDescriptionLbl);
        
        courseDescription = new JTextArea();
        courseDescription.setLineWrap(true);
        courseDescription.setWrapStyleWord(true);
        scroll = new JScrollPane(courseDescription);
        scroll.setBounds(150, 160, 230, 100);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        courseDescription.setEditable(false);
        middlePanel.add(scroll);
       

        enrollBtn = new JButton("Enroll");
        enrollBtn.setFont(new Font(Font.SERIF,Font.BOLD, 15));
        enrollBtn.setHorizontalAlignment(JButton.CENTER);
        enrollBtn.addActionListener(this);
        add(enrollBtn, BorderLayout.SOUTH);
        

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(500,400);
        setLocation(420,320);
        setVisible(true);
    }
    private String[] getSubjects(){
        try {
            com.elearning.dao.SubjectDao subjectDao = new com.elearning.dao.SubjectDao();
            return subjectDao.findAll().stream()
                    .map(com.elearning.model.Subject::getName)
                    .toArray(String[]::new);
        } catch(Exception e){
            e.printStackTrace();
            return new String[0];
        }
    }

    private void getSetCourses(int subjectID){
        try {
            com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
            java.util.List<com.elearning.model.Course> courses = courseDao.findAll(subjectID);
            for (com.elearning.model.Course c : courses) {
                coursesCb.addItem(c.getName());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == subjectsCb){
            coursesCb.removeAllItems();
            courseDescription.setText(null);
            Object selected = subjectsCb.getSelectedItem();
            if (selected == null) return;
            String subjectName = selected.toString();
            try {
                com.elearning.dao.SubjectDao subjectDao = new com.elearning.dao.SubjectDao();
                java.util.Optional<com.elearning.model.Subject> subjectOpt = subjectDao.findByName(subjectName);
                if (subjectOpt.isPresent()) {
                    this.getSetCourses(subjectOpt.get().getId());
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(ae.getSource() == coursesCb){
            Object selected = coursesCb.getSelectedItem();
            if (selected != null) {
                String courseName = selected.toString();
                try {
                    com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                    java.util.Optional<com.elearning.model.Course> courseOpt = courseDao.findByName(courseName);
                    if (courseOpt.isPresent()) {
                        courseDescription.setText(courseOpt.get().getDescription());
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        else if(ae.getSource() == enrollBtn){
            if (coursesCb.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a course to enroll", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String courseNameStr = coursesCb.getSelectedItem().toString();
            int studentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);

            try {
                com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                com.elearning.dao.EnrollmentDao enrollmentDao = new com.elearning.dao.EnrollmentDao();
                java.util.Optional<com.elearning.model.Course> courseOpt = courseDao.findByName(courseNameStr);
                if (!courseOpt.isPresent()) {
                    JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int courseId = courseOpt.get().getId();
                if (enrollmentDao.isEnrolled(courseId, studentId)) {
                    JOptionPane.showMessageDialog(this, "You are already enrolled in this Course!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                boolean enrolled = enrollmentDao.enroll(courseId, studentId);
                if (enrolled) {
                    JOptionPane.showMessageDialog(this, "Course Enrollment Successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to enroll in course", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception e){
                JOptionPane.showMessageDialog(this, "Error enrolling in course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
        public static void main(String[] args) {
            new EnrollCourse();
        }

    }