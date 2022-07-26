package com.ganbook.models;

import java.util.Date;

public class MeetingHour {

    private Date startDate;
    private Date endDate;
    private String available;

    public MeetingHour(Date startDate, Date endDate, String available) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.available = available;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
