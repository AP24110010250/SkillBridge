package com.skillbridge.model;

import java.sql.Timestamp;

// Model representing an achievement badge unlocked by a user
public class AchievementBadge {

    private int badgeId;
    private String badgeName;
    private String badgeDescription;
    private int requiredPoints;
    private Timestamp unlockedAt;

    public AchievementBadge() {
    }

    public AchievementBadge(int badgeId, String badgeName, String badgeDescription, int requiredPoints) {
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.badgeDescription = badgeDescription;
        this.requiredPoints = requiredPoints;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeDescription() {
        return badgeDescription;
    }

    public void setBadgeDescription(String badgeDescription) {
        this.badgeDescription = badgeDescription;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public Timestamp getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(Timestamp unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
