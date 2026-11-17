package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.AchievementBadge;

/**
 * Modern Achievement Badge Card showcasing unlocked accomplishments and locked goals.
 */
public class BadgeCard extends JPanel {

    private final AchievementBadge badge;
    private final boolean isUnlocked;

    public BadgeCard(AchievementBadge badge, boolean isUnlocked) {
        this.badge = badge;
        this.isUnlocked = isUnlocked;

        setLayout(new BorderLayout(12, 10));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setPreferredSize(new Dimension(240, 130));
        setMinimumSize(new Dimension(200, 130));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // 1. Icon Badge based on badge name
        String iconChar = getIconForBadge(badge.getBadgeName());
        JLabel iconLbl = new JLabel(iconChar) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isUnlocked ? UITheme.BRAND_PURPLE_LIGHT : new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLbl.setOpaque(false);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // 2. Info Stack
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLbl = new JLabel(badge.getBadgeName());
        nameLbl.setFont(UITheme.FONT_HEADER);
        nameLbl.setForeground(isUnlocked ? UITheme.TEXT_MAIN : UITheme.TEXT_MUTED);

        String desc = badge.getBadgeDescription() != null ? badge.getBadgeDescription() : "Peer achievement";
        JLabel descLbl = new JLabel("<html><body style='width: 150px; color: " +
                (isUnlocked ? "#6A6F73" : "#9CA3AF") + ";'>" + desc + "</body></html>");
        descLbl.setFont(UITheme.FONT_SMALL);

        JLabel statusLbl = new JLabel(isUnlocked ? "✓ Unlocked • " + badge.getRequiredPoints() + " pts" : "🔒 Needs " + badge.getRequiredPoints() + " pts");
        statusLbl.setFont(UITheme.FONT_SMALL_BOLD);
        statusLbl.setForeground(isUnlocked ? UITheme.ACCENT_EMERALD : UITheme.TEXT_FAINT);

        info.add(nameLbl);
        info.add(Box.createRigidArea(new Dimension(0, 3)));
        info.add(descLbl);
        info.add(Box.createRigidArea(new Dimension(0, 5)));
        info.add(statusLbl);

        add(iconLbl, BorderLayout.WEST);
        add(info, BorderLayout.CENTER);
    }

    private String getIconForBadge(String name) {
        if (name == null) return "🏅";
        String lower = name.toLowerCase();
        if (lower.contains("first") || lower.contains("starter")) return "🌱";
        if (lower.contains("master") || lower.contains("expert")) return "👑";
        if (lower.contains("reciproc")) return "🔄";
        if (lower.contains("review") || lower.contains("feedback")) return "⭐";
        if (lower.contains("mentor")) return "🎓";
        return "🏅";
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        g2.setColor(isUnlocked ? UITheme.BRAND_PURPLE_BORDER : UITheme.BORDER_LIGHT);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
