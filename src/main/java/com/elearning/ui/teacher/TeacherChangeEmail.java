package com.elearning.ui.teacher;

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

public class TeacherChangeEmail extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(TeacherChangeEmail.class);
    private final UserDao userDao = new UserDao();

    JLabel newEmaillbl;
    JTextField newEmail;
    JButton updateEmailbtn;

    public TeacherChangeEmail() {
        super("Update Email");
        newEmaillbl = new JLabel("New Email");
        newEmaillbl.setFont(new Font("Tahoma", Font.PLAIN, 20));
        newEmaillbl.setBounds(10, 10, 150, 30);
        add(newEmaillbl);

        newEmail = new JTextField();
        newEmail.setFont(new Font("Tahoma", Font.PLAIN, 22));
        newEmail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        newEmail.setBounds(150, 10, 150, 40);
        add(newEmail);
        newEmail.setColumns(20);

        updateEmailbtn = new JButton("Update");
        updateEmailbtn.setBounds(80, 90, 150, 30);
        updateEmailbtn.addActionListener(this);
        add(updateEmailbtn);

        setLayout(null);
        setSize(320, 220);
        setLocation(500, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == updateEmailbtn) {
            String email = newEmail.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int teacherId = com.elearning.config.SessionContext.getUserIdOrDefault(TeacherLogin.currentTeacherID);

            updateEmailbtn.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userDao.updateEmail(teacherId, Role.TEACHER, email);
                }

                @Override
                protected void done() {
                    updateEmailbtn.setEnabled(true);
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(TeacherChangeEmail.this, "Your Email Updated Successfully");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(TeacherChangeEmail.this, "Failed to update email", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        log.error("Failed to update teacher email", ex);
                        JOptionPane.showMessageDialog(TeacherChangeEmail.this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    public static void main(String[] args) {
        new TeacherChangeEmail();
    }
}