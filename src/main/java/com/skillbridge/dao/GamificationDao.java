package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.AchievementBadge;
import com.skillbridge.model.SkillPoint;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Gamification (Points & Badges)
public class GamificationDao {

    public boolean addPoints(int userId, int points, String reason) throws DatabaseException {
        String sql = "INSERT INTO skill_points (user_id, points, reason) VALUES (?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, points);
            statement.setString(3, reason);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error recording skill points: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public int getTotalPoints(int userId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(points), 0) AS total_pts FROM skill_points WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total_pts");
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving total points: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<SkillPoint> getPointHistory(int userId) throws DatabaseException {
        String sql = "SELECT * FROM skill_points WHERE user_id = ? ORDER BY created_at DESC";
        List<SkillPoint> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SkillPoint sp = new SkillPoint(
                        resultSet.getInt("point_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("points"),
                        resultSet.getString("reason")
                );
                sp.setCreatedAt(resultSet.getTimestamp("created_at"));
                list.add(sp);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving point history: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean unlockBadge(int userId, int badgeId) throws DatabaseException {
        String sql = "INSERT IGNORE INTO user_achievements (user_id, badge_id) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, badgeId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error unlocking badge: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public List<AchievementBadge> getUserBadges(int userId) throws DatabaseException {
        String sql = "SELECT ab.*, ua.unlocked_at FROM achievement_badges ab "
                + "JOIN user_achievements ua ON ab.badge_id = ua.badge_id "
                + "WHERE ua.user_id = ? ORDER BY ua.unlocked_at DESC";
        List<AchievementBadge> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AchievementBadge b = new AchievementBadge(
                        resultSet.getInt("badge_id"),
                        resultSet.getString("badge_name"),
                        resultSet.getString("badge_description"),
                        resultSet.getInt("required_points")
                );
                b.setUnlockedAt(resultSet.getTimestamp("unlocked_at"));
                list.add(b);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user badges: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<AchievementBadge> getAllBadges() throws DatabaseException {
        String sql = "SELECT * FROM achievement_badges ORDER BY required_points ASC";
        List<AchievementBadge> list = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                list.add(new AchievementBadge(
                        resultSet.getInt("badge_id"),
                        resultSet.getString("badge_name"),
                        resultSet.getString("badge_description"),
                        resultSet.getInt("required_points")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving master badges: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
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
