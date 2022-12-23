package com.medicalassistance.core.response;

import java.time.ZonedDateTime;

public class AdminCounselorCard extends UserCardResponse {
    ZonedDateTime createdAt;

    public AdminCounselorCard(UserCardResponse userCardResponse) {
        this.setFullName(userCardResponse.getFullName());
        this.setEmailAddress(userCardResponse.getEmailAddress());
        this.setPhoneNumber(userCardResponse.getPhoneNumber());
        this.setRegistrationNumber(userCardResponse.getRegistrationNumber());
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}