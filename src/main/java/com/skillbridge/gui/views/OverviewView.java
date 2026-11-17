package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.ActivityCalendar;
import com.skillbridge.gui.components.CompanionIllustration;
import com.skillbridge.gui.components.ContributionChart;
import com.skillbridge.gui.components.GoalCard;
import com.skillbridge.gui.components.LeaderboardCard;
import com.skillbridge.gui.components.MatchCard;
import com.skillbridge.gui.components.OnboardingBanner;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.gui.components.StatCard;
import com.skillbridge.matching.MatchResult;
import com.skillbridge.matching.MatchingEngine;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.model.User;
import com.skillbridge.service.ExchangeService;
import com.skillbridge.service.GamificationService;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserService;

/**
 * Master Overview Dashboard View:
 * Strictly vertical-only layout with ScrollablePanel and NO horizontal scrollbars.
 * Features:
 * - Section A: Personalized Welcome with Archie Knowledge Companion
 * - Onboarding 4-Step Cycle
 * - Section B: 4 Animated KPI Stat Cards
 * - Section C: Main 2-Column Row 1 (Recommended Partner Matches & Active Goals)
 * - Section D: Main 2-Column Row 2 (Monthly Activity Calendar & Community Leaderboard + Balance Chart)
 */
public class OverviewView extends JPanel {

    private final MainFrame mainFrame;
    private final MatchingEngine matchingEngine;
    private final ExchangeService exchangeService;
    private final GamificationService gamificationService;
    private final UserService userService;
    private final SkillService skillService;

    // Section A: Welcome
    private JLabel welcomeTitle;
    private JLabel welcomeSubtitle;

    // Section B: Key Performance Cards
    private StatCard pointsCard;
    private StatCard sessionsCard;
    private StatCard reliabilityCard;
    private StatCard matchesCard;

    // Section C: Calendar & Chart
    private ActivityCalendar activityCalendar;
    private ContributionChart contributionChart;

    // Section D: Goals
    private JPanel goalsContainer;

    // Section E: Recommended Partners
    private JPanel recommendedContainer;

    // Section G: Leaderboard
    private LeaderboardCard leaderboardCard;

    public OverviewView(MainFrame mainFrame, MatchingEngine matchingEngine,
                        ExchangeService exchangeService, GamificationService gamificationService,
                        UserService userService, SkillService skillService) {
        this.mainFrame = mainFrame;
        this.matchingEngine = matchingEngine;
        this.exchangeService = exchangeService;
        this.gamificationService = gamificationService;
        this.userService = userService;
        this.skillService = skillService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        // ScrollablePanel forces getScrollableTracksViewportWidth() = true (NO HORIZONTAL SCROLL)
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 28, 24));

        // ---------------------------------------------------------
        // SECTION A: WELCOME BANNER WITH ARCHIE BRAND COMPANION
        // ---------------------------------------------------------
        JPanel heroBanner = UITheme.createRoundedCard(14);
        heroBanner.setLayout(new BorderLayout(16, 10));
        heroBanner.setBackground(Color.WHITE);
        heroBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        heroBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.BRAND_PURPLE),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        // Left: Text + Actions
        JPanel heroTextCol = new JPanel();
        heroTextCol.setLayout(new BoxLayout(heroTextCol, BoxLayout.Y_AXIS));
        heroTextCol.setOpaque(false);

        welcomeTitle = new JLabel("Welcome back!");
        welcomeTitle.setFont(UITheme.FONT_PAGE_TITLE);
        welcomeTitle.setForeground(UITheme.TEXT_MAIN);

        welcomeSubtitle = new JLabel("Connect with peer students and mentors to exchange practical skills, schedule live sessions, and track growth.");
        welcomeSubtitle.setFont(UITheme.FONT_BODY);
        welcomeSubtitle.setForeground(UITheme.TEXT_MUTED);

        JPanel heroActionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        heroActionRow.setOpaque(false);

        JButton exploreBtn = UITheme.createPrimaryButton("Explore Skills Catalog →");
        exploreBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        exploreBtn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        exploreBtn.addActionListener(e -> mainFrame.navigateTo("DISCOVER"));

        JButton goalsBtn = UITheme.createSecondaryButton("View Learning Goals");
        goalsBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        goalsBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        goalsBtn.addActionListener(e -> mainFrame.navigateTo("PROGRESS"));

        heroActionRow.add(exploreBtn);
        heroActionRow.add(goalsBtn);

        heroTextCol.add(welcomeTitle);
        heroTextCol.add(Box.createRigidArea(new Dimension(0, 3)));
        heroTextCol.add(welcomeSubtitle);
        heroTextCol.add(Box.createRigidArea(new Dimension(0, 6)));
        heroTextCol.add(heroActionRow);

        // Right: Brand Companion Archie (Knowledge Owl)
        CompanionIllustration archie = new CompanionIllustration(80);

        heroBanner.add(heroTextCol, BorderLayout.CENTER);
        heroBanner.add(archie, BorderLayout.EAST);

        content.add(heroBanner);
        content.add(Box.createRigidArea(new Dimension(0, 14)));

        // ---------------------------------------------------------
        // ONBOARDING 4-STEP CYCLE BANNER
        // ---------------------------------------------------------
        OnboardingBanner onboarding = new OnboardingBanner(new OnboardingBanner.OnboardingActionListener() {
            @Override
            public void onExploreSkills() {
                mainFrame.navigateTo("DISCOVER");
            }

            @Override
            public void onOfferSkill() {
                mainFrame.showAddOfferedSkillDialog(null);
            }
        });
        onboarding.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        content.add(onboarding);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        // ---------------------------------------------------------
        // SECTION B: KEY PERFORMANCE CARDS (WITH NUMBER COUNTING)
        // ---------------------------------------------------------
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        pointsCard = new StatCard("Skill Points", "0", "⭐", UITheme.BRAND_PURPLE, "Level 1 Peer");
        sessionsCard = new StatCard("Sessions Done", "0", "🎓", UITheme.GREEN, "0 mins exchanged");
        reliabilityCard = new StatCard("Trust Rating", "100%", "🛡️", UITheme.BLUE, "Verified Peer");
        matchesCard = new StatCard("Top Matches", "0", "⚡", UITheme.AMBER, "Mutual opportunities");

        statsRow.add(pointsCard);
        statsRow.add(sessionsCard);
        statsRow.add(reliabilityCard);
        statsRow.add(matchesCard);

        content.add(statsRow);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---------------------------------------------------------
        // ROW 1: RECOMMENDED PARTNERS & ACTIVE GOALS (2 COLUMNS)
        // ---------------------------------------------------------
        JPanel row1 = new JPanel(new GridLayout(1, 2, 18, 0));
        row1.setOpaque(false);

        // Left Col: Recommended Partners
        JPanel leftCol1 = new JPanel(new BorderLayout(0, 8));
        leftCol1.setOpaque(false);

        JPanel recHeader = new JPanel(new BorderLayout());
        recHeader.setOpaque(false);

        JLabel recTitle = new JLabel("Recommended Exchange Partners");
        recTitle.setFont(UITheme.FONT_CARD_HEADING);
        recTitle.setForeground(UITheme.TEXT_MAIN);

        JButton viewAllMatchesBtn = UITheme.createSecondaryButton("View All Matches →");
        viewAllMatchesBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        viewAllMatchesBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        viewAllMatchesBtn.addActionListener(e -> mainFrame.navigateTo("MATCHES"));

        recHeader.add(recTitle, BorderLayout.WEST);
        recHeader.add(viewAllMatchesBtn, BorderLayout.EAST);

        recommendedContainer = new JPanel();
        recommendedContainer.setLayout(new BoxLayout(recommendedContainer, BoxLayout.Y_AXIS));
        recommendedContainer.setOpaque(false);

        leftCol1.add(recHeader, BorderLayout.NORTH);
        leftCol1.add(recommendedContainer, BorderLayout.CENTER);

        // Right Col: Active Learning Goals
        JPanel rightCol1 = new JPanel(new BorderLayout(0, 8));
        rightCol1.setOpaque(false);

        JPanel goalsHead = new JPanel(new BorderLayout());
        goalsHead.setOpaque(false);

        JLabel goalsTitle = new JLabel("Current Learning Milestones");
        goalsTitle.setFont(UITheme.FONT_CARD_HEADING);
        goalsTitle.setForeground(UITheme.TEXT_MAIN);

        JButton viewAllGoalsBtn = UITheme.createSecondaryButton("View Goals →");
        viewAllGoalsBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        viewAllGoalsBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        viewAllGoalsBtn.addActionListener(e -> mainFrame.navigateTo("PROGRESS"));

        goalsHead.add(goalsTitle, BorderLayout.WEST);
        goalsHead.add(viewAllGoalsBtn, BorderLayout.EAST);

        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setOpaque(false);

        rightCol1.add(goalsHead, BorderLayout.NORTH);
        rightCol1.add(goalsContainer, BorderLayout.CENTER);

        row1.add(leftCol1);
        row1.add(rightCol1);

        content.add(row1);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---------------------------------------------------------
        // ROW 2: ACTIVITY CALENDAR & LEADERBOARD + CHART (2 COLUMNS)
        // ---------------------------------------------------------
        JPanel row2 = new JPanel(new GridLayout(1, 2, 18, 0));
        row2.setOpaque(false);

        // Left: Activity Calendar
        activityCalendar = new ActivityCalendar();
        row2.add(activityCalendar);

        // Right: Stack of Contribution Chart + Leaderboard
        JPanel rightCol2 = new JPanel();
        rightCol2.setLayout(new BoxLayout(rightCol2, BoxLayout.Y_AXIS));
        rightCol2.setOpaque(false);

        contributionChart = new ContributionChart();
        contributionChart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        leaderboardCard = new LeaderboardCard();
        leaderboardCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        rightCol2.add(contributionChart);
        rightCol2.add(Box.createRigidArea(new Dimension(0, 14)));
        rightCol2.add(leaderboardCard);

        row2.add(rightCol2);

        content.add(row2);

        // Strict Vertical-Only Scrolling Policy
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData(User currentUser) {
        if (currentUser == null) return;

        welcomeTitle.setText("Welcome back, " + currentUser.getFullName() + "!");

        try {
            // Stat 1: Skill Points
            int pts = gamificationService.getUserPoints(currentUser.getUserId());
            pointsCard.setValue(String.valueOf(pts));
            pointsCard.setSubtitle(pts >= 100 ? "Level 2 Apprentice" : "Level 1 Peer");

            // Stat 2: Sessions Done
            List<ExchangeSession> sessions = exchangeService.getUserSessions(currentUser.getUserId());
            long completed = sessions.stream().filter(s -> "COMPLETED".equalsIgnoreCase(s.getStatus())).count();
            sessionsCard.setValue(String.valueOf(completed));
            sessionsCard.setSubtitle((completed * 60) + " mins exchanged");

            // Stat 3: Reliability Trust Rating
            reliabilityCard.setValue(String.format("%.0f%%", currentUser.getReliabilityScore()));
            reliabilityCard.setSubtitle(currentUser.getReliabilityScore() >= 90 ? "Trusted Peer" : "Active Peer");

            // Stat 4: Top Matches
            List<MatchResult> matches = matchingEngine.findMatchesForUser(currentUser.getUserId());
            matchesCard.setValue(String.valueOf(matches.size()));
            matchesCard.setSubtitle(matches.stream().filter(MatchResult::isReciprocal).count() + " Reciprocal");

            // Update Calendar & Contribution Chart with live data
            activityCalendar.setSessions(sessions);
            int offeredCount = skillService.getUserOfferedSkills(currentUser.getUserId()).size();
            int wantedCount = skillService.getUserWantedSkills(currentUser.getUserId()).size();
            contributionChart.updateData(offeredCount, wantedCount, (int) completed);

            // Populate Recommended Partners (Top 2)
            recommendedContainer.removeAll();
            int count = 0;
            for (MatchResult m : matches) {
                if (count++ >= 2) break;
                MatchCard card = new MatchCard(m, match -> mainFrame.showCreateRequestDialog(match));
                recommendedContainer.add(card);
                recommendedContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            if (matches.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(10);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 16));
                empty.add(new JLabel("No match candidates found yet. Try adding more wanted or offered skills!"));
                recommendedContainer.add(empty);
            }

            // Populate Goals (Top 2)
            goalsContainer.removeAll();
            List<LearningGoal> goals = gamificationService.getUserGoals(currentUser.getUserId());
            int gCount = 0;
            for (LearningGoal g : goals) {
                if (gCount++ >= 2) break;
                GoalCard card = new GoalCard(g, null, new GoalCard.GoalActionListener() {
                    @Override
                    public void onIncrementProgress(LearningGoal goal) {
                        try {
                            gamificationService.updateGoalProgress(goal.getGoalId(), goal.getCompletedSessions() + 1, goal.getTargetSessions());
                            refreshData(currentUser);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onMarkCompleted(LearningGoal goal) {
                        try {
                            gamificationService.updateGoalProgress(goal.getGoalId(), goal.getTargetSessions(), goal.getTargetSessions());
                            refreshData(currentUser);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                goalsContainer.add(card);
                goalsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            if (goals.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(10);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 16));
                empty.add(new JLabel("No active learning milestones. Set a new learning goal to track your growth!"));
                goalsContainer.add(empty);
            }

            // Populate Leaderboard with real peer records
            List<User> allUsers = userService.getAllUsers();
            List<LeaderboardCard.LeaderboardEntry> leaderboard = new ArrayList<>();
            int rank = 1;
            for (User u : allUsers) {
                int userPts = gamificationService.getUserPoints(u.getUserId());
                leaderboard.add(new LeaderboardCard.LeaderboardEntry(rank++, u, userPts));
                if (rank > 4) break;
            }
            leaderboardCard.setEntries(leaderboard);

            recommendedContainer.revalidate();
            recommendedContainer.repaint();
            goalsContainer.revalidate();
            goalsContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
