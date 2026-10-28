package com.elearning.ui.student;

import com.elearning.config.SessionContext;
import com.elearning.dao.UserDao;
import com.elearning.model.Role;
import com.elearning.model.UserSession;
import com.elearning.ui.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class StudentLogin extends JFrame implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(StudentLogin.class);

    private final JLabel l1, l2;
    private final JTextField t1;
    private final JPasswordField t2;
    private final JButton b1, b2;
    public static int currentStudentID;

    public StudentLogin() {
        super("Student Login");
        setLayout(null);

        l1 = new JLabel("Username");
        l1.setBounds(40, 20, 100, 30);
        add(l1);

        l2 = new JLabel("Password");
        l2.setBounds(40, 70, 100, 30);
        add(l2);

        t1 = new JTextField();
        t1.setBounds(150, 20, 150, 30);
        add(t1);

        t2 = new JPasswordField();
        t2.setBounds(150, 70, 150, 30);
        add(t2);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/second.jpg"));
        Image i2 = i1.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(350, 20, 150, 150);
        add(l3);

        b1 = new JButton("Login");
        b1.setBounds(40, 140, 120, 30);
        b1.setFont(new Font("serif", Font.BOLD, 15));
        b1.addActionListener(this);
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        add(b1);

        b2 = new JButton("Cancel");
        b2.setBounds(180, 140, 120, 30);
        b2.setFont(new Font("serif", Font.BOLD, 15));
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        add(b2);
        b2.addActionListener(this);

        getContentPane().setBackground(Color.WHITE);

        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);
        setLocation(420, 365);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            String u = t1.getText().trim();
            String v = String.valueOf(t2.getPassword());

            if (u.isEmpty() || v.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            b1.setEnabled(false);
            new SwingWorker<Optional<UserSession>, Void>() {
                @Override
                protected Optional<UserSession> doInBackground() throws Exception {
                    return new UserDao().authenticate(u, v, Role.STUDENT);
                }

                @Override
                protected void done() {
                    b1.setEnabled(true);
                    try {
                        Optional<UserSession> sessionOpt = get();
                        if (sessionOpt.isPresent()) {
                            SessionContext.setCurrentUser(sessionOpt.get());
                            currentStudentID = sessionOpt.get().getId();
                            JOptionPane.showMessageDialog(StudentLogin.this, "Login successful");
                            setVisible(false);
                            new Student();
                            if (Main.main != null) {
                                Main.main.dispose();
                            }
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(StudentLogin.this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        log.error("Authentication error for student {}", u, e);
                        JOptionPane.showMessageDialog(StudentLogin.this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } else if (ae.getSource() == b2) {
            dispose();
        }
    }

    public static void main(String[] args) {
        new StudentLogin();
    }
}
