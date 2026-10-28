package com.elearning.ui.admin;

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

public class DeleteSubject extends JFrame implements ActionListener{
    JLabel title, subjectCbLbl;
    JComboBox subjectsCb, coursesCb;
    JButton deleteBtn;
    JPanel middlePanel;
    public DeleteSubject(){
        super("Delete Subject");
        setLayout(new BorderLayout());
        
        title = new JLabel("Delete Subject", JLabel.CENTER);
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
        
        deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font(Font.SERIF,Font.BOLD, 15));
        deleteBtn.setHorizontalAlignment(JButton.CENTER);
        deleteBtn.addActionListener(this);
        add(deleteBtn, BorderLayout.SOUTH);
        

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(500,200);
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
    @Override
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == deleteBtn){
            Object selected = subjectsCb.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String subjectName = selected.toString();
            int input = JOptionPane.showConfirmDialog(this, "Deleting Subject will delete all Corresponding Courses to this subject"
                    + ", do you want to proceed?", "Confirm Deletion",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if(input == JOptionPane.YES_OPTION){
                try {
                    com.elearning.dao.SubjectDao subjectDao = new com.elearning.dao.SubjectDao();
                    java.util.Optional<com.elearning.model.Subject> subjectOpt = subjectDao.findByName(subjectName);
                    if (subjectOpt.isPresent()) {
                        boolean ok = subjectDao.deleteSubject(subjectOpt.get().getId());
                        if (ok) {
                            JOptionPane.showMessageDialog(this, "Subject Deleted Successfully");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete subject", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Subject not found", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch(Exception e){
                    JOptionPane.showMessageDialog(this, "Error deleting subject: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
        public static void main(String[] args) {
            new DeleteSubject();
        }

}