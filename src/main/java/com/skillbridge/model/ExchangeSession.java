package com.skillbridge.model;

import java.sql.Timestamp;

// Model representing a scheduled learning exchange session
public class ExchangeSession {

    private int sessionId;
    private int requestId;
    private Timestamp scheduledTime;
    private int durationMinutes;
    private String meetingLink;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private String notes;
    private Timestamp createdAt;

    public ExchangeSession() {
        this.status = "SCHEDULED";
        this.durationMinutes = 60;
    }

    public ExchangeSession(int sessionId, int requestId, Timestamp scheduledTime, int durationMinutes, String meetingLink, String status, String notes) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.meetingLink = meetingLink;
        this.status = status != null ? status : "SCHEDULED";
        this.notes = notes;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
