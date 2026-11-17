package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.UserSkillWanted;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for skills wanted by users
public class UserSkillWantedDao {

    public boolean insert(UserSkillWanted wanted) throws DatabaseException {
        String sql = "INSERT INTO user_skills_wanted (user_id, skill_id, target_level, priority) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, wanted.getUserId());
            statement.setInt(2, wanted.getSkillId());
            statement.setString(3, wanted.getTargetLevel());
            statement.setString(4, wanted.getPriority());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    wanted.setWantedId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding wanted skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public List<UserSkillWanted> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT usw.*, s.skill_name FROM user_skills_wanted usw "
                + "JOIN skills s ON usw.skill_id = s.skill_id "
                + "WHERE usw.user_id = ? ORDER BY s.skill_name";
        List<UserSkillWanted> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                list.add(new UserSkillWanted(
                        resultSet.getInt("wanted_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("skill_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getString("target_level"),
                        resultSet.getString("priority")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding wanted skills by user: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean exists(int userId, int skillId) throws DatabaseException {
        String sql = "SELECT 1 FROM user_skills_wanted WHERE user_id = ? AND skill_id = ?";
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
            throw new DatabaseException("Error checking wanted skill existence: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean update(UserSkillWanted wanted) throws DatabaseException {
        String sql = "UPDATE user_skills_wanted SET target_level = ?, priority = ? WHERE wanted_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, wanted.getTargetLevel());
            statement.setString(2, wanted.getPriority());
            statement.setInt(3, wanted.getWantedId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating wanted skill: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public boolean delete(int wantedId) throws DatabaseException {
        String sql = "DELETE FROM user_skills_wanted WHERE wanted_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, wantedId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting wanted skill: " + e.getMessage(), e);
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
