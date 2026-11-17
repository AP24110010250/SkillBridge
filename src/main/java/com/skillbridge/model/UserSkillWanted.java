package com.skillbridge.model;

// User skill wanted (skills the user wants to learn)
public class UserSkillWanted {

    private int wantedId;
    private int userId;
    private int skillId;
    private String skillName;
    private String targetLevel;
    private String priority;

    public UserSkillWanted() {
    }

    public UserSkillWanted(int wantedId, int userId, int skillId, String skillName, String targetLevel, String priority) {
        this.wantedId = wantedId;
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.targetLevel = targetLevel;
        this.priority = priority;
    }

    public int getWantedId() {
        return wantedId;
    }

    public void setWantedId(int wantedId) {
        this.wantedId = wantedId;
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

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
