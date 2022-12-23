package com.medicalassistance.core.request;

import java.time.ZonedDateTime;

public class DummyUsers {
    Integer startIndex;

    Integer numPatient;

    Integer numAttemptedAssessment;

    Integer numHasCounselorAppointment;

    Integer numInProcessingDoctor;

    Integer numHasDoctorAppointment;

    ZonedDateTime startDateTime;

    Integer interval; // In minutes

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getNumPatient() {
        return numPatient;
    }

    public void setNumPatient(Integer numPatient) {
        this.numPatient = numPatient;
    }

    public Integer getNumAttemptedAssessment() {
        return numAttemptedAssessment;
    }

    public void setNumAttemptedAssessment(Integer numAttemptedAssessment) {
        this.numAttemptedAssessment = numAttemptedAssessment;
    }

    public Integer getNumHasCounselorAppointment() {
        return numHasCounselorAppointment;
    }

    public void setNumHasCounselorAppointment(Integer numHasCounselorAppointment) {
        this.numHasCounselorAppointment = numHasCounselorAppointment;
    }

    public Integer getNumInProcessingDoctor() {
        return numInProcessingDoctor;
    }

    public void setNumInProcessingDoctor(Integer numInProcessingDoctor) {
        this.numInProcessingDoctor = numInProcessingDoctor;
    }

    public Integer getNumHasDoctorAppointment() {
        return numHasDoctorAppointment;
    }

    public void setNumHasDoctorAppointment(Integer numHasDoctorAppointment) {
        this.numHasDoctorAppointment = numHasDoctorAppointment;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}