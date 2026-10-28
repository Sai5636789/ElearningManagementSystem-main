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
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class AddCourse extends JFrame implements ActionListener{
    JLabel title, subjectCbLbl,courseNameLbl, courseDescriptionLbl, courseContentLbl;
    JComboBox subjectsCb;
    JTextArea courseDescription, courseContent;
    JButton addBtn;
    JPanel middlePanel;
    JTextField courseName;
    String SubjectsData[];
    JScrollPane scroll, scroll2;
    public AddCourse(){
        super("Add Course");
        setLayout(new BorderLayout());
        
        title = new JLabel("Add Course", JLabel.CENTER);
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
        subjectCbLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(subjectCbLbl);
        
        this.getSubjects();
        subjectsCb = new JComboBox(SubjectsData);
        subjectsCb.setSelectedIndex(-1);
        subjectsCb.setBounds(200, 30, 140, 28);
        subjectsCb.addActionListener(this);
        middlePanel.add(subjectsCb);
        
        
        courseNameLbl = new JLabel("Course Name");
        courseNameLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseNameLbl.setBounds(80, 75, 140, 28);
        courseNameLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseNameLbl);
        
        courseName = new JTextField();
        courseName.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseName.setBounds(200, 75, 120, 28);
        courseName.setHorizontalAlignment(JLabel.CENTER);
        middlePanel.add(courseName);
        
        
        courseDescriptionLbl = new JLabel("Course Description");
        courseDescriptionLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseDescriptionLbl.setBounds(80, 125, 140, 28);
        courseDescriptionLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseDescriptionLbl);
        
        courseDescription = new JTextArea();
        courseDescription.setLineWrap(true);
        courseDescription.setWrapStyleWord(true);
        scroll = new JScrollPane(courseDescription);
        scroll.setBounds(150, 155, 460, 100);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        courseDescription.setEditable(true);
        middlePanel.add(scroll);
        
        
        courseContentLbl = new JLabel("Course Content");
        courseContentLbl.setFont(new Font(Font.SERIF,Font.BOLD, 16));
        courseContentLbl.setBounds(80, 260, 140, 28);
        courseContentLbl.setHorizontalAlignment(JLabel.LEFT);
        middlePanel.add(courseContentLbl);
        
        courseContent = new JTextArea();
        courseContent.setLineWrap(true);
        courseContent.setWrapStyleWord(true);
        scroll2 = new JScrollPane(courseContent);
        scroll2.setBounds(10, 290, 685, 150);
        scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        courseContent.setEditable(true);
        middlePanel.add(scroll2);
       

        addBtn = new JButton("Add");
        addBtn.setFont(new Font(Font.SERIF,Font.BOLD, 15));
        addBtn.setHorizontalAlignment(JButton.CENTER);
        addBtn.addActionListener(this);
        add(addBtn, BorderLayout.SOUTH);
        

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(720,570);
        setLocation(375,145);
        setVisible(true);
    }
    private void getSubjects(){
        try {
            com.elearning.dao.SubjectDao subjectDao = new com.elearning.dao.SubjectDao();
            SubjectsData = subjectDao.findAll().stream()
                    .map(com.elearning.model.Subject::getName)
                    .toArray(String[]::new);
        } catch(Exception e){
            e.printStackTrace();
            SubjectsData = new String[0];
        }
    }
    @Override
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == addBtn){
            if (subjectsCb.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a subject", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String subjectStr = subjectsCb.getSelectedItem().toString();
            String courseNameStr = courseName.getText().trim();
            String courseDescriptionStr = courseDescription.getText().trim();
            String courseContentStr = courseContent.getText().trim();

            if (courseNameStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course name cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                com.elearning.dao.SubjectDao subjectDao = new com.elearning.dao.SubjectDao();
                java.util.Optional<com.elearning.model.Subject> subjectOpt = subjectDao.findByName(subjectStr);
                if (!subjectOpt.isPresent()) {
                    JOptionPane.showMessageDialog(this, "Subject not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int subjectId = subjectOpt.get().getId();
                int teacherId = com.elearning.config.SessionContext.getUserIdOrDefault(TeacherLogin.currentTeacherID);

                com.elearning.dao.CourseDao courseDao = new com.elearning.dao.CourseDao();
                com.elearning.model.Course course = new com.elearning.model.Course(0, courseNameStr, courseDescriptionStr, courseContentStr, teacherId, subjectId);
                boolean success = courseDao.addCourse(course);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course Added!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add course", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception e){
                JOptionPane.showMessageDialog(this, "This Course is already being offered or error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static void main(String[] args) {
        new AddCourse();
    }
 
}
