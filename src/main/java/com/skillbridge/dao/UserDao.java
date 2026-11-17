package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.Admin;
import com.skillbridge.model.Mentor;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for User operations
public class UserDao implements BaseDao<User> {

    @Override
    public boolean insert(User user) throws DatabaseException {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, bio, availability, reliability_score) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getBio());
            statement.setString(6, user.getAvailability());
            statement.setDouble(7, user.getReliabilityScore());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating user: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    @Override
    public User findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by id: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public User findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by email: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        String sql = "SELECT * FROM users ORDER BY full_name";
        List<User> userList = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                userList.add(mapResultSetToUser(resultSet));
            }
            return userList;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all users: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public boolean update(User user) throws DatabaseException {
        String sql = "UPDATE users SET full_name = ?, bio = ?, availability = ?, reliability_score = ? WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getBio());
            statement.setString(3, user.getAvailability());
            statement.setDouble(4, user.getReliabilityScore());
            statement.setInt(5, user.getUserId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user profile: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public boolean updatePassword(int userId, String newPasswordHash) throws DatabaseException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, newPasswordHash);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating password: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    // Maps database record to polymorphic User subclass
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;

        if ("MENTOR".equalsIgnoreCase(role)) {
            user = new Mentor();
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            user = new Admin();
        } else {
            user = new Student();
        }

        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(role);
        user.setBio(rs.getString("bio"));
        user.setAvailability(rs.getString("availability"));
        user.setReliabilityScore(rs.getDouble("reliability_score"));
        user.setCreatedAt(rs.getTimestamp("created_at"));

        return user;
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
