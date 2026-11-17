package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import com.skillbridge.dao.GamificationDao;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.BadgeCard;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.gui.components.StatCard;
import com.skillbridge.model.AchievementBadge;
import com.skillbridge.model.SkillPoint;
import com.skillbridge.model.User;
import com.skillbridge.service.GamificationService;

/**
 * Achievements & Points View: Level progression banner, points metrics, unlocked badge grid,
 * and points transaction ledger.
 */
public class AchievementsView extends JPanel {

    private final MainFrame mainFrame;
    private final GamificationService gamificationService;
    private final GamificationDao gamificationDao;

    private JLabel levelNameLbl;
    private JLabel totalPtsLbl;
    private JProgressBar levelProgressBar;
    private JLabel nextLevelLbl;

    private JPanel badgesGrid;
    private JPanel historyContainer;

    public AchievementsView(MainFrame mainFrame, GamificationService gamificationService) {
        this.mainFrame = mainFrame;
        this.gamificationService = gamificationService;
        this.gamificationDao = new GamificationDao();

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel titleLbl = new JLabel("Gamification & Achievements");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        header.add(titleLbl, BorderLayout.WEST);
        mainContainer.add(header);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 2. Level Progression Hero Card
        JPanel levelCard = UITheme.createRoundedCard(12);
        levelCard.setLayout(new BorderLayout(16, 12));
        levelCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        levelCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.BRAND_PURPLE),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        levelNameLbl = new JLabel("Level 1: Peer Explorer");
        levelNameLbl.setFont(UITheme.FONT_HERO);
        levelNameLbl.setForeground(UITheme.TEXT_MAIN);

        totalPtsLbl = new JLabel("0 Total Skill Points Earned");
        totalPtsLbl.setFont(UITheme.FONT_BODY);
        totalPtsLbl.setForeground(UITheme.TEXT_MUTED);

        leftCol.add(levelNameLbl);
        leftCol.add(Box.createRigidArea(new Dimension(0, 4)));
        leftCol.add(totalPtsLbl);

        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);
        rightCol.setPreferredSize(new Dimension(280, 50));

        nextLevelLbl = new JLabel("Next Level: 100 Points");
        nextLevelLbl.setFont(UITheme.FONT_SMALL_BOLD);
        nextLevelLbl.setForeground(UITheme.BRAND_PURPLE);

        levelProgressBar = UITheme.createProgressBar(0, UITheme.BRAND_PURPLE);
        levelProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

        rightCol.add(nextLevelLbl);
        rightCol.add(Box.createRigidArea(new Dimension(0, 6)));
        rightCol.add(levelProgressBar);

        levelCard.add(leftCol, BorderLayout.CENTER);
        levelCard.add(rightCol, BorderLayout.EAST);

        mainContainer.add(levelCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Badges Grid
        JLabel badgesTitle = new JLabel("Earned Badges & Milestones");
        badgesTitle.setFont(UITheme.FONT_HEADER);
        badgesTitle.setForeground(UITheme.TEXT_MAIN);
        badgesTitle.setAlignmentX(LEFT_ALIGNMENT);

        mainContainer.add(badgesTitle);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        badgesGrid = new JPanel(new GridLayout(0, 3, 14, 14));
        badgesGrid.setOpaque(false);

        mainContainer.add(badgesGrid);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Points Transaction History
        JLabel historyTitle = new JLabel("Recent Points Ledger");
        historyTitle.setFont(UITheme.FONT_HEADER);
        historyTitle.setForeground(UITheme.TEXT_MAIN);
        historyTitle.setAlignmentX(LEFT_ALIGNMENT);

        mainContainer.add(historyTitle);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));
        historyContainer.setOpaque(false);

        mainContainer.add(historyContainer);

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
            int pts = gamificationService.getUserPoints(currentUser.getUserId());
            totalPtsLbl.setText(pts + " Total Skill Points Earned");

            String level;
            int nextPts;
            if (pts >= 300) {
                level = "Level 4: Master Mentor 👑";
                nextPts = 500;
            } else if (pts >= 150) {
                level = "Level 3: Expert Peer 🎓";
                nextPts = 300;
            } else if (pts >= 50) {
                level = "Level 2: Apprentice Scholar 🚀";
                nextPts = 150;
            } else {
                level = "Level 1: Peer Explorer 🌱";
                nextPts = 50;
            }

            levelNameLbl.setText(level);
            int pct = Math.min((int) ((double) pts / nextPts * 100), 100);
            levelProgressBar.setValue(pct);
            nextLevelLbl.setText("Progress: " + pts + " / " + nextPts + " pts (" + pct + "%)");

            // Populate Badges
            badgesGrid.removeAll();
            List<AchievementBadge> allBadges = gamificationDao.getAllBadges();
            List<AchievementBadge> userBadges = gamificationService.getUserBadges(currentUser.getUserId());
            Set<Integer> unlockedIds = new HashSet<>();
            for (AchievementBadge b : userBadges) unlockedIds.add(b.getBadgeId());

            for (AchievementBadge b : allBadges) {
                boolean unlocked = unlockedIds.contains(b.getBadgeId()) || pts >= b.getRequiredPoints();
                BadgeCard card = new BadgeCard(b, unlocked);
                badgesGrid.add(card);
            }

            // Populate Point History
            historyContainer.removeAll();
            List<SkillPoint> pointsHistory = gamificationService.getPointHistory(currentUser.getUserId());
            for (SkillPoint sp : pointsHistory) {
                JPanel card = UITheme.createRoundedCard(10);
                card.setLayout(new BorderLayout(12, 0));
                card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JLabel reasonLbl = new JLabel(sp.getReason());
                reasonLbl.setFont(UITheme.FONT_BODY);
                reasonLbl.setForeground(UITheme.TEXT_MAIN);

                JLabel ptsLbl = new JLabel("+" + sp.getPoints() + " pts");
                ptsLbl.setFont(UITheme.FONT_BODY_BOLD);
                ptsLbl.setForeground(UITheme.ACCENT_EMERALD);

                card.add(reasonLbl, BorderLayout.WEST);
                card.add(ptsLbl, BorderLayout.EAST);

                historyContainer.add(card);
                historyContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            }

            if (pointsHistory.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(10);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 14, 18));
                empty.add(new JLabel("No points recorded yet. Complete an exchange session to earn 50 points!"));
                historyContainer.add(empty);
            }

            badgesGrid.revalidate();
            badgesGrid.repaint();
            historyContainer.revalidate();
            historyContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
