package com.elearning.ui.teacher;

import com.elearning.config.SessionContext;
import com.elearning.model.Role;
import com.elearning.ui.AbstractChangePassword;

public class TeacherChangePassword extends AbstractChangePassword {

    public TeacherChangePassword() {
        super("Change Password — Teacher");
    }

    @Override
    protected int getCurrentUserId() {
        return SessionContext.getUserIdOrDefault(TeacherLogin.currentTeacherID);
    }

    @Override
    protected Role getRole() {
        return Role.TEACHER;
    }

    public static void main(String[] args) {
        new TeacherChangePassword();
    }
}
