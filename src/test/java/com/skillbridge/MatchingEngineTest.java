package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.skillbridge.matching.FairOpportunityAllocator;
import com.skillbridge.matching.MatchResult;
import com.skillbridge.matching.MatchingEngine;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;

public class MatchingEngineTest {

    @Test
    public void testReciprocalVsOneWayMatching() {
        MatchingEngine engine = new MatchingEngine();

        // User 1: Koushik (Teaches Java [1], Wants Photoshop [4])
        User koushik = new Student(1, "Koushik", "k@test.com", "pass", "Bio", "Weekdays 5 PM", 98.0);
        List<UserSkillOffered> koushikOffered = List.of(new UserSkillOffered(1, 1, 1, "Java", "Advanced", 2));
        List<UserSkillWanted> koushikWanted = List.of(new UserSkillWanted(1, 1, 4, "Photoshop", "Intermediate", "High"));

        // User 2: Rahul (Teaches Photoshop [4], Wants Java [1]) -> Reciprocal Match!
        User rahul = new Student(2, "Rahul", "r@test.com", "pass", "Bio", "Weekdays 6 PM", 95.0);
        List<UserSkillOffered> rahulOffered = List.of(new UserSkillOffered(2, 2, 4, "Photoshop", "Advanced", 3));
        List<UserSkillWanted> rahulWanted = List.of(new UserSkillWanted(2, 2, 1, "Java", "Intermediate", "High"));

        // User 3: Arjun (Teaches Python [2], Wants Java [1]) -> One-way Match (Koushik can teach him, but Arjun teaches Python which Koushik didn't ask for)
        User arjun = new Student(3, "Arjun", "a@test.com", "pass", "Bio", "Weekends", 90.0);
        List<UserSkillOffered> arjunOffered = List.of(new UserSkillOffered(3, 3, 2, "Python", "Advanced", 2));
        List<UserSkillWanted> arjunWanted = List.of(new UserSkillWanted(3, 3, 1, "Java", "Intermediate", "Medium"));

        // Evaluate Koushik and Rahul
        MatchResult matchKoushikRahul = engine.evaluateDirect(
                koushik, koushikOffered, koushikWanted,
                rahul, rahulOffered, rahulWanted
        );

        assertTrue(matchKoushikRahul.isReciprocal());
        assertTrue(matchKoushikRahul.getOverallScore() >= 75.0, "Reciprocal match score should be high");
        assertEquals(1, matchKoushikRahul.getSkillsTheyTeachMe().size());
        assertEquals("Photoshop", matchKoushikRahul.getSkillsTheyTeachMe().get(0));
        assertEquals(1, matchKoushikRahul.getSkillsITeachThem().size());
        assertEquals("Java", matchKoushikRahul.getSkillsITeachThem().get(0));

        // Evaluate Koushik and Arjun
        MatchResult matchKoushikArjun = engine.evaluateDirect(
                koushik, koushikOffered, koushikWanted,
                arjun, arjunOffered, arjunWanted
        );

        assertFalse(matchKoushikArjun.isReciprocal());
        assertTrue(matchKoushikArjun.getSkillsTheyTeachMe().isEmpty(), "Arjun cannot teach Koushik Photoshop");
        assertEquals("Java", matchKoushikArjun.getSkillsITeachThem().get(0));
        assertTrue(matchKoushikRahul.getOverallScore() > matchKoushikArjun.getOverallScore(),
                "Reciprocal match score should be significantly higher than one-way match score");
    }

    @Test
    public void testFairAllocationAndRanking() {
        FairOpportunityAllocator allocator = new FairOpportunityAllocator();

        User mentor1 = new Student(10, "Mentor Busy", "busy@test.com", "pass", "Bio", "Weekdays", 95.0);
        User mentor2 = new Student(11, "Mentor Free", "free@test.com", "pass", "Bio", "Weekdays", 95.0);

        MatchResult match1 = new MatchResult(mentor1);
        match1.setOverallScore(85.0);

        MatchResult match2 = new MatchResult(mentor2);
        match2.setOverallScore(82.0);

        List<MatchResult> list = new ArrayList<>();
        list.add(match1);
        list.add(match2);

        // Mentor 1 has 4 active ongoing sessions, Mentor 2 has 0 active sessions
        Map<Integer, Integer> activeSessions = new HashMap<>();
        activeSessions.put(10, 4);
        activeSessions.put(11, 0);

        List<MatchResult> fairList = allocator.allocateFairly(list, activeSessions);

        // Mentor 2 (free) should now rank above Mentor 1 (busy) due to fair load balancing
        assertEquals(11, fairList.get(0).getPartner().getUserId());
        assertEquals(10, fairList.get(1).getPartner().getUserId());

        // Verify TreeSet ordering
        TreeSet<MatchResult> sortedSet = allocator.getRankedTreeSet(fairList);
        assertEquals(2, sortedSet.size());
        assertEquals(11, sortedSet.first().getPartner().getUserId());
    }

    @Test
    public void testDatabaseMatching() throws Exception {
        MatchingEngine engine = new MatchingEngine();
        // Test matching for user 1 (Koushik) against real database records
        List<MatchResult> matches = engine.findMatchesForUser(1);

        assertNotNull(matches);
        assertFalse(matches.isEmpty(), "Should find matches for user 1");

        // The top match for Koushik should be Rahul (User 2) with reciprocal Photoshop/Java exchange
        MatchResult topMatch = matches.get(0);
        assertEquals(2, topMatch.getPartner().getUserId());
        assertTrue(topMatch.isReciprocal());
    }
}
