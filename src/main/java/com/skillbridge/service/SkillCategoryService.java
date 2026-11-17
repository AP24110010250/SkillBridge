package com.skillbridge.service;

import java.util.List;
import com.skillbridge.dao.SkillCategoryDao;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.SkillCategory;

// Service layer handling Skill Category business logic
public class SkillCategoryService {

    private final SkillCategoryDao categoryDao;

    public SkillCategoryService() {
        this.categoryDao = new SkillCategoryDao();
    }

    // Constructor injection for testability
    public SkillCategoryService(SkillCategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public boolean addCategory(String categoryName, String description) throws DatabaseException {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        SkillCategory category = new SkillCategory();
        category.setCategoryName(categoryName.trim());
        category.setDescription(description != null ? description.trim() : "");

        return categoryDao.insert(category);
    }

    public SkillCategory getCategoryById(int id) throws DatabaseException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        return categoryDao.findById(id);
    }

    public List<SkillCategory> getAllCategories() throws DatabaseException {
        return categoryDao.findAll();
    }
}
