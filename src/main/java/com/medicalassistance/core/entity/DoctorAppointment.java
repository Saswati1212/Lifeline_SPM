package com.medicalassistance.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Appointments made by doctor for the patients.
 */
@Document("doctor_appointments")
public class DoctorAppointment extends Appointment {
    private String doctorId;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}
