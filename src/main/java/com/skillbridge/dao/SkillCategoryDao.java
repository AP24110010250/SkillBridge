package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for SkillCategory operations
public class SkillCategoryDao implements BaseDao<SkillCategory> {

    @Override
    public boolean insert(SkillCategory category) throws DatabaseException {
        String sql = "INSERT INTO skill_categories (category_name, description) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting skill category: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    @Override
    public SkillCategory findById(int id) throws DatabaseException {
        String sql = "SELECT category_id, category_name, description FROM skill_categories WHERE category_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new SkillCategory(
                        resultSet.getInt("category_id"),
                        resultSet.getString("category_name"),
                        resultSet.getString("description")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding skill category by id: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public List<SkillCategory> findAll() throws DatabaseException {
        String sql = "SELECT category_id, category_name, description FROM skill_categories ORDER BY category_name";
        List<SkillCategory> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                SkillCategory category = new SkillCategory(
                        resultSet.getInt("category_id"),
                        resultSet.getString("category_name"),
                        resultSet.getString("description")
                );
                categories.add(category);
            }
            return categories;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all skill categories: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public boolean update(SkillCategory category) throws DatabaseException {
        String sql = "UPDATE skill_categories SET category_name = ?, description = ? WHERE category_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, category.getCategoryId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating skill category: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM skill_categories WHERE category_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting skill category: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    // Helper methods for resource cleanup
    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}
