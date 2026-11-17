package com.skillbridge.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.skillbridge.dao.SkillCategoryDao;
import com.skillbridge.dao.SkillDao;
import com.skillbridge.dao.UserSkillOfferedDao;
import com.skillbridge.dao.UserSkillWantedDao;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.exception.DuplicateSkillException;
import com.skillbridge.model.Skill;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;

// Service handling Skill management, user skill assignments, search, and discovery
public class SkillService {

    private final SkillDao skillDao;
    private final SkillCategoryDao categoryDao;
    private final UserSkillOfferedDao offeredDao;
    private final UserSkillWantedDao wantedDao;

    public SkillService() {
        this.skillDao = new SkillDao();
        this.categoryDao = new SkillCategoryDao();
        this.offeredDao = new UserSkillOfferedDao();
        this.wantedDao = new UserSkillWantedDao();
    }

    public SkillService(SkillDao skillDao, SkillCategoryDao categoryDao, UserSkillOfferedDao offeredDao, UserSkillWantedDao wantedDao) {
        this.skillDao = skillDao;
        this.categoryDao = categoryDao;
        this.offeredDao = offeredDao;
        this.wantedDao = wantedDao;
    }

    // Add a new skill to the global catalog
    public Skill createSkill(int categoryId, String skillName, String description) throws DatabaseException, DuplicateSkillException {
        if (skillName == null || skillName.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be empty");
        }

        Skill existing = skillDao.findByName(skillName.trim());
        if (existing != null) {
            throw new DuplicateSkillException("Skill '" + skillName.trim() + "' already exists in the catalog");
        }

        Skill skill = new Skill(0, categoryId, skillName.trim(), description != null ? description.trim() : "");
        skillDao.insert(skill);
        return skill;
    }

    public List<Skill> getAllSkills() throws DatabaseException {
        return skillDao.findAll();
    }

    public List<Skill> getSkillsByCategory(int categoryId) throws DatabaseException {
        return skillDao.findByCategoryId(categoryId);
    }

    public List<Skill> searchSkills(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return skillDao.findAll();
        }
        return skillDao.searchSkills(keyword.trim());
    }

    // Add a skill that the user can teach
    public UserSkillOffered addSkillOffered(int userId, int skillId, String proficiency, int yearsExperience)
            throws DatabaseException, DuplicateSkillException {

        if (offeredDao.exists(userId, skillId)) {
            throw new DuplicateSkillException("You have already added this skill to your offered skills list");
        }

        UserSkillOffered offered = new UserSkillOffered();
        offered.setUserId(userId);
        offered.setSkillId(skillId);
        offered.setProficiencyLevel(proficiency != null ? proficiency : "Intermediate");
        offered.setYearsExperience(Math.max(yearsExperience, 0));

        offeredDao.insert(offered);
        return offered;
    }

    // Add a skill that the user wants to learn
    public UserSkillWanted addSkillWanted(int userId, int skillId, String targetLevel, String priority)
            throws DatabaseException, DuplicateSkillException {

        if (wantedDao.exists(userId, skillId)) {
            throw new DuplicateSkillException("You have already added this skill to your wanted skills list");
        }

        UserSkillWanted wanted = new UserSkillWanted();
        wanted.setUserId(userId);
        wanted.setSkillId(skillId);
        wanted.setTargetLevel(targetLevel != null ? targetLevel : "Intermediate");
        wanted.setPriority(priority != null ? priority : "Medium");

        wantedDao.insert(wanted);
        return wanted;
    }

    public List<UserSkillOffered> getUserOfferedSkills(int userId) throws DatabaseException {
        return offeredDao.findByUserId(userId);
    }

    public List<UserSkillWanted> getUserWantedSkills(int userId) throws DatabaseException {
        return wantedDao.findByUserId(userId);
    }

    public boolean removeSkillOffered(int offeredId) throws DatabaseException {
        return offeredDao.delete(offeredId);
    }

    public boolean removeSkillWanted(int wantedId) throws DatabaseException {
        return wantedDao.delete(wantedId);
    }

    // Demonstrates Collections: Set & Map for grouping and discovery
    public Map<String, List<Skill>> getSkillsGroupedByCategory() throws DatabaseException {
        Map<String, List<Skill>> groupedMap = new HashMap<>();
        List<SkillCategory> categories = categoryDao.findAll();
        List<Skill> allSkills = skillDao.findAll();

        Map<Integer, String> categoryNameMap = new HashMap<>();
        for (SkillCategory cat : categories) {
            categoryNameMap.put(cat.getCategoryId(), cat.getCategoryName());
            groupedMap.put(cat.getCategoryName(), new ArrayList<>());
        }

        for (Skill skill : allSkills) {
            String catName = categoryNameMap.getOrDefault(skill.getCategoryId(), "General");
            groupedMap.computeIfAbsent(catName, k -> new ArrayList<>()).add(skill);
        }

        return groupedMap;
    }

    // Set demonstration: Identify distinct skill names offered by a user
    public Set<String> getDistinctOfferedSkillNames(int userId) throws DatabaseException {
        List<UserSkillOffered> list = offeredDao.findByUserId(userId);
        Set<String> distinctNames = new HashSet<>();
        for (UserSkillOffered item : list) {
            if (item.getSkillName() != null) {
                distinctNames.add(item.getSkillName().toLowerCase());
            }
        }
        return distinctNames;
    }
}
