package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.Message;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Direct Messages
public class MessageDao {

    public boolean insert(Message msg) throws DatabaseException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_text, is_read) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, msg.getSenderId());
            statement.setInt(2, msg.getReceiverId());
            statement.setString(3, msg.getMessageText());
            statement.setBoolean(4, msg.isRead());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    msg.setMessageId(keys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error sending message: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public List<Message> getConversation(int user1Id, int user2Id) throws DatabaseException {
        String sql = "SELECT m.*, u1.full_name AS sender_name, u2.full_name AS receiver_name FROM messages m "
                + "JOIN users u1 ON m.sender_id = u1.user_id "
                + "JOIN users u2 ON m.receiver_id = u2.user_id "
                + "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) "
                + "ORDER BY m.sent_at ASC";
        List<Message> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, user1Id);
            statement.setInt(2, user2Id);
            statement.setInt(3, user2Id);
            statement.setInt(4, user1Id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Message msg = new Message();
                msg.setMessageId(resultSet.getInt("message_id"));
                msg.setSenderId(resultSet.getInt("sender_id"));
                msg.setSenderName(resultSet.getString("sender_name"));
                msg.setReceiverId(resultSet.getInt("receiver_id"));
                msg.setReceiverName(resultSet.getString("receiver_name"));
                msg.setMessageText(resultSet.getString("message_text"));
                msg.setRead(resultSet.getBoolean("is_read"));
                msg.setSentAt(resultSet.getTimestamp("sent_at"));
                list.add(msg);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving conversation: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public boolean markConversationAsRead(int senderId, int receiverId) throws DatabaseException {
        String sql = "UPDATE messages SET is_read = TRUE WHERE sender_id = ? AND receiver_id = ? AND is_read = FALSE";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);

            return statement.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error marking messages as read: " + e.getMessage(), e);
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
