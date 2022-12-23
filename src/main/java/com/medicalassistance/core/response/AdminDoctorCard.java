package com.medicalassistance.core.response;

import java.time.ZonedDateTime;

public class AdminDoctorCard extends UserCardResponse {
    ZonedDateTime createdAt;

    public AdminDoctorCard(UserCardResponse userCardResponse) {
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