package com.skillbridge.matching;

import java.util.List;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;

// Contract for intelligent match evaluation
public interface MatchingStrategy {

    MatchResult evaluateMatch(
            User currentUser,
            List<UserSkillOffered> myOffered,
            List<UserSkillWanted> myWanted,
            User candidate,
            List<UserSkillOffered> candidateOffered,
            List<UserSkillWanted> candidateWanted
    );
}
