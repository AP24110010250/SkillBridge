package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.Skill;

/**
 * Modern Udemy-style Course/Skill Card with category pill, description, and quick actions.
 */
public class SkillCard extends JPanel {

    private final Skill skill;
    private boolean isHovered = false;

    public interface SkillActionListener {
        void onWantToLearn(Skill skill);
        void onOfferSkill(Skill skill);
    }

    public SkillCard(Skill skill, String categoryName, SkillActionListener listener) {
        this.skill = skill;

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setPreferredSize(new Dimension(280, 195));
        setMinimumSize(new Dimension(250, 195));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // 1. Top Bar: Category Pill & Peer Exchange Badge
        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setOpaque(false);

        JLabel catPill = UITheme.createPillBadge(
                categoryName != null ? categoryName : "General",
                UITheme.BRAND_PURPLE,
                UITheme.BRAND_PURPLE_LIGHT
        );

        JLabel levelBadge = new JLabel("Peer Exchange");
        levelBadge.setFont(UITheme.FONT_SMALL_BOLD);
        levelBadge.setForeground(UITheme.TEXT_MUTED);

        topBar.add(catPill, BorderLayout.WEST);
        topBar.add(levelBadge, BorderLayout.EAST);

        // 2. Middle Content: Skill Title & Description
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(skill.getSkillName());
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><body style='width: 220px; color: #6A6F73;'>" +
                (skill.getDescription() != null && !skill.getDescription().isEmpty()
                        ? skill.getDescription()
                        : "Peer-to-peer exchange topic for college learners.") +
                "</body></html>");
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        centerPanel.add(titleLbl);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        centerPanel.add(descLbl);

        // 3. Bottom Actions: "Learn" & "Offer"
        JPanel bottomBar = new JPanel(new GridLayout(1, 2, 8, 0));
        bottomBar.setOpaque(false);

        JButton learnBtn = UITheme.createPrimaryButton("Want to Learn");
        learnBtn.setFont(UITheme.FONT_SMALL_BOLD);
        learnBtn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        learnBtn.addActionListener(e -> {
            if (listener != null) listener.onWantToLearn(skill);
        });

        JButton offerBtn = UITheme.createSecondaryButton("Offer Skill");
        offerBtn.setFont(UITheme.FONT_SMALL_BOLD);
        offerBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        offerBtn.addActionListener(e -> {
            if (listener != null) listener.onOfferSkill(skill);
        });

        bottomBar.add(learnBtn);
        bottomBar.add(offerBtn);

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

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

    public Skill getSkill() {
        return skill;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHovered ? UITheme.CARD_HOVER : UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        g2.setColor(isHovered ? UITheme.BRAND_PURPLE_BORDER : UITheme.BORDER_LIGHT);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
