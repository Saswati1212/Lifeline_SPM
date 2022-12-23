package com.medicalassistance.core.response;

import java.time.ZonedDateTime;

public class AppointmentResponse {
    private String patientRecordId;

    private UserResponse patient;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

    private ZonedDateTime createdAt;

    public String getPatientRecordId() {
        return patientRecordId;
    }

    public void setPatientRecordId(String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

    public UserResponse getPatient() {
        return patient;
    }

    public void setPatient(UserResponse patient) {
        this.patient = patient;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
