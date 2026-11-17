package com.skillbridge.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.Timer;
import com.skillbridge.gui.UITheme;

/**
 * SkillBridge Brand Companion: "Archie" — A refined, geometric knowledge owl.
 * Rendered using precision Java2D vector paths with a subtle, gentle ambient idle animation.
 * Communicates wisdom, personal development, and academic peer collaboration without childish cartoonishness.
 */
public class CompanionIllustration extends JPanel {

    private final int size;
    private boolean isBlinking = false;
    private Timer idleTimer;

    public CompanionIllustration(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setOpaque(false);

        // Gentle, professional ambient blink every 4.5 seconds
        idleTimer = new Timer(4500, e -> {
            isBlinking = true;
            repaint();
            Timer blinkBack = new Timer(180, ev -> {
                isBlinking = false;
                repaint();
            });
            blinkBack.setRepeats(false);
            blinkBack.start();
        });
        idleTimer.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (idleTimer != null && idleTimer.isRunning()) {
            idleTimer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double scale = size / 100.0;
        g2.scale(scale, scale);

        // 1. Soft Circular Aura / Halo
        g2.setColor(UITheme.BRAND_PURPLE_LIGHT);
        g2.fillOval(8, 8, 84, 84);

        // 2. Refined Owl Body (Smooth Rounded Pear Shape)
        g2.setColor(UITheme.BRAND_PURPLE);
        g2.fillRoundRect(24, 28, 52, 58, 32, 32);

        // Inner Chest Plate (Lighter Contrast)
        g2.setColor(new Color(243, 232, 255));
        g2.fillRoundRect(34, 46, 32, 36, 20, 20);

        // Subtle Chest Feathers Pattern (Geometric Chevrons)
        g2.setColor(new Color(196, 181, 253));
        g2.drawArc(42, 52, 8, 6, 0, -180);
        g2.drawArc(50, 52, 8, 6, 0, -180);
        g2.drawArc(46, 60, 8, 6, 0, -180);

        // 3. Owl Horns / Tufts
        int[] xTuftL = {24, 34, 30};
        int[] yTuftL = {28, 28, 16};
        g2.setColor(UITheme.BRAND_PURPLE_DEEP);
        g2.fillPolygon(xTuftL, yTuftL, 3);

        int[] xTuftR = {66, 76, 70};
        int[] yTuftR = {28, 28, 16};
        g2.fillPolygon(xTuftR, yTuftR, 3);

        // 4. Eyes & Spectacles (Symbol of Knowledge & Curiosity)
        int eyeY = 36;
        if (isBlinking) {
            // Elegant closed eye slit
            g2.setColor(UITheme.TEXT_MAIN);
            g2.drawLine(35, eyeY + 6, 45, eyeY + 6);
            g2.drawLine(55, eyeY + 6, 65, eyeY + 6);
        } else {
            // Left Eye Eye-ring & Pupil
            g2.setColor(Color.WHITE);
            g2.fillOval(33, eyeY, 14, 14);
            g2.setColor(UITheme.TEXT_MAIN);
            g2.fillOval(38, eyeY + 3, 7, 7);
            // Catchlight reflection
            g2.setColor(Color.WHITE);
            g2.fillOval(39, eyeY + 4, 2, 2);

            // Right Eye Eye-ring & Pupil
            g2.setColor(Color.WHITE);
            g2.fillOval(53, eyeY, 14, 14);
            g2.setColor(UITheme.TEXT_MAIN);
            g2.fillOval(55, eyeY + 3, 7, 7);
            g2.setColor(Color.WHITE);
            g2.fillOval(56, eyeY + 4, 2, 2);

            // Minimalist Golden Scholar Spectacle Rim
            g2.setColor(UITheme.AMBER);
            g2.drawOval(32, eyeY - 1, 16, 16);
            g2.drawOval(52, eyeY - 1, 16, 16);
            g2.drawLine(48, eyeY + 7, 52, eyeY + 7);
        }

        // 5. Golden Amber Beak
        int[] xBeak = {46, 54, 50};
        int[] yBeak = {47, 47, 55};
        g2.setColor(UITheme.AMBER);
        g2.fillPolygon(xBeak, yBeak, 3);

        // 6. Wings (Folded Elegantly)
        g2.setColor(UITheme.BRAND_PURPLE_DEEP);
        g2.fillRoundRect(20, 42, 10, 36, 10, 10);
        g2.fillRoundRect(70, 42, 10, 36, 10, 10);

        // 7. Small Scholarly Mortarboard / Graduation Cap
        g2.setColor(UITheme.TEXT_MAIN);
        int[] xCap = {50, 72, 50, 28};
        int[] yCap = {12, 18, 24, 18};
        g2.fillPolygon(xCap, yCap, 4);

        g2.setColor(UITheme.AMBER);
        g2.fillOval(48, 17, 4, 4);
        g2.drawLine(50, 19, 66, 26); // Tassel

        g2.dispose();
    }
}
