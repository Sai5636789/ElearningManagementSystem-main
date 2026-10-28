package com.elearning.ui.student;

import com.elearning.dao.UserDao;
import com.elearning.model.Role;
import com.elearning.model.UserSession;
import com.elearning.config.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentChangeName extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(StudentChangeName.class);
    private final UserDao userDao = new UserDao();

    JLabel newFNamelbl, newLNamelbl;
    JTextField newFName, newLName;
    JButton updateNameButton;

    public StudentChangeName() {
        super("Update Name");
        newFNamelbl = new JLabel("First Name");
        newFNamelbl.setFont(new Font("Tahoma", Font.PLAIN, 20));
        newFNamelbl.setBounds(10, 10, 150, 30);
        add(newFNamelbl);

        newFName = new JTextField();
        newFName.setFont(new Font("Tahoma", Font.PLAIN, 22));
        newFName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        newFName.setBounds(150, 10, 150, 40);
        add(newFName);
        newFName.setColumns(20);

        newLNamelbl = new JLabel("Last Name");
        newLNamelbl.setFont(new Font("Tahoma", Font.PLAIN, 20));
        newLNamelbl.setBounds(10, 80, 150, 30);
        add(newLNamelbl);

        newLName = new JTextField();
        newLName.setFont(new Font("Tahoma", Font.PLAIN, 22));
        newLName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        newLName.setBounds(150, 80, 150, 40);
        add(newLName);
        newLName.setColumns(20);

        updateNameButton = new JButton("Update");
        updateNameButton.setBounds(80, 140, 150, 30);
        updateNameButton.addActionListener(this);
        add(updateNameButton);

        setLayout(null);
        setSize(320, 220);
        setLocation(500, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == updateNameButton) {
            String firstName = newFName.getText().trim();
            String lastName = newLName.getText().trim();

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);

            updateNameButton.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userDao.updateName(studentId, Role.STUDENT, firstName, lastName);
                }

                @Override
                protected void done() {
                    updateNameButton.setEnabled(true);
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(StudentChangeName.this, "Your Name Updated Successfully");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(StudentChangeName.this, "Failed to update name", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        log.error("Failed to update student name", ex);
                        JOptionPane.showMessageDialog(StudentChangeName.this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    public static void main(String[] args) {
        new StudentChangeName();
    }
}