package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.List;
import com.skillbridge.model.AchievementBadge;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.model.Review;
import com.skillbridge.service.ExchangeService;
import com.skillbridge.service.GamificationService;

public class GamificationTest {

    @Test
    public void testReviewsPointsAndBadges() throws Exception {
        ExchangeService exchangeService = new ExchangeService();
        GamificationService gamificationService = new GamificationService();

        // 1. Setup a completed session between User 1 (Koushik) and User 2 (Rahul)
        ExchangeRequest req = exchangeService.createRequest(1, 2, 1, 4, "Session for review test");
        exchangeService.acceptRequest(req.getRequestId(), new Timestamp(System.currentTimeMillis()), "https://meet.jit.si/gamification-test");

        List<ExchangeSession> sessions = exchangeService.getUserSessions(2);
        ExchangeSession testSession = null;
        for (ExchangeSession s : sessions) {
            if (s.getRequestId() == req.getRequestId()) {
                testSession = s;
                break;
            }
        }
        assertNotNull(testSession);
        final int targetSessionId = testSession.getSessionId();
        exchangeService.completeSession(targetSessionId, "Session complete");

        // 2. Submit Review
        int initialPoints = gamificationService.getUserPoints(2);
        Review review = gamificationService.submitReview(targetSessionId, 1, 2, 5, "Outstanding mentor in Photoshop!");
        assertNotNull(review);
        assertEquals(5, review.getRating());

        // 3. Verify Points awarded
        int updatedPoints = gamificationService.getUserPoints(2);
        assertEquals(initialPoints + 50, updatedPoints);

        // 4. Verify Duplicate Review rejection
        assertThrows(IllegalArgumentException.class, () -> {
            gamificationService.submitReview(targetSessionId, 1, 2, 4, "Duplicate review attempt");
        });

        // 5. Verify Unlocked Badges
        List<AchievementBadge> badges = gamificationService.getUserBadges(2);
        assertFalse(badges.isEmpty(), "User 2 should have unlocked milestone badges");
    }

    @Test
    public void testLearningGoalProgressTracking() throws Exception {
        GamificationService gamificationService = new GamificationService();

        // Create learning goal for user 1 (Koushik) to learn Photoshop (skill 4) in 5 sessions
        LearningGoal goal = gamificationService.createLearningGoal(1, 4, "Master Digital Art & Photoshop", 5);
        assertNotNull(goal);
        assertTrue(goal.getGoalId() > 0);
        assertEquals(0.0, goal.getProgressPercentage());
        assertEquals("IN_PROGRESS", goal.getStatus());

        // Update progress: completed 3 out of 5 sessions -> 60%
        gamificationService.updateGoalProgress(goal.getGoalId(), 3, 5);
        List<LearningGoal> goals = gamificationService.getUserGoals(1);
        LearningGoal updatedGoal = null;
        for (LearningGoal g : goals) {
            if (g.getGoalId() == goal.getGoalId()) {
                updatedGoal = g;
                break;
            }
        }
        assertNotNull(updatedGoal);
        assertEquals(60.0, updatedGoal.getProgressPercentage(), 0.01);
        assertEquals("IN_PROGRESS", updatedGoal.getStatus());

        // Complete remaining sessions: 5 out of 5 -> 100% COMPLETED
        gamificationService.updateGoalProgress(goal.getGoalId(), 5, 5);
        goals = gamificationService.getUserGoals(1);
        for (LearningGoal g : goals) {
            if (g.getGoalId() == goal.getGoalId()) {
                updatedGoal = g;
                break;
            }
        }
        assertNotNull(updatedGoal);
        assertEquals(100.0, updatedGoal.getProgressPercentage(), 0.01);
        assertEquals("COMPLETED", updatedGoal.getStatus());
    }
}
