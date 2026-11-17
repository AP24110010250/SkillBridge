package com.skillbridge.model;

import java.sql.Timestamp;

// Model representing an abuse or behavioral report filed against a user
public class UserReport {

    private int reportId;
    private int reporterId;
    private String reporterName;
    private int reportedUserId;
    private String reportedUserName;
    private String reason;
    private String status; // PENDING, RESOLVED, DISMISSED
    private Timestamp createdAt;

    public UserReport() {
        this.status = "PENDING";
    }

    public UserReport(int reportId, int reporterId, int reportedUserId, String reason, String status) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reason = reason;
        this.status = status != null ? status : "PENDING";
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public int getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(int reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedUserName() {
        return reportedUserName;
    }

    public void setReportedUserName(String reportedUserName) {
        this.reportedUserName = reportedUserName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
