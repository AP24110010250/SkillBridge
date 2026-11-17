package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.Review;
import com.skillbridge.util.DatabaseConnection;

// Data Access Object for Reviews and Ratings
public class ReviewDao {

    public boolean insert(Review review) throws DatabaseException {
        String sql = "INSERT INTO reviews (session_id, reviewer_id, reviewee_id, rating, feedback) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, review.getSessionId());
            statement.setInt(2, review.getReviewerId());
            statement.setInt(3, review.getRevieweeId());
            statement.setInt(4, review.getRating());
            statement.setString(5, review.getFeedback());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    review.setReviewId(keys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error submitting review: " + e.getMessage(), e);
        } finally {
            closeResources(statement, connection);
        }
    }

    public boolean hasReviewed(int sessionId, int reviewerId) throws DatabaseException {
        String sql = "SELECT 1 FROM reviews WHERE session_id = ? AND reviewer_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, sessionId);
            statement.setInt(2, reviewerId);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DatabaseException("Error checking existing review: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public List<Review> findByRevieweeId(int revieweeId) throws DatabaseException {
        String sql = "SELECT r.*, u.full_name AS reviewer_name FROM reviews r "
                + "JOIN users u ON r.reviewer_id = u.user_id "
                + "WHERE r.reviewee_id = ? ORDER BY r.created_at DESC";
        List<Review> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, revieweeId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Review rev = new Review();
                rev.setReviewId(resultSet.getInt("review_id"));
                rev.setSessionId(resultSet.getInt("session_id"));
                rev.setReviewerId(resultSet.getInt("reviewer_id"));
                rev.setReviewerName(resultSet.getString("reviewer_name"));
                rev.setRevieweeId(resultSet.getInt("reviewee_id"));
                rev.setRating(resultSet.getInt("rating"));
                rev.setFeedback(resultSet.getString("feedback"));
                rev.setCreatedAt(resultSet.getTimestamp("created_at"));
                list.add(rev);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving reviews: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    public double calculateAverageRating(int revieweeId) throws DatabaseException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE reviewee_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, revieweeId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("avg_rating");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating average rating: " + e.getMessage(), e);
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
