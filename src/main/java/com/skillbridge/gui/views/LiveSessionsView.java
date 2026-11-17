package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.gui.components.SessionCard;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.User;
import com.skillbridge.service.ExchangeService;
import com.skillbridge.service.UserService;

/**
 * Live Sessions View: Displays scheduled and completed learning sessions with rich SessionCards.
 */
public class LiveSessionsView extends JPanel {

    private final MainFrame mainFrame;
    private final ExchangeService exchangeService;
    private final UserService userService;

    private JComboBox<String> filterCombo;
    private JPanel cardsContainer;
    private JLabel countLabel;

    private List<ExchangeSession> allSessions = new ArrayList<>();
    private final Map<Integer, User> userCache = new HashMap<>();
    private User currentUser;

    public LiveSessionsView(MainFrame mainFrame, ExchangeService exchangeService, UserService userService) {
        this.mainFrame = mainFrame;
        this.exchangeService = exchangeService;
        this.userService = userService;

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

        JLabel pageTitle = new JLabel("Live Learning Sessions");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_MAIN);

        countLabel = new JLabel("0 sessions");
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

        JLabel filterLbl = new JLabel("Status:");
        filterLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterLbl.setForeground(UITheme.TEXT_MAIN);

        filterCombo = new JComboBox<>(new String[]{
                "All Sessions",
                "📅 Scheduled & In-Progress",
                "✓ Completed Sessions"
        });
        filterCombo.setFont(UITheme.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(240, 34));
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
            allSessions = exchangeService.getUserSessions(currentUser.getUserId());
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

        for (ExchangeSession session : allSessions) {
            String status = session.getStatus() != null ? session.getStatus().toUpperCase() : "SCHEDULED";

            if (filterIdx == 1 && !"SCHEDULED".equalsIgnoreCase(status)) continue;
            if (filterIdx == 2 && !"COMPLETED".equalsIgnoreCase(status)) continue;

            count++;
            // Note: In SkillBridge, a session is tied to a request or partner
            SessionCard card = new SessionCard(session, null, new SessionCard.SessionActionListener() {
                @Override
                public void onComplete(ExchangeSession s) {
                    try {
                        exchangeService.completeSession(s.getSessionId(), "Session completed successfully via SkillBridge");
                        JOptionPane.showMessageDialog(mainFrame, "Session marked complete! Points have been awarded.", "Session Complete", JOptionPane.INFORMATION_MESSAGE);
                        refreshData(currentUser);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                public void onReview(ExchangeSession s) {
                    mainFrame.showReviewDialog(s);
                }
            });

            cardsContainer.add(card);
            cardsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        countLabel.setText("Showing " + count + " sessions");

        if (count == 0) {
            JPanel empty = UITheme.createRoundedCard(12);
            empty.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
            JLabel emptyLbl = new JLabel("No sessions found. Once an exchange request is accepted, a session appears here!");
            emptyLbl.setFont(UITheme.FONT_BODY);
            emptyLbl.setForeground(UITheme.TEXT_MUTED);
            empty.add(emptyLbl);
            cardsContainer.add(empty);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
}
