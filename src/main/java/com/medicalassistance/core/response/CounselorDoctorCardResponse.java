package com.medicalassistance.core.response;

public class CounselorDoctorCardResponse {
    private String registrationNumber;

    private String fullName;

    private String emailAddress;

    private String phoneNumber;

    private Integer currentPatients;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getCurrentPatients() {
        return currentPatients;
    }

    public void setCurrentPatients(Integer currentPatients) {
        this.currentPatients = currentPatients;
    }
}
