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
import com.skillbridge.matching.MatchResult;
import com.skillbridge.model.User;

/**
 * Modern Partner Match Card with Reciprocity Ribbon, Compatibility Score Breakdown,
 * Skill Badges, and Exchange Action.
 */
public class MatchCard extends JPanel {

    private final MatchResult match;
    private boolean isHovered = false;

    public interface MatchActionListener {
        void onRequestExchange(MatchResult match);
    }

    public MatchCard(MatchResult match, MatchActionListener listener) {
        this.match = match;
        User partner = match.getPartner();
        boolean isReciprocal = match.isReciprocal();

        setLayout(new BorderLayout(14, 12));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // 1. Top Bar: Reciprocity Pill + Matching Score Breakdown Chips
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel matchTypeBadge;
        if (isReciprocal) {
            matchTypeBadge = UITheme.createPillBadge(
                    "🔄 Reciprocal Opportunity (Mutual Learning Exchange)",
                    UITheme.GREEN,
                    UITheme.GREEN_LIGHT
            );
        } else {
            matchTypeBadge = UITheme.createPillBadge(
                    "⚡ Direct Skill Match",
                    UITheme.BLUE,
                    UITheme.BLUE_LIGHT
            );
        }

        String breakdownStr = String.format("Skill: %.0f/50 • Reciprocity: %.0f/25 • Avail: %.0f/15 • Trust: %.0f/10",
                match.getSkillScore(), match.getReciprocityScore(), match.getAvailabilityScore(), match.getReliabilityScore());
        JLabel breakdownLbl = new JLabel(breakdownStr);
        breakdownLbl.setFont(UITheme.FONT_METADATA);
        breakdownLbl.setForeground(UITheme.TEXT_MUTED);

        topBar.add(matchTypeBadge, BorderLayout.WEST);
        topBar.add(breakdownLbl, BorderLayout.EAST);

        // 2. Center Content: Partner Identity & Skills Mapping
        JPanel contentRow = new JPanel(new BorderLayout(18, 0));
        contentRow.setOpaque(false);

        // 2a. Left Avatar & Partner Meta
        JPanel leftMeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftMeta.setOpaque(false);

        String partnerName = partner != null ? partner.getFullName() : "Learner";
        String initials = getInitials(partnerName);
        Color avatarBg = isReciprocal ? UITheme.GREEN_LIGHT : UITheme.BRAND_PURPLE_LIGHT;
        Color avatarFg = isReciprocal ? UITheme.GREEN : UITheme.BRAND_PURPLE;
        JPanel avatar = UITheme.createAvatarCircle(initials, 44, avatarBg, avatarFg);

        JPanel nameAndRole = new JPanel();
        nameAndRole.setLayout(new BoxLayout(nameAndRole, BoxLayout.Y_AXIS));
        nameAndRole.setOpaque(false);

        JLabel nameLbl = new JLabel(partnerName);
        nameLbl.setFont(UITheme.FONT_CARD_HEADING);
        nameLbl.setForeground(UITheme.TEXT_MAIN);

        double relScore = partner != null ? partner.getReliabilityScore() : 100.0;
        String roleText = (partner != null ? partner.getRole() : "STUDENT") + " • ⭐ " + String.format("%.0f%% Reliability", relScore);
        JLabel roleLbl = new JLabel(roleText);
        roleLbl.setFont(UITheme.FONT_SUPPORTING);
        roleLbl.setForeground(UITheme.TEXT_MUTED);

        String avail = (partner != null && partner.getAvailability() != null && !partner.getAvailability().isEmpty())
                ? "🕒 " + partner.getAvailability()
                : "🕒 Flexible Availability";
        JLabel availLbl = new JLabel(avail);
        availLbl.setFont(UITheme.FONT_METADATA);
        availLbl.setForeground(UITheme.TEXT_FAINT);

        nameAndRole.add(nameLbl);
        nameAndRole.add(Box.createRigidArea(new Dimension(0, 2)));
        nameAndRole.add(roleLbl);
        nameAndRole.add(Box.createRigidArea(new Dimension(0, 2)));
        nameAndRole.add(availLbl);

        leftMeta.add(avatar);
        leftMeta.add(nameAndRole);

        // 2b. Middle: Skills Exchange Mapping
        JPanel skillsCol = new JPanel();
        skillsCol.setLayout(new BoxLayout(skillsCol, BoxLayout.Y_AXIS));
        skillsCol.setOpaque(false);

        String theyTeach = !match.getSkillsTheyTeachMe().isEmpty()
                ? String.join(", ", match.getSkillsTheyTeachMe())
                : "General mentoring";
        JLabel teachLbl = new JLabel("<html><b>They can teach you:</b> <font color='#6D28D9'>" + theyTeach + "</font></html>");
        teachLbl.setFont(UITheme.FONT_BODY);

        String youTeach = !match.getSkillsITeachThem().isEmpty()
                ? String.join(", ", match.getSkillsITeachThem())
                : (isReciprocal ? "Open to arrange" : "None needed (Direct Mentorship)");
        String youTeachHex = isReciprocal ? "#059669" : "#64748B";
        JLabel learnLbl = new JLabel("<html><b>You can teach them:</b> <font color='" + youTeachHex + "'>" + youTeach + "</font></html>");
        learnLbl.setFont(UITheme.FONT_BODY);

        skillsCol.add(teachLbl);
        skillsCol.add(Box.createRigidArea(new Dimension(0, 4)));
        skillsCol.add(learnLbl);

        contentRow.add(leftMeta, BorderLayout.WEST);
        contentRow.add(skillsCol, BorderLayout.CENTER);

        // 3. Right Action Box: Compatibility Pill & Request Button
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        actionBox.setOpaque(false);

        int scorePct = (int) Math.round(match.getOverallScore());
        JLabel scoreBadge = UITheme.createPillBadge(
                scorePct + "% Match",
                isReciprocal ? UITheme.GREEN : UITheme.BRAND_PURPLE,
                isReciprocal ? UITheme.GREEN_LIGHT : UITheme.BRAND_PURPLE_LIGHT
        );
        scoreBadge.setFont(UITheme.FONT_BODY_BOLD);
        scoreBadge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        JButton requestBtn = UITheme.createPrimaryButton("Request Exchange");
        requestBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        requestBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        requestBtn.addActionListener(e -> {
            if (listener != null) listener.onRequestExchange(match);
        });

        actionBox.add(scoreBadge);
        actionBox.add(requestBtn);

        add(topBar, BorderLayout.NORTH);
        add(contentRow, BorderLayout.CENTER);
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

    public MatchResult getMatch() {
        return match;
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

        if (match.isReciprocal()) {
            g2.setColor(isHovered ? UITheme.GREEN : UITheme.GREEN_BORDER);
        } else {
            g2.setColor(isHovered ? UITheme.BORDER_ACTIVE : UITheme.BORDER_LIGHT);
        }
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
