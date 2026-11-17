package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for skills offered by users
public class UserSkillOfferedDao {

    public boolean insert(UserSkillOffered offered) throws DatabaseException {
        String sql = "INSERT INTO user_skills_offered (user_id, skill_id, proficiency_level, years_experience) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, offered.getUserId());
            statement.setInt(2, offered.getSkillId());
            statement.setString(3, offered.getProficiencyLevel());
            statement.setInt(4, offered.getYearsExperience());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    offered.setOfferedId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding offered skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public List<UserSkillOffered> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT uso.*, s.skill_name FROM user_skills_offered uso "
                + "JOIN skills s ON uso.skill_id = s.skill_id "
                + "WHERE uso.user_id = ? ORDER BY s.skill_name";
        List<UserSkillOffered> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                list.add(new UserSkillOffered(
                        resultSet.getInt("offered_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("skill_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("proficiency_level"),
                        resultSet.getInt("years_experience")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding offered skills by user: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean exists(int userId, int skillId) throws DatabaseException {
        String sql = "SELECT 1 FROM user_skills_offered WHERE user_id = ? AND skill_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, skillId);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DatabaseException("Error checking offered skill existence: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean update(UserSkillOffered offered) throws DatabaseException {
        String sql = "UPDATE user_skills_offered SET proficiency_level = ?, years_experience = ? WHERE offered_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, offered.getProficiencyLevel());
            statement.setInt(2, offered.getYearsExperience());
            statement.setInt(3, offered.getOfferedId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating offered skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public boolean delete(int offeredId) throws DatabaseException {
        String sql = "DELETE FROM user_skills_offered WHERE offered_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, offeredId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting offered skill: " + e.getMessage(), e);
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
