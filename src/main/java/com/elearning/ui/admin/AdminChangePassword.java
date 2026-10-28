package com.elearning.ui.admin;

import com.elearning.config.SessionContext;
import com.elearning.model.Role;
import com.elearning.ui.AbstractChangePassword;

public class AdminChangePassword extends AbstractChangePassword {

    public AdminChangePassword() {
        super("Change Password — Admin");
    }

    @Override
    protected int getCurrentUserId() {
        return SessionContext.getUserIdOrDefault(AdminLogin.currentAdminID);
    }

    @Override
    protected Role getRole() {
        return Role.ADMIN;
    }

    public static void main(String[] args) {
        new AdminChangePassword();
    }
}
