package com.medicalassistance.core.common;

public enum PatientRecordStatus {
    NULL("NULL"),
    COUNSELOR_IN_PROGRESS("COUNSELOR_IN_PROGRESS"),
    COUNSELOR_APPOINTMENT("COUNSELOR_APPOINTMENT"),
    COUNSELOR_REJECTED("COUNSELOR_REJECTED"),
    DOCTOR_IN_PROGRESS("DOCTOR_IN_PROGRESS"),
    DOCTOR_REJECTED("DOCTOR_REJECTED"),
    DOCTOR_APPOINTMENT("DOCTOR_APPOINTMENT");

    private final String value;

    PatientRecordStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}