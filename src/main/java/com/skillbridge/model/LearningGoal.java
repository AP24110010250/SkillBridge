package com.skillbridge.model;

import java.sql.Timestamp;

// Model representing a student's learning goal and tracked progress
public class LearningGoal {

    private int goalId;
    private int userId;
    private int skillId;
    private String skillName;
    private String title;
    private int targetSessions;
    private int completedSessions;
    private double progressPercentage;
    private String status; // IN_PROGRESS, COMPLETED
    private Timestamp createdAt;

    public LearningGoal() {
        this.targetSessions = 5;
        this.completedSessions = 0;
        this.progressPercentage = 0.0;
        this.status = "IN_PROGRESS";
    }

    public LearningGoal(int goalId, int userId, int skillId, String title, int targetSessions, int completedSessions) {
        this.goalId = goalId;
        this.userId = userId;
        this.skillId = skillId;
        this.title = title;
        this.targetSessions = Math.max(targetSessions, 1);
        this.completedSessions = Math.max(completedSessions, 0);
        this.progressPercentage = Math.min(((double) this.completedSessions / this.targetSessions) * 100.0, 100.0);
        this.status = this.progressPercentage >= 100.0 ? "COMPLETED" : "IN_PROGRESS";
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTargetSessions() {
        return targetSessions;
    }

    public void setTargetSessions(int targetSessions) {
        this.targetSessions = Math.max(targetSessions, 1);
        calculatePercentage();
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = Math.max(completedSessions, 0);
        calculatePercentage();
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
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

    private void calculatePercentage() {
        if (this.targetSessions > 0) {
            this.progressPercentage = Math.min(((double) this.completedSessions / this.targetSessions) * 100.0, 100.0);
        } else {
            this.progressPercentage = 0.0;
        }
        if (this.progressPercentage >= 100.0) {
            this.status = "COMPLETED";
        }
    }
}
