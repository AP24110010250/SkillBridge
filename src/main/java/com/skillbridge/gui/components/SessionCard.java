package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.User;

/**
 * Modern Session Card with date pill, meeting link, partner meta, and Complete/Review actions.
 */
public class SessionCard extends JPanel {

    private final ExchangeSession session;
    private boolean isHovered = false;

    public interface SessionActionListener {
        void onComplete(ExchangeSession session);
        void onReview(ExchangeSession session);
    }

    public SessionCard(ExchangeSession session, User partner, SessionActionListener listener) {
        this.session = session;

        setLayout(new BorderLayout(14, 10));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // 1. Top Bar: Scheduled Date Badge & Duration
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        String dateStr = session.getScheduledTime() != null ? session.getScheduledTime().toString().substring(0, 16) : "Flexible Time";
        JLabel dateBadge = UITheme.createPillBadge("📅 " + dateStr, UITheme.BRAND_PURPLE, UITheme.BRAND_PURPLE_LIGHT);

        JLabel durLbl = new JLabel("⏱️ " + session.getDurationMinutes() + " Minutes • Session #" + session.getSessionId());
        durLbl.setFont(UITheme.FONT_SMALL);
        durLbl.setForeground(UITheme.TEXT_MUTED);

        topBar.add(dateBadge, BorderLayout.WEST);
        topBar.add(durLbl, BorderLayout.EAST);

        // 2. Center: Partner & Session Location
        JPanel center = new JPanel(new BorderLayout(14, 0));
        center.setOpaque(false);

        String partnerName = partner != null ? partner.getFullName() : "Learning Partner";
        String initials = getInitials(partnerName);
        JPanel avatar = UITheme.createAvatarCircle(initials, 40, UITheme.ACCENT_BLUE_LIGHT, UITheme.ACCENT_BLUE);

        JPanel infoCol = new JPanel();
        infoCol.setLayout(new BoxLayout(infoCol, BoxLayout.Y_AXIS));
        infoCol.setOpaque(false);

        JLabel nameLbl = new JLabel("Partner: " + partnerName);
        nameLbl.setFont(UITheme.FONT_HEADER);
        nameLbl.setForeground(UITheme.TEXT_MAIN);

        String link = session.getMeetingLink() != null && !session.getMeetingLink().isEmpty()
                ? session.getMeetingLink()
                : "Campus Library / Study Room";
        JLabel locLbl = new JLabel("<html><b>Venue / Link:</b> <font color='#2563EB'>" + link + "</font></html>");
        locLbl.setFont(UITheme.FONT_BODY);

        String notes = session.getNotes() != null && !session.getNotes().isEmpty() ? session.getNotes() : "Peer skill exchange session";
        JLabel notesLbl = new JLabel("Notes: " + notes);
        notesLbl.setFont(UITheme.FONT_SMALL);
        notesLbl.setForeground(UITheme.TEXT_MUTED);

        infoCol.add(nameLbl);
        infoCol.add(Box.createRigidArea(new Dimension(0, 2)));
        infoCol.add(locLbl);
        infoCol.add(Box.createRigidArea(new Dimension(0, 2)));
        infoCol.add(notesLbl);

        center.add(avatar, BorderLayout.WEST);
        center.add(infoCol, BorderLayout.CENTER);

        // 3. Right Status & Actions
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        String status = session.getStatus() != null ? session.getStatus().toUpperCase() : "SCHEDULED";
        JLabel statusPill;
        if ("COMPLETED".equalsIgnoreCase(status)) {
            statusPill = UITheme.createPillBadge("COMPLETED", UITheme.ACCENT_EMERALD, UITheme.ACCENT_EMERALD_LIGHT);
        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            statusPill = UITheme.createPillBadge("CANCELLED", UITheme.TEXT_MUTED, new Color(241, 245, 249));
        } else {
            statusPill = UITheme.createPillBadge("SCHEDULED", UITheme.ACCENT_BLUE, UITheme.ACCENT_BLUE_LIGHT);
        }

        actionBox.add(statusPill);

        if ("SCHEDULED".equalsIgnoreCase(status)) {
            JButton completeBtn = UITheme.createPrimaryButton("Mark Complete");
            completeBtn.setBackground(UITheme.ACCENT_EMERALD);
            completeBtn.setFont(UITheme.FONT_SMALL_BOLD);
            completeBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            completeBtn.addActionListener(e -> {
                if (listener != null) listener.onComplete(session);
            });
            actionBox.add(completeBtn);
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            JButton reviewBtn = UITheme.createOutlineButton("⭐ Leave Review", UITheme.BRAND_PURPLE);
            reviewBtn.setFont(UITheme.FONT_SMALL_BOLD);
            reviewBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BRAND_PURPLE, 1, true),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            reviewBtn.addActionListener(e -> {
                if (listener != null) listener.onReview(session);
            });
            actionBox.add(reviewBtn);
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

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "SB";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
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
