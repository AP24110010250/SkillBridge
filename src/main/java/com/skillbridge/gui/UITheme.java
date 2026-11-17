package com.skillbridge.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * SkillBridge Master Visual Design System
 * Inspired by modern EdTech platforms (e.g. Coursera, Udemy) and premium SaaS dashboards.
 * Strict professional palette, clear typography scale, generous spacing, and subtle micro-interactions.
 */
public class UITheme {

    // -------------------------------------------------------------
    // 1. REFINED PROFESSIONAL COLOR PALETTE
    // -------------------------------------------------------------

    // Primary Brand
    public static final Color BRAND_PURPLE = new Color(0x6D, 0x28, 0xD9);       // #6D28D9 Primary Purple
    public static final Color BRAND_PURPLE_DEEP = new Color(0x4C, 0x1D, 0x95);  // #4C1D95 Deep Purple
    public static final Color BRAND_PURPLE_LIGHT = new Color(0xF3, 0xE8, 0xFF); // #F3E8FF Soft Lavender Tint
    public static final Color BRAND_PURPLE_HOVER = new Color(0x5B, 0x21, 0xB6); // Hover state

    // Backgrounds
    public static final Color APP_BG = new Color(0xF6, 0xF7, 0xFB);             // #F6F7FB Main Canvas Background
    public static final Color SECONDARY_BG = new Color(0xEE, 0xF1, 0xF6);       // #EEF1F6 Subtle Section Contrast
    public static final Color CARD_BG = new Color(0xFF, 0xFF, 0xFF);            // #FFFFFF Pure White Card
    public static final Color CARD_HOVER = new Color(0xFC, 0xFD, 0xFE);         // Hover tint
    public static final Color SIDEBAR_BG = new Color(0xFF, 0xFF, 0xFF);         // Sidebar Surface
    public static final Color HEADER_BG = new Color(0xFF, 0xFF, 0xFF);          // Top Bar Surface

    // Typography
    public static final Color TEXT_MAIN = new Color(0x17, 0x20, 0x33);          // #172033 High-Contrast Slate Charcoal
    public static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);         // #64748B Secondary Slate Text
    public static final Color TEXT_FAINT = new Color(0x94, 0xA3, 0xB8);         // #94A3B8 Faint / Metadata Text

    // Semantic Colors
    public static final Color GREEN = new Color(0x05, 0x96, 0x69);              // #059669 Success / Reciprocity Green
    public static final Color GREEN_LIGHT = new Color(0xEC, 0xFD, 0xF5);        // #ECFDF5 Light Green Tint
    public static final Color GREEN_BORDER = new Color(0xA7, 0xF3, 0xD0);

    public static final Color BLUE = new Color(0x25, 0x63, 0xEB);               // #2563EB Information Blue
    public static final Color BLUE_LIGHT = new Color(0xEF, 0xF6, 0xFF);         // #EFF6FF Light Blue Tint
    public static final Color BLUE_BORDER = new Color(0xBF, 0xDB, 0xFE);

    public static final Color AMBER = new Color(0xD9, 0x77, 0x06);              // #D97706 Warning / Pending Amber
    public static final Color AMBER_LIGHT = new Color(0xFF, 0xFB, 0xEB);        // #FFFBEB Light Amber Tint
    public static final Color AMBER_BORDER = new Color(0xFD, 0xE6, 0x8A);

    public static final Color RED = new Color(0xDC, 0x26, 0x26);                // #DC2626 Error / Destructive Red
    public static final Color RED_LIGHT = new Color(0xFE, 0xF2, 0xF2);          // #FEF2F2 Light Red Tint
    public static final Color RED_BORDER = new Color(0xFE, 0xCA, 0xCA);

    // Borders & Dividers
    public static final Color BORDER_LIGHT = new Color(0xE2, 0xE8, 0xF0);       // Hairline border
    public static final Color BORDER_ACTIVE = new Color(0xC4, 0xB5, 0xFD);      // Active purple border
    public static final Color DIVIDER = new Color(0xED, 0xF2, 0xF7);

    // Backward-Compatibility Aliases
    public static final Color ACCENT_EMERALD = GREEN;
    public static final Color ACCENT_EMERALD_LIGHT = GREEN_LIGHT;
    public static final Color ACCENT_EMERALD_BORDER = GREEN_BORDER;
    public static final Color ACCENT_AMBER = AMBER;
    public static final Color ACCENT_AMBER_LIGHT = AMBER_LIGHT;
    public static final Color ACCENT_AMBER_BORDER = AMBER_BORDER;
    public static final Color ACCENT_ROSE = RED;
    public static final Color ACCENT_ROSE_LIGHT = RED_LIGHT;
    public static final Color ACCENT_ROSE_BORDER = RED_BORDER;
    public static final Color ACCENT_BLUE = BLUE;
    public static final Color ACCENT_BLUE_LIGHT = BLUE_LIGHT;
    public static final Color ACCENT_BLUE_BORDER = BLUE_BORDER;
    public static final Color BRAND_PURPLE_BORDER = BORDER_ACTIVE;

    // -------------------------------------------------------------
    // 2. TYPOGRAPHY SCALE (Segoe UI / Inter Professional Hierarchy)
    // -------------------------------------------------------------

    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SECTION_HEADING = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_CARD_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SUPPORTING = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SUPPORTING_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_METADATA = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METADATA_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    // Backward-Compatibility Font Aliases
    public static final Font FONT_HERO = FONT_PAGE_TITLE;
    public static final Font FONT_TITLE = FONT_SECTION_HEADING;
    public static final Font FONT_HEADER = FONT_CARD_HEADING;
    public static final Font FONT_SUBHEADER = FONT_BODY_BOLD;
    public static final Font FONT_SMALL = FONT_SUPPORTING;
    public static final Font FONT_SMALL_BOLD = FONT_SUPPORTING_BOLD;

    // -------------------------------------------------------------
    // 3. REUSABLE CARD & CONTAINER FACTORIES
    // -------------------------------------------------------------

    /**
     * Creates a rounded card with anti-aliasing and hairline border.
     */
    public static JPanel createRoundedCard(int radius) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.setColor(BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBackground(CARD_BG);
        return panel;
    }

    /**
     * Creates an interactive hoverable card with subtle elevation and border shift.
     */
    public static JPanel createHoverCard(int radius) {
        final boolean[] isHovered = {false};
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered[0] ? CARD_HOVER : getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

                g2.setColor(isHovered[0] ? BORDER_ACTIVE : BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBackground(CARD_BG);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered[0] = true;
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered[0] = false;
                panel.repaint();
            }
        });
        return panel;
    }

    /**
     * Creates a pill badge label with soft background and rounded corners.
     */
    public static JLabel createPillBadge(String text, Color textColor, Color bgColor) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setOpaque(false);
        lbl.setFont(FONT_METADATA_BOLD);
        lbl.setForeground(textColor);
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    /**
     * Creates an avatar circle with initials.
     */
    public static JPanel createAvatarCircle(String initials, int size, Color bg, Color fg) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, size, size);
                g2.setColor(fg);
                g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.42)));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(initials);
                int textH = fm.getAscent();
                g2.drawString(initials, (size - textW) / 2, (size + textH) / 2 - 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(size, size));
        avatar.setMaximumSize(new Dimension(size, size));
        return avatar;
    }

    /**
     * Primary action button with smooth hover state.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(BRAND_PURPLE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BRAND_PURPLE_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BRAND_PURPLE);
            }
        });
        return btn;
    }

    /**
     * Secondary action button with hairline border.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    /**
     * Prominent outline button.
     */
    public static JButton createOutlineButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(accentColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Modern styled search input field with active border.
     */
    public static JTextField createSearchField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_MUTED);
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(TEXT_MAIN);
                }
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_PURPLE, 1, true),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_MUTED);
                }
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)
                ));
            }
        });
        return tf;
    }

    /**
     * Custom painted progress bar.
     */
    public static JProgressBar createProgressBar(int value, Color fillColor) {
        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(value);
        pb.setStringPainted(true);
        pb.setFont(FONT_METADATA_BOLD);
        pb.setForeground(fillColor);
        pb.setBackground(new Color(241, 245, 249));
        pb.setBorder(BorderFactory.createEmptyBorder());
        pb.setPreferredSize(new Dimension(140, 14));
        return pb;
    }

    /**
     * Toast notification utility: displays a subtle, non-blocking toast overlay on a frame.
     */
    public static void showToast(JFrame parent, String message, Color accentColor) {
        if (parent == null) return;
        JDialog toast = new JDialog(parent);
        toast.setUndecorated(true);
        toast.setAlwaysOnTop(true);
        toast.setFocusableWindowState(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_MAIN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accentColor);
                g2.fillRect(0, 0, 4, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel label = new JLabel(message);
        label.setFont(FONT_BODY_BOLD);
        label.setForeground(Color.WHITE);
        panel.add(label);

        toast.getContentPane().add(panel);
        toast.pack();

        int x = parent.getX() + (parent.getWidth() - toast.getWidth()) / 2;
        int y = parent.getY() + parent.getHeight() - toast.getHeight() - 50;
        toast.setLocation(x, y);
        toast.setVisible(true);

        Timer timer = new Timer(2400, e -> {
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
