package com.medicalassistance.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Assigned doctors to patients by counselor.
 */
@Document("assigned_patients")
public class AssignedPatient extends DateDomainObject {
    @Id
    private String assignedPatientId;

    private String doctorRegistrationNumber;

    @Indexed(unique = true)
    private String patientId;

    @Indexed(unique = true)
    private String patientRecordId;

    private String counselorRegistrationNumber;

    public String getDoctorRegistrationNumber() {
        return doctorRegistrationNumber;
    }

    public String getAssignedPatientId() {
        return assignedPatientId;
    }

    public void setAssignedPatientId(String assignedPatientId) {
        this.assignedPatientId = assignedPatientId;
    }

    public void setDoctorRegistrationNumber(String doctorRegistrationNumber) {
        this.doctorRegistrationNumber = doctorRegistrationNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientRecordId() {
        return patientRecordId;
    }

    public void setPatientRecordId(String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

    public String getCounselorRegistrationNumber() {
        return counselorRegistrationNumber;
    }

    public void setCounselorRegistrationNumber(String counselorRegistrationNumber) {
        this.counselorRegistrationNumber = counselorRegistrationNumber;
    }
}
