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
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.Skill;
import com.skillbridge.model.User;

/**
 * Modern Exchange Request Card with direction badge, skill exchange details, and Accept/Decline actions.
 */
public class RequestCard extends JPanel {

    private final ExchangeRequest request;
    private boolean isHovered = false;

    public interface RequestActionListener {
        void onAccept(ExchangeRequest request);
        void onDecline(ExchangeRequest request);
        void onCancel(ExchangeRequest request);
    }

    public RequestCard(ExchangeRequest request, boolean isIncoming, User otherUser,
                       Skill offeredSkill, Skill wantedSkill, RequestActionListener listener) {
        this.request = request;

        setLayout(new BorderLayout(14, 10));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // 1. Top Bar: Direction Pill + Request ID + Date
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel directionBadge = UITheme.createPillBadge(
                isIncoming ? "📥 Incoming Request" : "📤 Outgoing Request",
                isIncoming ? UITheme.BRAND_PURPLE : UITheme.TEXT_MUTED,
                isIncoming ? UITheme.BRAND_PURPLE_LIGHT : new Color(241, 245, 249)
        );

        String partnerName = otherUser != null ? otherUser.getFullName() : ("User #" + (isIncoming ? request.getSenderId() : request.getReceiverId()));
        JLabel idLbl = new JLabel("Request #" + request.getRequestId() + " • " + (request.getCreatedAt() != null ? request.getCreatedAt().toString().substring(0, 16) : "Recent"));
        idLbl.setFont(UITheme.FONT_SMALL);
        idLbl.setForeground(UITheme.TEXT_MUTED);

        topBar.add(directionBadge, BorderLayout.WEST);
        topBar.add(idLbl, BorderLayout.EAST);

        // 2. Center Content: Partner & Skills Exchanged
        JPanel center = new JPanel(new BorderLayout(14, 0));
        center.setOpaque(false);

        String initials = getInitials(partnerName);
        JPanel avatar = UITheme.createAvatarCircle(initials, 38, UITheme.BRAND_PURPLE_LIGHT, UITheme.BRAND_PURPLE);

        JPanel infoCol = new JPanel();
        infoCol.setLayout(new BoxLayout(infoCol, BoxLayout.Y_AXIS));
        infoCol.setOpaque(false);

        JLabel nameLbl = new JLabel((isIncoming ? "From: " : "To: ") + partnerName);
        nameLbl.setFont(UITheme.FONT_HEADER);
        nameLbl.setForeground(UITheme.TEXT_MAIN);

        String offName = offeredSkill != null ? offeredSkill.getSkillName() : "Skill #" + request.getOfferedSkillId();
        String wantName = wantedSkill != null ? wantedSkill.getSkillName() : "Skill #" + request.getWantedSkillId();
        JLabel skillsLbl = new JLabel("<html><b>Offered:</b> <font color='#5624D0'>" + offName +
                "</font> &nbsp;⇄&nbsp; <b>Wanted:</b> <font color='#109566'>" + wantName + "</font></html>");
        skillsLbl.setFont(UITheme.FONT_BODY);

        String msgText = request.getMessage() != null && !request.getMessage().isEmpty()
                ? "“" + request.getMessage() + "”"
                : "No message attached.";
        JLabel noteLbl = new JLabel(msgText);
        noteLbl.setFont(UITheme.FONT_SMALL);
        noteLbl.setForeground(UITheme.TEXT_MUTED);

        infoCol.add(nameLbl);
        infoCol.add(Box.createRigidArea(new Dimension(0, 2)));
        infoCol.add(skillsLbl);
        infoCol.add(Box.createRigidArea(new Dimension(0, 3)));
        infoCol.add(noteLbl);

        center.add(avatar, BorderLayout.WEST);
        center.add(infoCol, BorderLayout.CENTER);

        // 3. Right Status & Action Buttons
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        String status = request.getStatus() != null ? request.getStatus().toUpperCase() : "PENDING";
        JLabel statusPill;
        switch (status) {
            case "ACCEPTED":
                statusPill = UITheme.createPillBadge("ACCEPTED", UITheme.ACCENT_EMERALD, UITheme.ACCENT_EMERALD_LIGHT);
                break;
            case "REJECTED":
                statusPill = UITheme.createPillBadge("REJECTED", UITheme.ACCENT_ROSE, UITheme.ACCENT_ROSE_LIGHT);
                break;
            case "CANCELLED":
                statusPill = UITheme.createPillBadge("CANCELLED", UITheme.TEXT_MUTED, new Color(241, 245, 249));
                break;
            default:
                statusPill = UITheme.createPillBadge("PENDING", UITheme.ACCENT_AMBER, UITheme.ACCENT_AMBER_LIGHT);
                break;
        }

        actionBox.add(statusPill);

        if ("PENDING".equalsIgnoreCase(status)) {
            if (isIncoming) {
                JButton acceptBtn = UITheme.createPrimaryButton("Accept");
                acceptBtn.setBackground(UITheme.ACCENT_EMERALD);
                acceptBtn.setFont(UITheme.FONT_SMALL_BOLD);
                acceptBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                acceptBtn.addActionListener(e -> {
                    if (listener != null) listener.onAccept(request);
                });

                JButton declineBtn = UITheme.createSecondaryButton("Decline");
                declineBtn.setFont(UITheme.FONT_SMALL_BOLD);
                declineBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
                declineBtn.addActionListener(e -> {
                    if (listener != null) listener.onDecline(request);
                });

                actionBox.add(acceptBtn);
                actionBox.add(declineBtn);
            } else {
                JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
                cancelBtn.setFont(UITheme.FONT_SMALL_BOLD);
                cancelBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
                cancelBtn.addActionListener(e -> {
                    if (listener != null) listener.onCancel(request);
                });
                actionBox.add(cancelBtn);
            }
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
