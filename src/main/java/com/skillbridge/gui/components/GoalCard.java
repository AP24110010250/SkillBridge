package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.LearningGoal;

/**
 * Modern Learning Goal Card with Animated Progress Bar, Milestone Metrics, and Action Controls.
 */
public class GoalCard extends JPanel {

    private final LearningGoal goal;
    private boolean isHovered = false;
    private Timer progressTimer;

    public interface GoalActionListener {
        void onIncrementProgress(LearningGoal goal);
        void onMarkCompleted(LearningGoal goal);
    }

    public GoalCard(LearningGoal goal, String skillName, GoalActionListener listener) {
        this.goal = goal;

        setLayout(new BorderLayout(14, 10));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // 1. Top Bar: Goal Title & Status
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel titleLbl = new JLabel(goal.getTitle());
        titleLbl.setFont(UITheme.FONT_CARD_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        boolean isDone = "COMPLETED".equalsIgnoreCase(goal.getStatus()) || goal.getProgressPercentage() >= 100.0;
        JLabel statusPill = UITheme.createPillBadge(
                isDone ? "✓ MASTERED" : "IN PROGRESS",
                isDone ? UITheme.GREEN : UITheme.BRAND_PURPLE,
                isDone ? UITheme.GREEN_LIGHT : UITheme.BRAND_PURPLE_LIGHT
        );

        topBar.add(titleLbl, BorderLayout.WEST);
        topBar.add(statusPill, BorderLayout.EAST);

        // 2. Center Content: Topic Context & Animated Progress Bar
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        String topicStr = skillName != null ? "Topic: " + skillName : "Personal Learning Milestone";
        int targetSessions = Math.max(1, goal.getTargetSessions());
        int completedSessions = goal.getCompletedSessions();
        int finalPct = (int) Math.round(goal.getProgressPercentage());

        JLabel topicLbl = new JLabel(topicStr + " • " + completedSessions + " of " + targetSessions + " sessions completed (" + finalPct + "%)");
        topicLbl.setFont(UITheme.FONT_SUPPORTING);
        topicLbl.setForeground(UITheme.TEXT_MUTED);

        JProgressBar pb = UITheme.createProgressBar(0, isDone ? UITheme.GREEN : UITheme.BRAND_PURPLE);
        pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        // Smooth progress bar fill animation
        animateProgressBar(pb, finalPct);

        center.add(topicLbl);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(pb);

        // 3. Right Action Controls
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBox.setOpaque(false);

        if (!isDone) {
            JButton plusBtn = UITheme.createSecondaryButton("+1 Session");
            plusBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
            plusBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            plusBtn.addActionListener(e -> {
                if (listener != null) listener.onIncrementProgress(goal);
            });

            JButton doneBtn = UITheme.createPrimaryButton("Mark Done");
            doneBtn.setBackground(UITheme.GREEN);
            doneBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
            doneBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            doneBtn.addActionListener(e -> {
                if (listener != null) listener.onMarkCompleted(goal);
            });

            actionBox.add(plusBtn);
            actionBox.add(doneBtn);
        } else {
            JLabel trophy = new JLabel("🏆 Milestone Achieved!");
            trophy.setFont(UITheme.FONT_BODY_BOLD);
            trophy.setForeground(UITheme.GREEN);
            actionBox.add(trophy);
        }

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(actionBox, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    private void animateProgressBar(JProgressBar pb, int target) {
        if (target <= 0) {
            pb.setValue(0);
            return;
        }

        final int durationMs = 400;
        final int steps = 15;
        final int interval = durationMs / steps;
        final int stepIncrement = Math.max(1, target / steps);
        final int[] cur = {0};

        progressTimer = new Timer(interval, e -> {
            cur[0] += stepIncrement;
            if (cur[0] >= target) {
                cur[0] = target;
                ((Timer) e.getSource()).stop();
            }
            pb.setValue(cur[0]);
        });
        progressTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHovered ? UITheme.CARD_HOVER : UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        g2.setColor(isHovered ? UITheme.BORDER_ACTIVE : UITheme.BORDER_LIGHT);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
