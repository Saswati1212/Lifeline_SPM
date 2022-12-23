package com.medicalassistance.core.response;

import java.time.ZonedDateTime;

public class AppointmentListForDateResponse {
    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

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
