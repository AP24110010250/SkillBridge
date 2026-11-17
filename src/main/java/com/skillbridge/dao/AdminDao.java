package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.PlatformStatistics;
import com.skillbridge.model.UserReport;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Admin actions, User Reports, and Platform Metrics
public class AdminDao {

    public boolean insertReport(UserReport report) throws DatabaseException {
        String sql = "INSERT INTO user_reports (reporter_id, reported_user_id, reason, status) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, report.getReporterId());
            statement.setInt(2, report.getReportedUserId());
            statement.setString(3, report.getReason());
            statement.setString(4, report.getStatus());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    report.setReportId(keys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error submitting user report: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public List<UserReport> findAllReports() throws DatabaseException {
        String sql = "SELECT ur.*, u1.full_name AS reporter_name, u2.full_name AS reported_name "
                + "FROM user_reports ur "
                + "JOIN users u1 ON ur.reporter_id = u1.user_id "
                + "JOIN users u2 ON ur.reported_user_id = u2.user_id "
                + "ORDER BY ur.created_at DESC";
        List<UserReport> list = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                UserReport r = new UserReport();
                r.setReportId(resultSet.getInt("report_id"));
                r.setReporterId(resultSet.getInt("reporter_id"));
                r.setReporterName(resultSet.getString("reporter_name"));
                r.setReportedUserId(resultSet.getInt("reported_user_id"));
                r.setReportedUserName(resultSet.getString("reported_name"));
                r.setReason(resultSet.getString("reason"));
                r.setStatus(resultSet.getString("status"));
                r.setCreatedAt(resultSet.getTimestamp("created_at"));
                list.add(r);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user reports: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean updateReportStatus(int reportId, String status) throws DatabaseException {
        String sql = "UPDATE user_reports SET status = ? WHERE report_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, reportId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating report status: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public PlatformStatistics getPlatformStatistics() throws DatabaseException {
        PlatformStatistics stats = new PlatformStatistics();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();

            // Total users
            rs = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                stats.setTotalUsers(rs.getInt(1));
            }
            rs.close();

            // Total skills
            rs = statement.executeQuery("SELECT COUNT(*) FROM skills");
            if (rs.next()) {
                stats.setTotalSkills(rs.getInt(1));
            }
            rs.close();

            // Total exchange requests
            rs = statement.executeQuery("SELECT COUNT(*) FROM exchange_requests");
            if (rs.next()) {
                stats.setTotalExchangeRequests(rs.getInt(1));
            }
            rs.close();

            // Completed sessions
            rs = statement.executeQuery("SELECT COUNT(*) FROM exchange_sessions WHERE status = 'COMPLETED'");
            if (rs.next()) {
                stats.setCompletedSessions(rs.getInt(1));
            }
            rs.close();

            // Pending reports
            rs = statement.executeQuery("SELECT COUNT(*) FROM user_reports WHERE status = 'PENDING'");
            if (rs.next()) {
                stats.setPendingReports(rs.getInt(1));
            }
            rs.close();

            // Platform average rating
            rs = statement.executeQuery("SELECT COALESCE(AVG(rating), 5.0) FROM reviews");
            if (rs.next()) {
                stats.setPlatformAverageRating(rs.getDouble(1));
            }
            rs.close();

            // Category distribution (demonstrating Map)
            Map<String, Integer> distMap = new HashMap<>();
            rs = statement.executeQuery("SELECT sc.category_name, COUNT(s.skill_id) AS cnt "
                    + "FROM skill_categories sc "
                    + "LEFT JOIN skills s ON sc.category_id = s.category_id "
                    + "GROUP BY sc.category_id, sc.category_name");
            while (rs.next()) {
                distMap.put(rs.getString("category_name"), rs.getInt("cnt"));
            }
            stats.setCategoryDistribution(distMap);

            return stats;
        } catch (SQLException e) {
            throw new DatabaseException("Error computing platform statistics: " + e.getMessage(), e);
        } finally {
            closeResources(rs, statement, connection);
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
