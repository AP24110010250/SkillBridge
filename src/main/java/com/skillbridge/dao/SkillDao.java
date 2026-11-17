package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.Skill;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for master Skill catalog operations
public class SkillDao implements BaseDao<Skill> {

    @Override
    public boolean insert(Skill skill) throws DatabaseException {
        String sql = "INSERT INTO skills (category_id, skill_name, description) VALUES (?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, skill.getCategoryId());
            statement.setString(2, skill.getSkillName());
            statement.setString(3, skill.getDescription());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    skill.setSkillId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    @Override
    public Skill findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM skills WHERE skill_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Skill(
                        resultSet.getInt("skill_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("description")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding skill by id: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public Skill findByName(String skillName) throws DatabaseException {
        String sql = "SELECT * FROM skills WHERE LOWER(skill_name) = LOWER(?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, skillName != null ? skillName.trim() : "");
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Skill(
                        resultSet.getInt("skill_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("description")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding skill by name: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public List<Skill> findAll() throws DatabaseException {
        String sql = "SELECT * FROM skills ORDER BY skill_name";
        List<Skill> skills = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                skills.add(new Skill(
                        resultSet.getInt("skill_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("description")
                ));
            }
            return skills;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving skills: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<Skill> findByCategoryId(int categoryId) throws DatabaseException {
        String sql = "SELECT * FROM skills WHERE category_id = ? ORDER BY skill_name";
        List<Skill> skills = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                skills.add(new Skill(
                        resultSet.getInt("skill_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("description")
                ));
            }
            return skills;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving skills by category: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<Skill> searchSkills(String keyword) throws DatabaseException {
        String sql = "SELECT * FROM skills WHERE LOWER(skill_name) LIKE ? OR LOWER(description) LIKE ? ORDER BY skill_name";
        List<Skill> skills = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            String pattern = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                skills.add(new Skill(
                        resultSet.getInt("skill_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("description")
                ));
            }
            return skills;
        } catch (SQLException e) {
            throw new DatabaseException("Error searching skills: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public boolean update(Skill skill) throws DatabaseException {
        String sql = "UPDATE skills SET category_id = ?, skill_name = ?, description = ? WHERE skill_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, skill.getCategoryId());
            statement.setString(2, skill.getSkillName());
            statement.setString(3, skill.getDescription());
            statement.setInt(4, skill.getSkillId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM skills WHERE skill_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
