package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.User;

/**
 * Peer Contribution & Learning Leaderboard Card.
 * Displays academic ranks, peer avatars, trust ratings, and earned skill points with professional restraint.
 */
public class LeaderboardCard extends JPanel {

    private final JPanel rosterPanel;

    public static class LeaderboardEntry {
        public final int rank;
        public final User user;
        public final int points;

        public LeaderboardEntry(int rank, User user, int points) {
            this.rank = rank;
            this.user = user;
            this.points = points;
        }
    }

    public LeaderboardCard() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLbl = new JLabel("Peer Collaboration Leaderboard");
        titleLbl.setFont(UITheme.FONT_CARD_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel subLbl = new JLabel("Top Knowledge Contributors");
        subLbl.setFont(UITheme.FONT_SUPPORTING);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(subLbl, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Roster Stack
        rosterPanel = new JPanel();
        rosterPanel.setLayout(new BoxLayout(rosterPanel, BoxLayout.Y_AXIS));
        rosterPanel.setOpaque(false);

        add(rosterPanel, BorderLayout.CENTER);
    }

    public void setEntries(List<LeaderboardEntry> entries) {
        rosterPanel.removeAll();
        for (LeaderboardEntry e : entries) {
            rosterPanel.add(createRow(e));
            rosterPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        rosterPanel.revalidate();
        rosterPanel.repaint();
    }

    private JPanel createRow(LeaderboardEntry entry) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.DIVIDER),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Rank Badge
        JLabel rankLbl = new JLabel(String.valueOf(entry.rank), SwingConstants.CENTER);
        rankLbl.setFont(UITheme.FONT_BODY_BOLD);
        rankLbl.setPreferredSize(new Dimension(28, 28));

        Color rankBg;
        Color rankFg = Color.WHITE;
        if (entry.rank == 1) {
            rankBg = new Color(0xF5, 0x9E, 0x0B); // Gold Amber
        } else if (entry.rank == 2) {
            rankBg = new Color(0x64, 0x74, 0x8B); // Silver Slate
        } else if (entry.rank == 3) {
            rankBg = new Color(0xB4, 0x53, 0x09); // Bronze
        } else {
            rankBg = UITheme.SECONDARY_BG;
            rankFg = UITheme.TEXT_MUTED;
        }

        final Color bg = rankBg;
        final Color fg = rankFg;
        JPanel rankCircle = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        rankCircle.setOpaque(false);
        rankCircle.setPreferredSize(new Dimension(28, 28));
        rankLbl.setForeground(fg);
        rankCircle.add(rankLbl, BorderLayout.CENTER);

        // User Avatar & Name
        String name = entry.user != null ? entry.user.getFullName() : "Peer";
        String initials = getInitials(name);
        JPanel avatar = UITheme.createAvatarCircle(initials, 28, UITheme.BRAND_PURPLE_LIGHT, UITheme.BRAND_PURPLE);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(UITheme.FONT_BODY_BOLD);
        nameLbl.setForeground(UITheme.TEXT_MAIN);

        double rel = entry.user != null ? entry.user.getReliabilityScore() : 100.0;
        JLabel roleLbl = new JLabel((entry.user != null ? entry.user.getRole() : "STUDENT") + " • ⭐ " + String.format("%.0f%% Trust", rel));
        roleLbl.setFont(UITheme.FONT_METADATA);
        roleLbl.setForeground(UITheme.TEXT_MUTED);

        info.add(nameLbl);
        info.add(roleLbl);

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftGroup.setOpaque(false);
        leftGroup.add(rankCircle);
        leftGroup.add(avatar);
        leftGroup.add(info);

        // Points Pill
        JLabel ptsPill = UITheme.createPillBadge(entry.points + " pts", UITheme.BRAND_PURPLE, UITheme.BRAND_PURPLE_LIGHT);

        row.add(leftGroup, BorderLayout.WEST);
        row.add(ptsPill, BorderLayout.EAST);
        return row;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "SB";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
