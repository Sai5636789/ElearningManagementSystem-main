package com.elearning.ui.student;

import com.elearning.config.SessionContext;
import com.elearning.model.Role;
import com.elearning.ui.AbstractChangePassword;

public class StudentChangePassword extends AbstractChangePassword {

    public StudentChangePassword() {
        super("Change Password — Student");
    }

    @Override
    protected int getCurrentUserId() {
        return SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
    }

    @Override
    protected Role getRole() {
        return Role.STUDENT;
    }

    public static void main(String[] args) {
        new StudentChangePassword();
    }
}
