package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import com.skillbridge.gui.UITheme;

/**
 * Modern KPI Metric Card with Animated Number Counting, Status Pill, and Hover Micro-interaction.
 */
public class StatCard extends JPanel {

    private final JLabel valueLabel;
    private final JLabel subtitleLabel;
    private boolean isHovered = false;
    private Timer countTimer;

    public StatCard(String title, String initialValue, String icon, Color accentColor, String subtitle) {
        setLayout(new BorderLayout(10, 8));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setPreferredSize(new Dimension(160, 110));
        setMinimumSize(new Dimension(100, 100));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Top Row: Icon badge + Title
        JPanel topRow = new JPanel(new BorderLayout(8, 0));
        topRow.setOpaque(false);

        JLabel iconBadge = new JLabel(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconBadge.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BODY_BOLD);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        topRow.add(iconBadge, BorderLayout.WEST);
        topRow.add(titleLbl, BorderLayout.CENTER);

        // Center: Animated Large Value
        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UITheme.TEXT_MAIN);

        // Bottom Row: Subtitle / status pill
        subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(UITheme.FONT_SUPPORTING);
        subtitleLabel.setForeground(accentColor);

        add(topRow, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(subtitleLabel, BorderLayout.SOUTH);

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

    /**
     * Updates value with smooth counting animation if numeric.
     */
    public void setValue(String value) {
        if (value == null) return;
        String cleanStr = value.replace("%", "").replace("pts", "").trim();

        try {
            int targetVal = Integer.parseInt(cleanStr);
            animateNumber(targetVal, value.contains("%") ? "%" : (value.contains("pts") ? " pts" : ""));
        } catch (NumberFormatException e) {
            valueLabel.setText(value);
        }
    }

    private void animateNumber(int target, String suffix) {
        if (countTimer != null && countTimer.isRunning()) {
            countTimer.stop();
        }

        final int durationMs = 450;
        final int steps = 15;
        final int interval = durationMs / steps;
        final int increment = Math.max(1, target / steps);
        final int[] current = {0};

        countTimer = new Timer(interval, e -> {
            current[0] += increment;
            if (current[0] >= target) {
                current[0] = target;
                ((Timer) e.getSource()).stop();
            }
            valueLabel.setText(current[0] + suffix);
        });
        countTimer.start();
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
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
