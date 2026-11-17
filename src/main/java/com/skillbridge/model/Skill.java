package com.skillbridge.model;

// Master Skill entity
public class Skill {

    private int skillId;
    private int categoryId;
    private String skillName;
    private String description;

    public Skill() {
    }

    public Skill(int skillId, int categoryId, String skillName, String description) {
        this.skillId = skillId;
        this.categoryId = categoryId;
        this.skillName = skillName;
        this.description = description;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
