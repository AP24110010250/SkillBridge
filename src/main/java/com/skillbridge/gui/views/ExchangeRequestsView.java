package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.RequestCard;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.Skill;
import com.skillbridge.model.User;
import com.skillbridge.service.ExchangeService;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserService;

/**
 * Exchange Requests View: Manage incoming and outgoing skill swap requests with rich RequestCards.
 */
public class ExchangeRequestsView extends JPanel {

    private final MainFrame mainFrame;
    private final ExchangeService exchangeService;
    private final UserService userService;
    private final SkillService skillService;

    private JComboBox<String> filterCombo;
    private JPanel cardsContainer;
    private JLabel countLabel;

    private List<ExchangeRequest> allRequests = new ArrayList<>();
    private final Map<Integer, User> userCache = new HashMap<>();
    private final Map<Integer, Skill> skillCache = new HashMap<>();
    private User currentUser;

    public ExchangeRequestsView(MainFrame mainFrame, ExchangeService exchangeService,
                                UserService userService, SkillService skillService) {
        this.mainFrame = mainFrame;
        this.exchangeService = exchangeService;
        this.userService = userService;
        this.skillService = skillService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel pageTitle = new JLabel("Exchange Requests");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_MAIN);

        countLabel = new JLabel("0 requests");
        countLabel.setFont(UITheme.FONT_BODY);
        countLabel.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(pageTitle, BorderLayout.WEST);
        titlePanel.add(countLabel, BorderLayout.EAST);

        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 2. Filter Row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterLbl.setForeground(UITheme.TEXT_MAIN);

        filterCombo = new JComboBox<>(new String[]{
                "All Requests",
                "📥 Incoming (Received)",
                "📤 Outgoing (Sent)",
                "⏳ Pending Only"
        });
        filterCombo.setFont(UITheme.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(220, 34));
        filterCombo.addActionListener(e -> renderCards());

        filterRow.add(filterLbl);
        filterRow.add(filterCombo);

        mainContainer.add(filterRow);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Cards Container
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        mainContainer.add(cardsContainer);

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData(User currentUser) {
        this.currentUser = currentUser;
        if (currentUser == null) return;

        try {
            // Load all skills into cache
            for (Skill s : skillService.getAllSkills()) {
                skillCache.put(s.getSkillId(), s);
            }

            allRequests.clear();
            List<ExchangeRequest> incoming = exchangeService.getReceivedRequests(currentUser.getUserId());
            List<ExchangeRequest> outgoing = exchangeService.getSentRequests(currentUser.getUserId());
            allRequests.addAll(incoming);
            allRequests.addAll(outgoing);

            // Pre-cache users
            for (ExchangeRequest req : allRequests) {
                int otherId = req.getSenderId() == currentUser.getUserId() ? req.getReceiverId() : req.getSenderId();
                if (!userCache.containsKey(otherId)) {
                    try {
                        User u = userService.getUserById(otherId);
                        if (u != null) userCache.put(otherId, u);
                    } catch (Exception ignored) {}
                }
            }

            renderCards();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderCards() {
        cardsContainer.removeAll();
        if (currentUser == null) return;

        int filterIdx = filterCombo.getSelectedIndex();
        int count = 0;

        for (ExchangeRequest req : allRequests) {
            boolean isIncoming = req.getReceiverId() == currentUser.getUserId();
            String status = req.getStatus() != null ? req.getStatus().toUpperCase() : "PENDING";

            if (filterIdx == 1 && !isIncoming) continue;
            if (filterIdx == 2 && isIncoming) continue;
            if (filterIdx == 3 && !"PENDING".equalsIgnoreCase(status)) continue;

            count++;
            int otherId = isIncoming ? req.getSenderId() : req.getReceiverId();
            User otherUser = userCache.get(otherId);
            Skill offSkill = skillCache.get(req.getOfferedSkillId());
            Skill wantSkill = skillCache.get(req.getWantedSkillId());

            RequestCard card = new RequestCard(req, isIncoming, otherUser, offSkill, wantSkill, new RequestCard.RequestActionListener() {
                @Override
                public void onAccept(ExchangeRequest request) {
                    try {
                        Timestamp sessionTime = new Timestamp(System.currentTimeMillis() + 86400000L); // Tomorrow
                        String venue = "SkillBridge Peer Collaboration Hub";
                        exchangeService.acceptRequest(request.getRequestId(), sessionTime, venue);
                        JOptionPane.showMessageDialog(mainFrame, "Exchange accepted! Peer chat is now unlocked and session is placed on both calendars.", "Request Accepted", JOptionPane.INFORMATION_MESSAGE);
                        refreshData(currentUser);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                public void onDecline(ExchangeRequest request) {
                    try {
                        exchangeService.rejectRequest(request.getRequestId());
                        JOptionPane.showMessageDialog(mainFrame, "Request declined.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                        refreshData(currentUser);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                public void onCancel(ExchangeRequest request) {
                    try {
                        exchangeService.cancelRequest(request.getRequestId());
                        JOptionPane.showMessageDialog(mainFrame, "Request cancelled.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                        refreshData(currentUser);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            cardsContainer.add(card);
            cardsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        countLabel.setText("Showing " + count + " requests");

        if (count == 0) {
            JPanel empty = UITheme.createRoundedCard(12);
            empty.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
            JLabel emptyLbl = new JLabel("No requests found. Check Smart Matches to send your first exchange request!");
            emptyLbl.setFont(UITheme.FONT_BODY);
            emptyLbl.setForeground(UITheme.TEXT_MUTED);
            empty.add(emptyLbl);
            cardsContainer.add(empty);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
}
