package com.medicalassistance.core.response;

import java.time.ZonedDateTime;

public class AssignedPatientResponse {
    private String patientRecordId;

    private UserCardResponse patient;

    private CounselorDoctorCardResponse counselor;

    private ZonedDateTime assignedAt;

    private ZonedDateTime assessmentCreatedAt;

    public String getPatientRecordId() {
        return patientRecordId;
    }

    public void setPatientRecordId(String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

    public UserCardResponse getPatient() {
        return patient;
    }

    public void setPatient(UserCardResponse patient) {
        this.patient = patient;
    }

    public CounselorDoctorCardResponse getCounselor() {
        return counselor;
    }

    public void setCounselor(CounselorDoctorCardResponse counselor) {
        this.counselor = counselor;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public ZonedDateTime getAssessmentCreatedAt() {
        return assessmentCreatedAt;
    }

    public void setAssessmentCreatedAt(ZonedDateTime assessmentCreatedAt) {
        this.assessmentCreatedAt = assessmentCreatedAt;
    }
}
