package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Exchange Requests
public class ExchangeRequestDao {

    public boolean insert(ExchangeRequest req) throws DatabaseException {
        String sql = "INSERT INTO exchange_requests (sender_id, receiver_id, offered_skill_id, wanted_skill_id, status, message) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, req.getSenderId());
            statement.setInt(2, req.getReceiverId());
            statement.setInt(3, req.getOfferedSkillId());
            statement.setInt(4, req.getWantedSkillId());
            statement.setString(5, req.getStatus());
            statement.setString(6, req.getMessage());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    req.setRequestId(keys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating exchange request: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public ExchangeRequest findById(int requestId) throws DatabaseException {
        String sql = "SELECT er.*, "
                + "u1.full_name AS sender_name, u2.full_name AS receiver_name, "
                + "s1.skill_name AS offered_skill_name, s2.skill_name AS wanted_skill_name "
                + "FROM exchange_requests er "
                + "JOIN users u1 ON er.sender_id = u1.user_id "
                + "JOIN users u2 ON er.receiver_id = u2.user_id "
                + "JOIN skills s1 ON er.offered_skill_id = s1.skill_id "
                + "JOIN skills s2 ON er.wanted_skill_id = s2.skill_id "
                + "WHERE er.request_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, requestId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToRequest(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding exchange request by id: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<ExchangeRequest> findByReceiverId(int receiverId) throws DatabaseException {
        String sql = "SELECT er.*, "
                + "u1.full_name AS sender_name, u2.full_name AS receiver_name, "
                + "s1.skill_name AS offered_skill_name, s2.skill_name AS wanted_skill_name "
                + "FROM exchange_requests er "
                + "JOIN users u1 ON er.sender_id = u1.user_id "
                + "JOIN users u2 ON er.receiver_id = u2.user_id "
                + "JOIN skills s1 ON er.offered_skill_id = s1.skill_id "
                + "JOIN skills s2 ON er.wanted_skill_id = s2.skill_id "
                + "WHERE er.receiver_id = ? ORDER BY er.created_at DESC";
        return queryRequestList(sql, receiverId);
    }

    public List<ExchangeRequest> findBySenderId(int senderId) throws DatabaseException {
        String sql = "SELECT er.*, "
                + "u1.full_name AS sender_name, u2.full_name AS receiver_name, "
                + "s1.skill_name AS offered_skill_name, s2.skill_name AS wanted_skill_name "
                + "FROM exchange_requests er "
                + "JOIN users u1 ON er.sender_id = u1.user_id "
                + "JOIN users u2 ON er.receiver_id = u2.user_id "
                + "JOIN skills s1 ON er.offered_skill_id = s1.skill_id "
                + "JOIN skills s2 ON er.wanted_skill_id = s2.skill_id "
                + "WHERE er.sender_id = ? ORDER BY er.created_at DESC";
        return queryRequestList(sql, senderId);
    }

    public boolean updateStatus(int requestId, String newStatus) throws DatabaseException {
        String sql = "UPDATE exchange_requests SET status = ? WHERE request_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, newStatus);
            statement.setInt(2, requestId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating exchange request status: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    private List<ExchangeRequest> queryRequestList(String sql, int paramId) throws DatabaseException {
        List<ExchangeRequest> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, paramId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                list.add(mapResultSetToRequest(resultSet));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error querying exchange requests: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    private ExchangeRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        ExchangeRequest req = new ExchangeRequest();
        req.setRequestId(rs.getInt("request_id"));
        req.setSenderId(rs.getInt("sender_id"));
        req.setSenderName(rs.getString("sender_name"));
        req.setReceiverId(rs.getInt("receiver_id"));
        req.setReceiverName(rs.getString("receiver_name"));
        req.setOfferedSkillId(rs.getInt("offered_skill_id"));
        req.setOfferedSkillName(rs.getString("offered_skill_name"));
        req.setWantedSkillId(rs.getInt("wanted_skill_id"));
        req.setWantedSkillName(rs.getString("wanted_skill_name"));
        req.setStatus(rs.getString("status"));
        req.setMessage(rs.getString("message"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        req.setUpdatedAt(rs.getTimestamp("updated_at"));
        return req;
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
