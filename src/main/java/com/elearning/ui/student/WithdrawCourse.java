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

public class WithdrawCourse extends JFrame implements ActionListener{
    JLabel title, courseCbLbl;
    JComboBox courseCb;
    JButton withdrawBtn;
    JPanel middlePanel;
    public WithdrawCourse(){
        super("Withdraw Course");
        setLayout(new BorderLayout());
        
        title = new JLabel("Withdraw Course", JLabel.CENTER);
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
        courseCbLbl.setBounds(80, 30, 120, 28);
        courseCbLbl.setHorizontalAlignment(JLabel.CENTER);
        middlePanel.add(courseCbLbl);

        courseCb = new JComboBox(this.getCourses());
        courseCb.setSelectedIndex(-1);
        courseCb.setBounds(200, 30, 140, 28);
        courseCb.addActionListener(this);
        middlePanel.add(courseCb);
        
        withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setFont(new Font(Font.SERIF,Font.BOLD, 15));
        withdrawBtn.setHorizontalAlignment(JButton.CENTER);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn, BorderLayout.SOUTH);
        

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(500,200);
        setLocation(420,320);
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
        if(ae.getSource() == withdrawBtn){
            Object selected = courseCb.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a course to withdraw from", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String courseName = selected.toString();
            int input = JOptionPane.showConfirmDialog(this, "Are you sure you want to Withdraw from this Course?", "Confirm Withdrawal",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if(input == JOptionPane.YES_OPTION){
                try {
                    int studentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
                    com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                    com.elearning.dao.EnrollmentDao enrollmentDao = new com.elearning.dao.EnrollmentDao();
                    java.util.Optional<com.elearning.model.Course> courseOpt = courseDao.findByName(courseName);
                    if (courseOpt.isPresent()) {
                        boolean ok = enrollmentDao.withdraw(courseOpt.get().getId(), studentId);
                        if (ok) {
                            JOptionPane.showMessageDialog(this, "Course Withdrawn Successfully");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to withdraw course", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch(Exception e){
                    JOptionPane.showMessageDialog(this, "Error withdrawing course: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }
        public static void main(String[] args) {
            new WithdrawCourse();
        }

}