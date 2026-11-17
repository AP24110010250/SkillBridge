package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.skillbridge.gui.UITheme;

/**
 * First-Time User Experience Onboarding Banner
 * Explains the SkillBridge peer learning cycle in 4 clear, professional visual steps.
 */
public class OnboardingBanner extends JPanel {

    public interface OnboardingActionListener {
        void onExploreSkills();
        void onOfferSkill();
    }

    public OnboardingBanner(OnboardingActionListener listener) {
        setLayout(new BorderLayout(14, 12));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.BRAND_PURPLE),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                        BorderFactory.createEmptyBorder(16, 20, 16, 20)
                )
        ));

        // Top Row: Title + Quick Dismiss/Action
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);

        JLabel pill = UITheme.createPillBadge("HOW IT WORKS", UITheme.BRAND_PURPLE, UITheme.BRAND_PURPLE_LIGHT);
        JLabel titleLbl = new JLabel("Welcome to Peer-to-Peer Learning on SkillBridge");
        titleLbl.setFont(UITheme.FONT_CARD_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        titleBox.add(pill);
        titleBox.add(titleLbl);

        topRow.add(titleBox, BorderLayout.WEST);
        add(topRow, BorderLayout.NORTH);

        // Center: 4 Visual Steps Grid
        JPanel stepsGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        stepsGrid.setOpaque(false);

        stepsGrid.add(createStepCell("1", "🔍", "Discover Skills", "Browse catalog topics you want to learn."));
        stepsGrid.add(createStepCell("2", "📚", "Share Strengths", "Add skills you are ready to teach others."));
        stepsGrid.add(createStepCell("3", "⚡", "Smart Matching", "Find reciprocal mutual exchange partners."));
        stepsGrid.add(createStepCell("4", "🎓", "Live Sessions", "Complete sessions and earn reputation."));

        add(stepsGrid, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomRow.setOpaque(false);

        JButton exploreBtn = UITheme.createPrimaryButton("Explore Skills Catalog →");
        exploreBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        exploreBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        exploreBtn.addActionListener(e -> {
            if (listener != null) listener.onExploreSkills();
        });

        JButton offerBtn = UITheme.createSecondaryButton("+ Offer a Skill");
        offerBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        offerBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        offerBtn.addActionListener(e -> {
            if (listener != null) listener.onOfferSkill();
        });

        bottomRow.add(offerBtn);
        bottomRow.add(exploreBtn);

        add(bottomRow, BorderLayout.SOUTH);
    }

    private JPanel createStepCell(String num, String icon, String title, String desc) {
        JPanel cell = UITheme.createRoundedCard(8);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(UITheme.APP_BG);
        cell.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        header.setOpaque(false);

        JLabel numLbl = new JLabel(num, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BRAND_PURPLE_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        numLbl.setFont(UITheme.FONT_METADATA_BOLD);
        numLbl.setForeground(UITheme.BRAND_PURPLE);
        numLbl.setPreferredSize(new Dimension(20, 20));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 14));

        header.add(numLbl);
        header.add(iconLbl);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUPPORTING_BOLD);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel descLbl = new JLabel("<html><body style='color: #64748B;'>" + desc + "</body></html>");
        descLbl.setFont(UITheme.FONT_METADATA);

        cell.add(header);
        cell.add(Box.createRigidArea(new Dimension(0, 4)));
        cell.add(titleLbl);
        cell.add(Box.createRigidArea(new Dimension(0, 2)));
        cell.add(descLbl);

        return cell;
    }
}
