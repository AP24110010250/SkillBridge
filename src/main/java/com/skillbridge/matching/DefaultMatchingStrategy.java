package com.skillbridge.matching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;

// Default intelligent matching strategy evaluating skill compatibility, reciprocity, availability, and reliability
public class DefaultMatchingStrategy implements MatchingStrategy {

    @Override
    public MatchResult evaluateMatch(
            User currentUser,
            List<UserSkillOffered> myOffered,
            List<UserSkillWanted> myWanted,
            User candidate,
            List<UserSkillOffered> candidateOffered,
            List<UserSkillWanted> candidateWanted) {

        MatchResult result = new MatchResult(candidate);

        // 1. Identify skills candidate can teach me (their offered vs my wanted)
        List<String> canTeachMe = new ArrayList<>();
        Set<Integer> myWantedSkillIds = new HashSet<>();
        for (UserSkillWanted wanted : myWanted) {
            myWantedSkillIds.add(wanted.getSkillId());
        }

        for (UserSkillOffered offered : candidateOffered) {
            if (myWantedSkillIds.contains(offered.getSkillId())) {
                canTeachMe.add(offered.getSkillName());
            }
        }
        result.setSkillsTheyTeachMe(canTeachMe);

        // 2. Identify skills I can teach candidate (my offered vs their wanted)
        List<String> canTeachThem = new ArrayList<>();
        Set<Integer> candidateWantedSkillIds = new HashSet<>();
        for (UserSkillWanted wanted : candidateWanted) {
            candidateWantedSkillIds.add(wanted.getSkillId());
        }

        for (UserSkillOffered offered : myOffered) {
            if (candidateWantedSkillIds.contains(offered.getSkillId())) {
                canTeachThem.add(offered.getSkillName());
            }
        }
        result.setSkillsITeachThem(canTeachThem);

        // If neither user can teach the other anything, no match
        if (canTeachMe.isEmpty() && canTeachThem.isEmpty()) {
            result.setOverallScore(0.0);
            return result;
        }

        // 3. Compute Skill Compatibility Score (Max 50 points)
        // If they can teach me skills I want, score up to 50 points based on match count
        double skillScore = 0.0;
        if (!canTeachMe.isEmpty()) {
            skillScore = Math.min(35.0 + (canTeachMe.size() - 1) * 10.0, 50.0);
        } else if (!canTeachThem.isEmpty()) {
            // One-way: I can teach them, but they can't teach me
            skillScore = 15.0;
        }
        result.setSkillScore(skillScore);

        // 4. Compute Reciprocity Score (Max 25 points)
        // True two-way reciprocal exchange gets full 25 points
        boolean reciprocal = !canTeachMe.isEmpty() && !canTeachThem.isEmpty();
        result.setReciprocal(reciprocal);
        double reciprocityScore = reciprocal ? 25.0 : 0.0;
        result.setReciprocityScore(reciprocityScore);

        // 5. Compute Availability Overlap Score (Max 15 points)
        double availabilityScore = calculateAvailabilityScore(currentUser.getAvailability(), candidate.getAvailability());
        result.setAvailabilityScore(availabilityScore);

        // 6. Compute Reliability Score (Max 10 points)
        // Candidate reliability score is stored out of 100
        double candidateReliability = candidate.getReliabilityScore();
        double reliabilityScore = (candidateReliability / 100.0) * 10.0;
        result.setReliabilityScore(reliabilityScore);

        // 7. Calculate Total Score (Max 100 points)
        double total = skillScore + reciprocityScore + availabilityScore + reliabilityScore;
        result.setOverallScore(Math.min(total, 100.0));

        return result;
    }

    // Helper to calculate keyword overlap in availability strings (e.g. "Weekdays", "Weekends", "Evenings")
    private double calculateAvailabilityScore(String myAvailability, String partnerAvailability) {
        if (myAvailability == null || partnerAvailability == null) {
            return 5.0; // neutral default
        }

        String myLower = myAvailability.toLowerCase();
        String partnerLower = partnerAvailability.toLowerCase();

        String[] tokens = {"weekday", "weekend", "morning", "afternoon", "evening", "night", "full-time"};
        int matches = 0;

        for (String token : tokens) {
            if (myLower.contains(token) && partnerLower.contains(token)) {
                matches++;
            }
        }

        if (matches >= 2) {
            return 15.0;
        } else if (matches == 1) {
            return 10.0;
        } else {
            return 5.0;
        }
    }
}
