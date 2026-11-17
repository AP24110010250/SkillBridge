package com.skillbridge.model;

import java.sql.Timestamp;

// Model representing a gamification skill point transaction
public class SkillPoint {

    private int pointId;
    private int userId;
    private int points;
    private String reason;
    private Timestamp createdAt;

    public SkillPoint() {
    }

    public SkillPoint(int pointId, int userId, int points, String reason) {
        this.pointId = pointId;
        this.userId = userId;
        this.points = points;
        this.reason = reason;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
