package com.skillbridge.matching;

import java.util.ArrayList;
import java.util.List;
import com.skillbridge.model.User;

// Represents an evaluated match result between two users
public class MatchResult implements Comparable<MatchResult> {

    private User partner;
    private double overallScore;        // 0.0 to 100.0
    private double skillScore;          // Max 50 points
    private double reciprocityScore;    // Max 25 points
    private double availabilityScore;   // Max 15 points
    private double reliabilityScore;    // Max 10 points
    private boolean isReciprocal;

    private List<String> skillsTheyTeachMe;
    private List<String> skillsITeachThem;

    public MatchResult() {
        this.skillsTheyTeachMe = new ArrayList<>();
        this.skillsITeachThem = new ArrayList<>();
    }

    public MatchResult(User partner) {
        this.partner = partner;
        this.skillsTheyTeachMe = new ArrayList<>();
        this.skillsITeachThem = new ArrayList<>();
    }

    // Comparable implementation: Ranks candidates descending by overall score
    @Override
    public int compareTo(MatchResult other) {
        if (other == null) {
            return -1;
        }
        return Double.compare(other.overallScore, this.overallScore);
    }

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner = partner;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(double skillScore) {
        this.skillScore = skillScore;
    }

    public double getReciprocityScore() {
        return reciprocityScore;
    }

    public void setReciprocityScore(double reciprocityScore) {
        this.reciprocityScore = reciprocityScore;
    }

    public double getAvailabilityScore() {
        return availabilityScore;
    }

    public void setAvailabilityScore(double availabilityScore) {
        this.availabilityScore = availabilityScore;
    }

    public double getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(double reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public boolean isReciprocal() {
        return isReciprocal;
    }

    public void setReciprocal(boolean reciprocal) {
        isReciprocal = reciprocal;
    }

    public List<String> getSkillsTheyTeachMe() {
        return skillsTheyTeachMe;
    }

    public void setSkillsTheyTeachMe(List<String> skillsTheyTeachMe) {
        this.skillsTheyTeachMe = skillsTheyTeachMe;
    }

    public List<String> getSkillsITeachThem() {
        return skillsITeachThem;
    }

    public void setSkillsITeachThem(List<String> skillsITeachThem) {
        this.skillsITeachThem = skillsITeachThem;
    }
}
