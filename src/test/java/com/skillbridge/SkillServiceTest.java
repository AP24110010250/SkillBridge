package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import com.skillbridge.exception.DuplicateSkillException;
import com.skillbridge.model.Skill;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;
import com.skillbridge.service.SkillService;

public class SkillServiceTest {

    @Test
    public void testCatalogAndSearch() throws Exception {
        SkillService skillService = new SkillService();

        List<Skill> allSkills = skillService.getAllSkills();
        assertNotNull(allSkills);
        assertTrue(allSkills.size() >= 4);

        // Search for Java
        List<Skill> searchResults = skillService.searchSkills("java");
        assertFalse(searchResults.isEmpty());
        boolean hasJava = false;
        for (Skill s : searchResults) {
            if (s.getSkillName().equalsIgnoreCase("Java")) {
                hasJava = true;
                break;
            }
        }
        assertTrue(hasJava);

        // Test category grouping
        Map<String, List<Skill>> grouped = skillService.getSkillsGroupedByCategory();
        assertNotNull(grouped);
        assertTrue(grouped.containsKey("Programming & Tech"));
    }

    @Test
    public void testUserSkillsOfferedAndWanted() throws Exception {
        SkillService skillService = new SkillService();
        int testUserId = 1; // Koushik

        // Retrieve Koushik's offered skills
        List<UserSkillOffered> offeredList = skillService.getUserOfferedSkills(testUserId);
        assertNotNull(offeredList);
        assertFalse(offeredList.isEmpty());

        // Attempting to add an existing offered skill must throw DuplicateSkillException
        int existingSkillId = offeredList.get(0).getSkillId();
        assertThrows(DuplicateSkillException.class, () -> {
            skillService.addSkillOffered(testUserId, existingSkillId, "Expert", 3);
        });

        // Retrieve Koushik's wanted skills
        List<UserSkillWanted> wantedList = skillService.getUserWantedSkills(testUserId);
        assertNotNull(wantedList);
        assertFalse(wantedList.isEmpty());

        int existingWantedId = wantedList.get(0).getSkillId();
        assertThrows(DuplicateSkillException.class, () -> {
            skillService.addSkillWanted(testUserId, existingWantedId, "Advanced", "High");
        });
    }
}
