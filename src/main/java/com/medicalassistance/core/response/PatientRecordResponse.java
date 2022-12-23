package com.medicalassistance.core.response;

import java.time.ZonedDateTime;

public class PatientRecordResponse {
    String recordId;
    UserResponse patient;
    ZonedDateTime createdAt;
    AssessmentResultResponse assessmentResult;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public UserResponse getPatient() {
        return patient;
    }

    public void setPatient(UserResponse patient) {
        this.patient = patient;
    }

    public AssessmentResultResponse getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(AssessmentResultResponse assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
