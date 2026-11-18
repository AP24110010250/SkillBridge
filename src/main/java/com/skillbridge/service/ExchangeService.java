package com.skillbridge.service;

import java.sql.Timestamp;
import java.util.List;
import com.skillbridge.dao.ExchangeRequestDao;
import com.skillbridge.dao.ExchangeSessionDao;
import com.skillbridge.dao.MessageDao;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.Message;
import com.skillbridge.util.NotificationManager;

// Service coordinating exchange requests, sessions, and messaging
public class ExchangeService {

    private final ExchangeRequestDao requestDao;
    private final ExchangeSessionDao sessionDao;
    private final MessageDao messageDao;
    private final NotificationManager notificationManager;

    public ExchangeService() {
        this.requestDao = new ExchangeRequestDao();
        this.sessionDao = new ExchangeSessionDao();
        this.messageDao = new MessageDao();
        this.notificationManager = NotificationManager.getInstance();
    }

    public ExchangeService(ExchangeRequestDao requestDao, ExchangeSessionDao sessionDao, MessageDao messageDao) {
        this.requestDao = requestDao;
        this.sessionDao = sessionDao;
        this.messageDao = messageDao;
        this.notificationManager = NotificationManager.getInstance();
    }

    // 1. Create a new skill exchange request
    public ExchangeRequest createRequest(int senderId, int receiverId, int offeredSkillId, int wantedSkillId, String message)
            throws DatabaseException {

        if (senderId == receiverId) {
            throw new IllegalArgumentException("Cannot send exchange request to yourself");
        }

        ExchangeRequest req = new ExchangeRequest(0, senderId, receiverId, offeredSkillId, wantedSkillId, "PENDING", message);
        requestDao.insert(req);

        // Dispatch background notification
        notificationManager.sendNotification("New skill exchange request sent to user #" + receiverId + " from user #" + senderId);

        return req;
    }

    // 2. Accept an exchange request (Enables chat; session scheduled via agreement)
    public boolean acceptRequest(int requestId, Timestamp sessionTime, String meetingVenue) throws DatabaseException {
        ExchangeRequest req = requestDao.findById(requestId);
        if (req == null) {
            return false;
        }

        boolean updated = requestDao.updateStatus(requestId, "ACCEPTED");
        if (updated) {
            Timestamp scheduled = sessionTime != null ? sessionTime : new Timestamp(System.currentTimeMillis() + 86400000L); // Default +24h
            String venue = (meetingVenue != null && !meetingVenue.trim().isEmpty()) ? meetingVenue.trim() : "SkillBridge Campus Study Room";

            ExchangeSession session = new ExchangeSession(0, requestId, scheduled, 60, venue, "SCHEDULED", "Peer skill exchange agreed via chat");
            sessionDao.insert(session);

            notificationManager.sendNotification("Exchange request #" + requestId + " accepted! Peer chat unlocked & session scheduled.");
            return true;
        }
        return false;
    }

    // 3. Reject an exchange request
    public boolean rejectRequest(int requestId) throws DatabaseException {
        boolean updated = requestDao.updateStatus(requestId, "REJECTED");
        if (updated) {
            notificationManager.sendNotification("Exchange request #" + requestId + " was rejected.");
        }
        return updated;
    }

    // 4. Cancel a pending request
    public boolean cancelRequest(int requestId) throws DatabaseException {
        return requestDao.updateStatus(requestId, "CANCELLED");
    }

    // 5. Query requests
    public List<ExchangeRequest> getReceivedRequests(int userId) throws DatabaseException {
        return requestDao.findByReceiverId(userId);
    }

    public List<ExchangeRequest> getSentRequests(int userId) throws DatabaseException {
        return requestDao.findBySenderId(userId);
    }

    // 6. Session management
    public List<ExchangeSession> getUserSessions(int userId) throws DatabaseException {
        return sessionDao.findByUserId(userId);
    }

    public boolean completeSession(int sessionId, String completionNotes) throws DatabaseException {
        ExchangeSession session = sessionDao.findById(sessionId);
        if (session == null) {
            return false;
        }
        session.setNotes(completionNotes);
        return sessionDao.updateStatus(sessionId, "COMPLETED");
    }

    public boolean cancelSession(int sessionId) throws DatabaseException {
        return sessionDao.updateStatus(sessionId, "CANCELLED");
    }

    // 7. Messaging
    public Message sendMessage(int senderId, int receiverId, String text) throws DatabaseException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty");
        }

        Message msg = new Message(0, senderId, receiverId, text.trim(), false);
        messageDao.insert(msg);

        notificationManager.sendNotification("New message sent from user #" + senderId + " to user #" + receiverId);
        return msg;
    }

    public List<Message> getConversation(int user1Id, int user2Id) throws DatabaseException {
        messageDao.markConversationAsRead(user2Id, user1Id);
        return messageDao.getConversation(user1Id, user2Id);
    }
}
