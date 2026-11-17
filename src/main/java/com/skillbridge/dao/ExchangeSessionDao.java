package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Exchange Sessions
public class ExchangeSessionDao {

    public boolean insert(ExchangeSession session) throws DatabaseException {
        String sql = "INSERT INTO exchange_sessions (request_id, scheduled_time, duration_minutes, meeting_link, status, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, session.getRequestId());
            statement.setTimestamp(2, session.getScheduledTime());
            statement.setInt(3, session.getDurationMinutes());
            statement.setString(4, session.getMeetingLink());
            statement.setString(5, session.getStatus());
            statement.setString(6, session.getNotes());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    session.setSessionId(keys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating exchange session: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public ExchangeSession findById(int sessionId) throws DatabaseException {
        String sql = "SELECT * FROM exchange_sessions WHERE session_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, sessionId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToSession(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding exchange session: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<ExchangeSession> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT es.* FROM exchange_sessions es "
                + "JOIN exchange_requests er ON es.request_id = er.request_id "
                + "WHERE er.sender_id = ? OR er.receiver_id = ? "
                + "ORDER BY es.scheduled_time ASC";
        List<ExchangeSession> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                list.add(mapResultSetToSession(resultSet));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding sessions for user: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean updateStatus(int sessionId, String status) throws DatabaseException {
        String sql = "UPDATE exchange_sessions SET status = ? WHERE session_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, sessionId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating session status: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    private ExchangeSession mapResultSetToSession(ResultSet rs) throws SQLException {
        ExchangeSession s = new ExchangeSession();
        s.setSessionId(rs.getInt("session_id"));
        s.setRequestId(rs.getInt("request_id"));
        s.setScheduledTime(rs.getTimestamp("scheduled_time"));
        s.setDurationMinutes(rs.getInt("duration_minutes"));
        s.setMeetingLink(rs.getString("meeting_link"));
        s.setStatus(rs.getString("status"));
        s.setNotes(rs.getString("notes"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
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
