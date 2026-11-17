package com.skillbridge.model;

import java.util.HashMap;
import java.util.Map;

// Model aggregating platform-wide metrics and activity statistics for admin dashboard
public class PlatformStatistics {

    private int totalUsers;
    private int totalSkills;
    private int totalExchangeRequests;
    private int completedSessions;
    private int pendingReports;
    private double platformAverageRating;
    private Map<String, Integer> categoryDistribution;

    public PlatformStatistics() {
        this.categoryDistribution = new HashMap<>();
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalSkills() {
        return totalSkills;
    }

    public void setTotalSkills(int totalSkills) {
        this.totalSkills = totalSkills;
    }

    public int getTotalExchangeRequests() {
        return totalExchangeRequests;
    }

    public void setTotalExchangeRequests(int totalExchangeRequests) {
        this.totalExchangeRequests = totalExchangeRequests;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public int getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(int pendingReports) {
        this.pendingReports = pendingReports;
    }

    public double getPlatformAverageRating() {
        return platformAverageRating;
    }

    public void setPlatformAverageRating(double platformAverageRating) {
        this.platformAverageRating = platformAverageRating;
    }

    public Map<String, Integer> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
}
