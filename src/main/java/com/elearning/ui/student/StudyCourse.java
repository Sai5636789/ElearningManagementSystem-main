package com.elearning.ui.student;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import com.elearning.dao.DBConnection;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class StudyCourse extends JFrame implements ActionListener{
    JLabel title, subjectCbLbl, courseCbLbl, courseContentLbl;
    JTextArea courseContent;
    JComboBox coursesCb;
    JPanel middlePanel;
    JScrollPane scroll;
    public StudyCourse(){
        super("Study Course");
        setLayout(new BorderLayout());
        
        title = new JLabel("Study Course", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont (22.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);
        
        middlePanel = new JPanel();
        middlePanel.setLayout(null);
        add(middlePanel, BorderLayout.CENTER);
        
        
        courseCbLbl = new JLabel("Select Course");
        courseCbLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseCbLbl.setBounds(80, 80, 120, 28);
        courseCbLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseCbLbl);
        

        coursesCb = new JComboBox(this.getCourses());
        coursesCb.setSelectedIndex(-1);
        coursesCb.setBounds(200, 80, 140, 28);
        coursesCb.addActionListener(this);
        middlePanel.add(coursesCb);
        
        
        courseContentLbl = new JLabel("Course Content");
        courseContentLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseContentLbl.setBounds(80, 160, 120, 28);
        courseContentLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseContentLbl);
        
        courseContent = new JTextArea();
        courseContent.setLineWrap(true);
        courseContent.setWrapStyleWord(true);
        scroll = new JScrollPane(courseContent);
        scroll.setBounds(10, 190, 570, 180);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        courseContent.setEditable(false);
        middlePanel.add(scroll);
       
        

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600,450);
        setLocation(420,260);
        setVisible(true);
    }
    private String[] getCourses(){
        try {
            int studentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
            com.elearning.dao.EnrollmentDao enrollmentDao = new com.elearning.dao.EnrollmentDao();
            return enrollmentDao.findEnrolledCourses(studentId).stream()
                    .map(com.elearning.model.Course::getName)
                    .toArray(String[]::new);
        } catch(Exception e){
            e.printStackTrace();
            return new String[0];
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == coursesCb){
            Object selected = coursesCb.getSelectedItem();
            if (selected != null) {
                String courseName = selected.toString();
                try {
                    com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                    java.util.Optional<com.elearning.model.Course> courseOpt = courseDao.findByName(courseName);
                    if (courseOpt.isPresent()) {
                        courseContent.setText(courseOpt.get().getContent());
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
        public static void main(String[] args) {
            new StudyCourse();
        }

    }