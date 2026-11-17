package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.List;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.Message;
import com.skillbridge.service.ExchangeService;

public class ExchangeWorkflowTest {

    @Test
    public void testCompleteExchangeWorkflow() throws Exception {
        ExchangeService exchangeService = new ExchangeService();

        // 1. User 1 (Koushik) sends request to User 2 (Rahul)
        // Koushik offers Java (1), wants Photoshop (4)
        ExchangeRequest req = exchangeService.createRequest(1, 2, 1, 4, "Hi Rahul, let's exchange Java for Photoshop lessons!");
        assertNotNull(req);
        assertTrue(req.getRequestId() > 0);
        assertEquals("PENDING", req.getStatus());

        // 2. User 2 checks received requests
        List<ExchangeRequest> received = exchangeService.getReceivedRequests(2);
        assertFalse(received.isEmpty());

        // 3. User 2 accepts the request
        Timestamp tomorrow = new Timestamp(System.currentTimeMillis() + 86400000L);
        boolean accepted = exchangeService.acceptRequest(req.getRequestId(), tomorrow, "https://meet.jit.si/skillbridge-session");
        assertTrue(accepted);

        // 4. Verify session created
        List<ExchangeSession> sessions = exchangeService.getUserSessions(2);
        assertFalse(sessions.isEmpty());
        // Retrieve the session corresponding to our new request
        ExchangeSession currentSession = null;
        for (ExchangeSession s : sessions) {
            if (s.getRequestId() == req.getRequestId()) {
                currentSession = s;
                break;
            }
        }
        assertNotNull(currentSession);
        assertEquals("SCHEDULED", currentSession.getStatus());

        // 5. Complete session
        boolean completed = exchangeService.completeSession(currentSession.getSessionId(), "Great learning exchange on Photoshop filters!");
        assertTrue(completed);

        // 6. Test direct chat messaging
        Message sentMsg = exchangeService.sendMessage(1, 2, "Thanks for the great Photoshop session, Rahul!");
        assertNotNull(sentMsg);
        assertTrue(sentMsg.getMessageId() > 0);

        List<Message> conversation = exchangeService.getConversation(1, 2);
        assertFalse(conversation.isEmpty());
        assertEquals("Thanks for the great Photoshop session, Rahul!", conversation.get(conversation.size() - 1).getMessageText());
    }
}
