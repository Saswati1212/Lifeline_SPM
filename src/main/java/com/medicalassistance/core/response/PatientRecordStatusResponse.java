package com.medicalassistance.core.response;

import com.medicalassistance.core.common.PatientRecordStatus;

import java.time.ZonedDateTime;

public class PatientRecordStatusResponse {
    private PatientRecordStatus patientRecordStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;

    public PatientRecordStatusResponse() {
    }

    public PatientRecordStatusResponse(PatientRecordStatus patientRecordStatus) {
        this.patientRecordStatus = patientRecordStatus;
    }

    public PatientRecordStatus getPatientRecordStatus() {
        return patientRecordStatus;
    }

    public void setPatientRecordStatus(PatientRecordStatus patientRecordStatus) {
        this.patientRecordStatus = patientRecordStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
}