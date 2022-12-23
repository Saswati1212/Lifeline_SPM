package com.medicalassistance.core.request;

import java.time.ZonedDateTime;

public class AppointmentListForDateRequest {
    private ZonedDateTime date;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}