package com.skillbridge.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import com.skillbridge.model.User;

// Fair opportunity allocator preventing popular mentors from being monopolized
public class FairOpportunityAllocator {

    // Adjust candidate match scores based on their active workload / commitment count
    // A partner with fewer active sessions gets a fairness boost; an overloaded mentor has score slightly damped
    public List<MatchResult> allocateFairly(List<MatchResult> rawMatches, Map<Integer, Integer> activeSessionCountMap) {
        if (rawMatches == null || rawMatches.isEmpty()) {
            return new ArrayList<>();
        }

        List<MatchResult> fairMatches = new ArrayList<>();

        for (MatchResult match : rawMatches) {
            User partner = match.getPartner();
            int partnerId = partner.getUserId();
            int activeLoad = activeSessionCountMap != null ? activeSessionCountMap.getOrDefault(partnerId, 0) : 0;

            // Fairness adjustment penalty: -3 points per active ongoing session (max penalty 15 points)
            double fairnessPenalty = Math.min(activeLoad * 3.0, 15.0);
            double adjustedScore = Math.max(match.getOverallScore() - fairnessPenalty, 0.0);

            MatchResult adjusted = new MatchResult(partner);
            adjusted.setOverallScore(adjustedScore);
            adjusted.setSkillScore(match.getSkillScore());
            adjusted.setReciprocityScore(match.getReciprocityScore());
            adjusted.setAvailabilityScore(match.getAvailabilityScore());
            adjusted.setReliabilityScore(match.getReliabilityScore());
            adjusted.setReciprocal(match.isReciprocal());
            adjusted.setSkillsTheyTeachMe(match.getSkillsTheyTeachMe());
            adjusted.setSkillsITeachThem(match.getSkillsITeachThem());

            fairMatches.add(adjusted);
        }

        // Sort descending by score using Comparable
        Collections.sort(fairMatches);
        return fairMatches;
    }

    // Demonstrates TreeSet (SortedSet): returns uniquely sorted candidates by score
    public TreeSet<MatchResult> getRankedTreeSet(List<MatchResult> matches) {
        return new TreeSet<>(matches);
    }
}
