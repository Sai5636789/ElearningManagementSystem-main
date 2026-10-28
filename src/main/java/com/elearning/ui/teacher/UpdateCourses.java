package com.elearning.ui.teacher;

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


public class UpdateCourses extends JFrame implements ActionListener{
    JLabel title;
    JPanel middlePanel;
    JComboBox courseCb;
    JTextArea courseDescription, courseContent;
    JScrollPane scroll, scroll2;
    JButton updateBtn;
    public UpdateCourses() {
        super("Update Course");
        setLayout(new BorderLayout());
        
        title = new JLabel("Update Course", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont (22.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);
        
        middlePanel = new JPanel();
        middlePanel.setLayout(null);
        add(middlePanel, BorderLayout.CENTER);
        
        courseCb = new JComboBox(this.getMyCourses());
        courseCb.setSelectedIndex(-1);
        courseCb.setBounds(200, 30, 140, 28);
        courseCb.addActionListener(this);
        middlePanel.add(courseCb);
        
        JLabel courseDescriptionLbl = new JLabel("Course Description");
        courseDescriptionLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseDescriptionLbl.setBounds(80, 100, 140, 28);
        courseDescriptionLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseDescriptionLbl);
        
        courseDescription = new JTextArea();
        courseDescription.setLineWrap(true);
        courseDescription.setWrapStyleWord(true);
        scroll = new JScrollPane(courseDescription);
        scroll.setBounds(150, 130, 460, 100);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        courseDescription.setEditable(true);
        middlePanel.add(scroll);
        
        
        JLabel courseContentLbl = new JLabel("Course Content");
        courseContentLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseContentLbl.setBounds(80, 235, 140, 28);
        courseContentLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseContentLbl);
        
        courseContent = new JTextArea();
        courseContent.setLineWrap(true);
        courseContent.setWrapStyleWord(true);
        scroll2 = new JScrollPane(courseContent);
        scroll2.setBounds(10, 270, 685, 150);
        scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        courseContent.setEditable(true);
        middlePanel.add(scroll2);
        
        updateBtn = new JButton("Update");
        updateBtn.setFont(new Font(Font.SERIF,Font.BOLD, 15));
        updateBtn.setHorizontalAlignment(JButton.CENTER);
        updateBtn.addActionListener(this);
        add(updateBtn, BorderLayout.SOUTH);
        
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(720,520);
        setLocation(375,145);
        setVisible(true);
    }

    private String[] getMyCourses(){
        try {
            int teacherId = com.elearning.config.SessionContext.getUserIdOrDefault(TeacherLogin.currentTeacherID);
            com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
            return courseDao.findByTeacher(teacherId).stream()
                    .map(com.elearning.model.Course::getName)
                    .toArray(String[]::new);
        } catch(Exception e){
            e.printStackTrace();
            return new String[0];
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == courseCb){
            Object selected = courseCb.getSelectedItem();
            if (selected == null) return;
            String courseName = selected.toString();
            try {
                com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                java.util.Optional<com.elearning.model.Course> courseOpt = courseDao.findByName(courseName);
                if (courseOpt.isPresent()) {
                    courseDescription.setText(courseOpt.get().getDescription());
                    courseContent.setText(courseOpt.get().getContent());
                }
            } catch(Exception exception){
                exception.printStackTrace();
            }
        }
        else if(e.getSource() == updateBtn){
            Object selected = courseCb.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a course to update", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String courseName = selected.toString();
            String courseDescriptionStr = courseDescription.getText();
            String courseContentStr = courseContent.getText();
            try {
                com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                java.util.Optional<com.elearning.model.Course> courseOpt = courseDao.findByName(courseName);
                if (courseOpt.isPresent()) {
                    boolean ok = courseDao.updateCourse(courseOpt.get().getId(), courseDescriptionStr, courseContentStr);
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Course Updated Successfully");
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update course", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception exc){
                JOptionPane.showMessageDialog(this, "Error updating course: " + exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                exc.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        new UpdateCourses();
    }
    
}
