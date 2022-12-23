package com.medicalassistance.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Appointments made by counselor for the patients.
 */
@Document("counselor_appointments")
public class CounselorAppointment extends Appointment {
    private String counselorId;

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }
}
