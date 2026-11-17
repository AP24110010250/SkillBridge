package com.skillbridge.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.skillbridge.dao.UserDao;
import com.skillbridge.dao.UserSkillOfferedDao;
import com.skillbridge.dao.UserSkillWantedDao;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;

// Intelligent Matching Engine providing partner discovery, ranking, and fair allocation
public class MatchingEngine {

    private final MatchingStrategy matchingStrategy;
    private final FairOpportunityAllocator fairAllocator;
    private final UserDao userDao;
    private final UserSkillOfferedDao offeredDao;
    private final UserSkillWantedDao wantedDao;

    public MatchingEngine() {
        this.matchingStrategy = new DefaultMatchingStrategy();
        this.fairAllocator = new FairOpportunityAllocator();
        this.userDao = new UserDao();
        this.offeredDao = new UserSkillOfferedDao();
        this.wantedDao = new UserSkillWantedDao();
    }

    public MatchingEngine(MatchingStrategy strategy, FairOpportunityAllocator allocator,
                          UserDao userDao, UserSkillOfferedDao offeredDao, UserSkillWantedDao wantedDao) {
        this.matchingStrategy = strategy;
        this.fairAllocator = allocator;
        this.userDao = userDao;
        this.offeredDao = offeredDao;
        this.wantedDao = wantedDao;
    }

    // Finds and ranks compatible skill exchange partners for a given user
    public List<MatchResult> findMatchesForUser(int userId) throws DatabaseException {
        User currentUser = userDao.findById(userId);
        if (currentUser == null) {
            return new ArrayList<>();
        }

        List<UserSkillOffered> myOffered = offeredDao.findByUserId(userId);
        List<UserSkillWanted> myWanted = wantedDao.findByUserId(userId);

        List<User> allUsers = userDao.findAll();
        List<MatchResult> rawMatches = new ArrayList<>();

        for (User candidate : allUsers) {
            // Exclude self and admin accounts
            if (candidate.getUserId() == userId || "ADMIN".equalsIgnoreCase(candidate.getRole())) {
                continue;
            }

            List<UserSkillOffered> candidateOffered = offeredDao.findByUserId(candidate.getUserId());
            List<UserSkillWanted> candidateWanted = wantedDao.findByUserId(candidate.getUserId());

            MatchResult match = matchingStrategy.evaluateMatch(
                    currentUser, myOffered, myWanted,
                    candidate, candidateOffered, candidateWanted
            );

            // Only consider candidates with a positive match score
            if (match.getOverallScore() > 0.0) {
                rawMatches.add(match);
            }
        }

        // Apply fair allocation adjustments
        Map<Integer, Integer> activeSessionLoadMap = new HashMap<>();
        List<MatchResult> rankedMatches = fairAllocator.allocateFairly(rawMatches, activeSessionLoadMap);

        return rankedMatches;
    }

    // Direct in-memory matching calculation (independent of database, ideal for testing)
    public MatchResult evaluateDirect(
            User user1, List<UserSkillOffered> offered1, List<UserSkillWanted> wanted1,
            User user2, List<UserSkillOffered> offered2, List<UserSkillWanted> wanted2) {
        return matchingStrategy.evaluateMatch(user1, offered1, wanted1, user2, offered2, wanted2);
    }
}
