package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.MatchCard;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.matching.MatchResult;
import com.skillbridge.matching.MatchingEngine;
import com.skillbridge.model.User;

/**
 * Smart Matches View: Displays algorithmic peer matches using rich MatchCards, clearly distinguishing
 * reciprocal opportunities from one-way matches.
 */
public class SmartMatchesView extends JPanel {

    private final MainFrame mainFrame;
    private final MatchingEngine matchingEngine;

    private JComboBox<String> filterCombo;
    private JPanel cardsContainer;
    private JLabel countLabel;
    private List<MatchResult> currentMatches = new ArrayList<>();

    public SmartMatchesView(MainFrame mainFrame, MatchingEngine matchingEngine) {
        this.mainFrame = mainFrame;
        this.matchingEngine = matchingEngine;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header & Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel pageTitle = new JLabel("Smart Skill Matches");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_MAIN);

        countLabel = new JLabel("0 compatibility matches found");
        countLabel.setFont(UITheme.FONT_BODY);
        countLabel.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(pageTitle, BorderLayout.WEST);
        titlePanel.add(countLabel, BorderLayout.EAST);

        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Info / Explanation Card
        JPanel infoCard = UITheme.createRoundedCard(10);
        infoCard.setLayout(new BorderLayout(10, 6));
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT_EMERALD),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel infoTitle = new JLabel("4-Factor Intelligent Matching Engine");
        infoTitle.setFont(UITheme.FONT_BODY_BOLD);
        infoTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel infoBody = new JLabel("Ranks candidates by Skill Overlap (50%), Reciprocal Mutual Need (25%), Schedule Overlap (15%), and Peer Trust Rating (10%).");
        infoBody.setFont(UITheme.FONT_SMALL);
        infoBody.setForeground(UITheme.TEXT_MUTED);

        infoCard.add(infoTitle, BorderLayout.NORTH);
        infoCard.add(infoBody, BorderLayout.CENTER);

        mainContainer.add(infoCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Filter Controls
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel filterLbl = new JLabel("Show:");
        filterLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterLbl.setForeground(UITheme.TEXT_MAIN);

        filterCombo = new JComboBox<>(new String[]{
                "All Matches (Ranked by Score)",
                "🔄 Reciprocal Exchanges Only (Mutual Need)",
                "⭐ High Compatibility (>= 75%)"
        });
        filterCombo.setFont(UITheme.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(320, 34));
        filterCombo.addActionListener(e -> renderCards());

        filterRow.add(filterLbl);
        filterRow.add(filterCombo);

        mainContainer.add(filterRow);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 4. Cards Container
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
        if (currentUser == null) return;
        try {
            currentMatches = matchingEngine.findMatchesForUser(currentUser.getUserId());
            renderCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderCards() {
        cardsContainer.removeAll();
        int filterIndex = filterCombo.getSelectedIndex();

        int displayedCount = 0;
        for (MatchResult match : currentMatches) {
            if (filterIndex == 1 && !match.isReciprocal()) {
                continue; // Reciprocal only
            }
            if (filterIndex == 2 && match.getOverallScore() < 75.0) {
                continue; // High compatibility only
            }

            displayedCount++;
            MatchCard card = new MatchCard(match, m -> mainFrame.showCreateRequestDialog(m));
            cardsContainer.add(card);
            cardsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        countLabel.setText("Showing " + displayedCount + " matching partners");

        if (displayedCount == 0) {
            JPanel empty = UITheme.createRoundedCard(12);
            empty.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
            JLabel emptyLbl = new JLabel("No matches found for this filter. Try adding more skills to your 'Offered' or 'Wanted' lists!");
            emptyLbl.setFont(UITheme.FONT_BODY);
            emptyLbl.setForeground(UITheme.TEXT_MUTED);
            empty.add(emptyLbl);
            cardsContainer.add(empty);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
}
