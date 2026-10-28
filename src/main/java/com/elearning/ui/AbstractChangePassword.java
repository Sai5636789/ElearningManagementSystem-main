package com.elearning.ui;

import com.elearning.dao.UserDao;
import com.elearning.model.Role;
import com.elearning.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Shared change-password dialog used by all three roles.
 * Subclasses supply the current user's ID and role; this class owns the
 * form layout and the verify-then-update logic.
 */
public abstract class AbstractChangePassword extends JFrame implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(AbstractChangePassword.class);

    private final JPasswordField currentPasswordField;
    private final JPasswordField newPasswordField;
    private final JButton updateBtn;

    protected AbstractChangePassword(String title) {
        super(title);
        setLayout(null);
        setSize(360, 240);
        setLocation(500, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JLabel currentLbl = new JLabel("Current password");
        currentLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        currentLbl.setBounds(10, 15, 150, 25);
        add(currentLbl);

        currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        currentPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        currentPasswordField.setBounds(170, 12, 160, 30);
        add(currentPasswordField);

        JLabel newLbl = new JLabel("New password");
        newLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        newLbl.setBounds(10, 60, 150, 25);
        add(newLbl);

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        newPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        newPasswordField.setBounds(170, 57, 160, 30);
        add(newPasswordField);

        JLabel confirmLbl = new JLabel("Confirm new");
        confirmLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        confirmLbl.setBounds(10, 105, 150, 25);
        add(confirmLbl);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        confirmField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        confirmField.setBounds(170, 102, 160, 30);
        add(confirmField);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(100, 160, 140, 35);
        updateBtn.addActionListener(ae -> {
            String current = String.valueOf(currentPasswordField.getPassword());
            String next    = String.valueOf(newPasswordField.getPassword());
            String confirm = String.valueOf(confirmField.getPassword());

            if (current.isBlank() || next.isBlank()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!next.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "New password and confirmation do not match.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (next.length() < 6) {
                JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            updateBtn.setEnabled(false);
            new javax.swing.SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    UserDao dao = new UserDao();
                    // Load stored hash and verify current password before updating.
                    Optional<com.elearning.model.User> userOpt = dao.findById(getCurrentUserId(), getRole());
                    if (userOpt.isEmpty() || !PasswordUtil.verify(current, userOpt.get().getPassword())) {
                        return null; // null signals wrong current password
                    }
                    return dao.updatePassword(getCurrentUserId(), getRole(), next);
                }

                @Override
                protected void done() {
                    updateBtn.setEnabled(true);
                    try {
                        Boolean result = get();
                        if (result == null) {
                            JOptionPane.showMessageDialog(AbstractChangePassword.this,
                                    "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else if (result) {
                            JOptionPane.showMessageDialog(AbstractChangePassword.this,
                                    "Password updated successfully.");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(AbstractChangePassword.this,
                                    "Update failed — please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        log.error("Error updating password", ex);
                        JOptionPane.showMessageDialog(AbstractChangePassword.this,
                                "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
        add(updateBtn);

        setVisible(true);
    }

    /** Returns the ID of the currently logged-in user for this role. */
    protected abstract int getCurrentUserId();

    /** Returns the Role used to select the correct DB table. */
    protected abstract Role getRole();

    @Override
    public void actionPerformed(ActionEvent ae) { /* handled by lambda above */ }
}
