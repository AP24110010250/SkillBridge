package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.GoalCard;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.gui.components.StatCard;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.model.Skill;
import com.skillbridge.model.User;
import com.skillbridge.service.GamificationService;
import com.skillbridge.service.SkillService;

/**
 * Learning Progress View: Track milestone progression, update completed sessions,
 * and view completion percentage.
 */
public class LearningProgressView extends JPanel {

    private final MainFrame mainFrame;
    private final GamificationService gamificationService;
    private final SkillService skillService;

    private StatCard totalGoalsCard;
    private StatCard activeGoalsCard;
    private StatCard completedGoalsCard;
    private JPanel goalsContainer;

    private final Map<Integer, String> skillNames = new HashMap<>();
    private User currentUser;

    public LearningProgressView(MainFrame mainFrame, GamificationService gamificationService, SkillService skillService) {
        this.mainFrame = mainFrame;
        this.gamificationService = gamificationService;
        this.skillService = skillService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header & Add Goal Action
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titleLbl = new JLabel("Learning Milestones & Goals");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JButton addGoalBtn = UITheme.createPrimaryButton("+ Set New Learning Goal");
        addGoalBtn.setFont(UITheme.FONT_SMALL_BOLD);
        addGoalBtn.addActionListener(e -> mainFrame.showCreateGoalDialog());

        header.add(titleLbl, BorderLayout.WEST);
        header.add(addGoalBtn, BorderLayout.EAST);

        mainContainer.add(header);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Metrics Row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        totalGoalsCard = new StatCard("Total Goals", "0", "🎯", UITheme.BRAND_PURPLE, "Defined targets");
        activeGoalsCard = new StatCard("In Progress", "0", "⏳", UITheme.ACCENT_AMBER, "Active learning");
        completedGoalsCard = new StatCard("Mastered", "0", "🏆", UITheme.ACCENT_EMERALD, "Completed milestones");

        statsRow.add(totalGoalsCard);
        statsRow.add(activeGoalsCard);
        statsRow.add(completedGoalsCard);

        mainContainer.add(statsRow);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Goal Cards Container
        JLabel listTitle = new JLabel("All Learning Goals");
        listTitle.setFont(UITheme.FONT_HEADER);
        listTitle.setForeground(UITheme.TEXT_MAIN);
        listTitle.setAlignmentX(LEFT_ALIGNMENT);

        mainContainer.add(listTitle);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setOpaque(false);

        mainContainer.add(goalsContainer);

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
            // Load skills cache
            skillNames.clear();
            for (Skill s : skillService.getAllSkills()) {
                skillNames.put(s.getSkillId(), s.getSkillName());
            }

            List<LearningGoal> goals = gamificationService.getUserGoals(currentUser.getUserId());
            int total = goals.size();
            long completed = goals.stream().filter(g -> "COMPLETED".equalsIgnoreCase(g.getStatus()) || g.getProgressPercentage() >= 100.0).count();
            long active = total - completed;

            totalGoalsCard.setValue(String.valueOf(total));
            activeGoalsCard.setValue(String.valueOf(active));
            completedGoalsCard.setValue(String.valueOf(completed));

            goalsContainer.removeAll();
            for (LearningGoal g : goals) {
                String sName = skillNames.get(g.getSkillId());
                GoalCard card = new GoalCard(g, sName, new GoalCard.GoalActionListener() {
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
                goalsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
            }

            if (goals.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(12);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
                JLabel emptyLbl = new JLabel("No learning goals configured. Click '+ Set New Learning Goal' to track your progress!");
                emptyLbl.setFont(UITheme.FONT_BODY);
                emptyLbl.setForeground(UITheme.TEXT_MUTED);
                empty.add(emptyLbl);
                goalsContainer.add(empty);
            }

            goalsContainer.revalidate();
            goalsContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
