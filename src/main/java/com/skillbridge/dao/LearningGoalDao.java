package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Learning Goals and Progress
public class LearningGoalDao {

    public boolean insert(LearningGoal goal) throws DatabaseException {
        String sql = "INSERT INTO learning_goals (user_id, skill_id, title, target_sessions, completed_sessions, progress_percentage, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, goal.getUserId());
            statement.setInt(2, goal.getSkillId());
            statement.setString(3, goal.getTitle());
            statement.setInt(4, goal.getTargetSessions());
            statement.setInt(5, goal.getCompletedSessions());
            statement.setDouble(6, goal.getProgressPercentage());
            statement.setString(7, goal.getStatus());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    goal.setGoalId(keys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating learning goal: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public List<LearningGoal> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT lg.*, s.skill_name FROM learning_goals lg "
                + "JOIN skills s ON lg.skill_id = s.skill_id "
                + "WHERE lg.user_id = ? ORDER BY lg.created_at DESC";
        List<LearningGoal> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LearningGoal goal = new LearningGoal();
                goal.setGoalId(resultSet.getInt("goal_id"));
                goal.setUserId(resultSet.getInt("user_id"));
                goal.setSkillId(resultSet.getInt("skill_id"));
                goal.setSkillName(resultSet.getString("skill_name"));
                goal.setTitle(resultSet.getString("title"));
                goal.setTargetSessions(resultSet.getInt("target_sessions"));
                goal.setCompletedSessions(resultSet.getInt("completed_sessions"));
                goal.setProgressPercentage(resultSet.getDouble("progress_percentage"));
                goal.setStatus(resultSet.getString("status"));
                goal.setCreatedAt(resultSet.getTimestamp("created_at"));
                list.add(goal);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding learning goals: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean updateProgress(int goalId, int completedSessions, double progressPercentage, String status) throws DatabaseException {
        String sql = "UPDATE learning_goals SET completed_sessions = ?, progress_percentage = ?, status = ? WHERE goal_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, completedSessions);
            statement.setDouble(2, progressPercentage);
            statement.setString(3, status);
            statement.setInt(4, goalId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating learning goal progress: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public boolean delete(int goalId) throws DatabaseException {
        String sql = "DELETE FROM learning_goals WHERE goal_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, goalId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting learning goal: " + e.getMessage(), e);
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
