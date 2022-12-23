package com.medicalassistance.core.request;

import java.util.Locale;

public class LoginRequest {
    String emailId;
    String password;

    public String getEmailId() {
        if (emailId != null && !emailId.isEmpty()) {
            return emailId.toLowerCase();
        }
        return null;
    }

    public void setEmailId(String emailId) {
        if (emailId != null && !emailId.isEmpty())
            this.emailId = emailId.toLowerCase(Locale.ROOT);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
