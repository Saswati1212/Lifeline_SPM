package com.medicalassistance.core.entity;

import com.medicalassistance.core.common.PatientRecordStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents the patient record.
 */
@Document("patient_records")
public class PatientRecord extends DateDomainObject {
    @Id
    private String patientRecordId;

    @Indexed(unique = true)
    private String assessmentResultId;

    private String patientId;

    private PatientRecordStatus status = PatientRecordStatus.NULL;

    private String appointmentId;

    private String activePatientId;

    private String assignedPatientId;

    public String getPatientRecordId() {
        return patientRecordId;
    }

    public void setPatientRecordId(String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

    public String getAssessmentResultId() {
        return assessmentResultId;
    }

    public void setAssessmentResultId(String assessmentResultId) {
        this.assessmentResultId = assessmentResultId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public PatientRecordStatus getStatus() {
        return status;
    }

    public void setStatus(PatientRecordStatus status) {
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getActivePatientId() {
        return activePatientId;
    }

    public void setActivePatientId(String activePatientId) {
        this.activePatientId = activePatientId;
    }

    public String getAssignedPatientId() {
        return assignedPatientId;
    }

    public void setAssignedPatientId(String assignedPatientId) {
        this.assignedPatientId = assignedPatientId;
    }
}