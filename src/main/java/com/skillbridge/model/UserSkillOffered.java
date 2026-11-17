package com.skillbridge.model;

// User skill offered (skills the user can teach)
public class UserSkillOffered {

    private int offeredId;
    private int userId;
    private int skillId;
    private String skillName;
    private String proficiencyLevel;
    private int yearsExperience;

    public UserSkillOffered() {
    }

    public UserSkillOffered(int offeredId, int userId, int skillId, String skillName, String proficiencyLevel, int yearsExperience) {
        this.offeredId = offeredId;
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsExperience = yearsExperience;
    }

    public int getOfferedId() {
        return offeredId;
    }

    public void setOfferedId(int offeredId) {
        this.offeredId = offeredId;
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

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
}
