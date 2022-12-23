package com.medicalassistance.core.request;

public class DoctorAssignmentRequest {
    private String doctorRegistrationNumber;

    private String activePatientId;

    public String getDoctorRegistrationNumber() {
        return doctorRegistrationNumber;
    }

    public void setDoctorRegistrationNumber(String doctorRegistrationNumber) {
        this.doctorRegistrationNumber = doctorRegistrationNumber;
    }

    public String getActivePatientId() {
        return activePatientId;
    }

    public void setActivePatientId(String activePatientId) {
        this.activePatientId = activePatientId;
    }
}