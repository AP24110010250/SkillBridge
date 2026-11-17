package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.skillbridge.gui.UITheme;

/**
 * Weekly/Category Skill Contribution Activity Visualization Chart.
 * Professional, clean dual-bar comparison between Teaching Knowledge and Learning Knowledge.
 */
public class ContributionChart extends JPanel {

    private final Map<String, int[]> contributionData = new LinkedHashMap<>(); // [teachingCount, learningCount]

    public ContributionChart() {
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

        JLabel titleLbl = new JLabel("Weekly Skill Exchange Balance");
        titleLbl.setFont(UITheme.FONT_CARD_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        legend.setOpaque(false);

        JLabel teachLegend = new JLabel("■ Teaching");
        teachLegend.setFont(UITheme.FONT_METADATA_BOLD);
        teachLegend.setForeground(UITheme.BRAND_PURPLE);

        JLabel learnLegend = new JLabel("■ Learning");
        learnLegend.setFont(UITheme.FONT_METADATA_BOLD);
        learnLegend.setForeground(UITheme.GREEN);

        legend.add(teachLegend);
        legend.add(learnLegend);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(legend, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Chart Surface
        JPanel chartSurface = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight() - 25; // Reserve for labels
                if (contributionData.isEmpty() || h <= 0) return;

                int maxVal = 5;
                for (int[] pair : contributionData.values()) {
                    maxVal = Math.max(maxVal, Math.max(pair[0], pair[1]));
                }

                // Grid guidelines
                g2.setColor(UITheme.DIVIDER);
                g2.drawLine(20, h, w - 20, h);
                g2.drawLine(20, h / 2, w - 20, h / 2);

                int groupWidth = (w - 40) / contributionData.size();
                int barWidth = Math.max(14, groupWidth / 4);
                int idx = 0;

                for (Map.Entry<String, int[]> entry : contributionData.entrySet()) {
                    int centerX = 20 + idx * groupWidth + groupWidth / 2;
                    int tVal = entry.getValue()[0];
                    int lVal = entry.getValue()[1];

                    int tBarH = (int) (((double) tVal / maxVal) * (h - 20));
                    int lBarH = (int) (((double) lVal / maxVal) * (h - 20));

                    // Teaching Bar (Purple)
                    int tX = centerX - barWidth - 3;
                    int tY = h - tBarH;
                    g2.setColor(UITheme.BRAND_PURPLE);
                    g2.fillRoundRect(tX, tY, barWidth, tBarH, 6, 6);

                    // Learning Bar (Green)
                    int lX = centerX + 3;
                    int lY = h - lBarH;
                    g2.setColor(UITheme.GREEN);
                    g2.fillRoundRect(lX, lY, barWidth, lBarH, 6, 6);

                    // Category / Day Label
                    g2.setFont(UITheme.FONT_SUPPORTING);
                    g2.setColor(UITheme.TEXT_MUTED);
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int strW = fm.stringWidth(entry.getKey());
                    g2.drawString(entry.getKey(), centerX - strW / 2, h + 18);

                    idx++;
                }

                g2.dispose();
            }
        };
        chartSurface.setOpaque(false);
        chartSurface.setPreferredSize(new Dimension(300, 150));
        add(chartSurface, BorderLayout.CENTER);

        // Default initial distribution
        loadDefaultData(2, 3);
    }

    public void updateData(int offeredCount, int wantedCount, int completedSessions) {
        contributionData.clear();
        contributionData.put("Mon", new int[]{Math.max(1, offeredCount / 2), Math.max(1, wantedCount / 3)});
        contributionData.put("Tue", new int[]{Math.max(0, offeredCount - 1), Math.max(1, completedSessions)});
        contributionData.put("Wed", new int[]{Math.max(1, offeredCount), Math.max(1, wantedCount / 2)});
        contributionData.put("Thu", new int[]{Math.max(1, completedSessions), Math.max(2, wantedCount)});
        contributionData.put("Fri", new int[]{Math.max(2, offeredCount), Math.max(1, completedSessions + 1)});
        repaint();
    }

    private void loadDefaultData(int t, int l) {
        contributionData.put("Mon", new int[]{1, 2});
        contributionData.put("Tue", new int[]{2, 1});
        contributionData.put("Wed", new int[]{3, 2});
        contributionData.put("Thu", new int[]{1, 3});
        contributionData.put("Fri", new int[]{2, 2});
        repaint();
    }
}
