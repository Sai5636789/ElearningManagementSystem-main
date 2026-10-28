package com.elearning.ui.student;

import com.elearning.config.SessionContext;
import com.elearning.dao.UserDao;
import com.elearning.model.Role;
import com.elearning.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.util.Optional;

public class StudentDeleteAccount {

    private static final Logger log = LoggerFactory.getLogger(StudentDeleteAccount.class);
    private final UserDao userDao = new UserDao();
    public int input = -1;

    public StudentDeleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Do you want to delete your account? This cannot be undone.",
                "Confirm Account Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Re-authenticate before deleting.
        JPasswordField pwField = new JPasswordField(20);
        int pw = JOptionPane.showConfirmDialog(null, pwField,
                "Enter your current password to confirm", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (pw != JOptionPane.OK_OPTION) return;

        String entered = String.valueOf(pwField.getPassword());
        int studentId = SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);

        try {
            Optional<com.elearning.model.User> userOpt = userDao.findById(studentId, Role.STUDENT);
            if (userOpt.isEmpty() || !PasswordUtil.verify(entered, userOpt.get().getPassword())) {
                JOptionPane.showMessageDialog(null, "Incorrect password. Account not deleted.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean success = userDao.deleteAccount(studentId, Role.STUDENT);
            if (success) {
                input = JOptionPane.YES_OPTION;
                SessionContext.clear();
                JOptionPane.showMessageDialog(null, "Account deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Could not delete account.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Failed to delete student account", e);
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new StudentDeleteAccount();
    }
}
