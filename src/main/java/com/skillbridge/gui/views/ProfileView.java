package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.model.Mentor;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.service.UserService;

/**
 * Profile View: Displays learner/mentor identity, college credentials, trust rating,
 * and profile modification dialog.
 */
public class ProfileView extends JPanel {

    private final MainFrame mainFrame;
    private final UserService userService;

    private JLabel nameLbl;
    private JLabel usernameLbl;
    private JLabel rolePill;
    private JLabel deptYearLbl;
    private JLabel bioLbl;
    private JLabel emailLbl;
    private JLabel phoneLbl;
    private JLabel availLbl;
    private JLabel trustScoreLbl;
    private JPanel avatarContainer;

    private User currentUser;

    public ProfileView(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header with Edit Action
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titleLbl = new JLabel("User Profile & Credentials");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JButton editBtn = UITheme.createPrimaryButton("Edit Profile Information");
        editBtn.setFont(UITheme.FONT_SMALL_BOLD);
        editBtn.addActionListener(e -> mainFrame.showEditProfileDialog());

        header.add(titleLbl, BorderLayout.WEST);
        header.add(editBtn, BorderLayout.EAST);

        mainContainer.add(header);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Identity Hero Card
        JPanel heroCard = UITheme.createRoundedCard(12);
        heroCard.setLayout(new BorderLayout(20, 14));
        heroCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        heroCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        avatarContainer = new JPanel(new BorderLayout());
        avatarContainer.setOpaque(false);

        JPanel metaCol = new JPanel();
        metaCol.setLayout(new BoxLayout(metaCol, BoxLayout.Y_AXIS));
        metaCol.setOpaque(false);

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameRow.setOpaque(false);

        nameLbl = new JLabel("User Full Name");
        nameLbl.setFont(UITheme.FONT_HERO);
        nameLbl.setForeground(UITheme.TEXT_MAIN);

        rolePill = UITheme.createPillBadge("STUDENT", UITheme.BRAND_PURPLE, UITheme.BRAND_PURPLE_LIGHT);
        nameRow.add(nameLbl);
        nameRow.add(rolePill);

        usernameLbl = new JLabel("@username");
        usernameLbl.setFont(UITheme.FONT_BODY);
        usernameLbl.setForeground(UITheme.TEXT_MUTED);

        deptYearLbl = new JLabel("Computer Science & Engineering • Year 3");
        deptYearLbl.setFont(UITheme.FONT_BODY_BOLD);
        deptYearLbl.setForeground(UITheme.BRAND_PURPLE);

        bioLbl = new JLabel("<html><body style='width: 480px; color: #6A6F73;'>Enthusiastic peer learner and mentor passionate about algorithms, system design, and collaborative engineering.</body></html>");
        bioLbl.setFont(UITheme.FONT_BODY);

        metaCol.add(nameRow);
        metaCol.add(Box.createRigidArea(new Dimension(0, 2)));
        metaCol.add(usernameLbl);
        metaCol.add(Box.createRigidArea(new Dimension(0, 6)));
        metaCol.add(deptYearLbl);
        metaCol.add(Box.createRigidArea(new Dimension(0, 6)));
        metaCol.add(bioLbl);

        heroCard.add(avatarContainer, BorderLayout.WEST);
        heroCard.add(metaCol, BorderLayout.CENTER);

        mainContainer.add(heroCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. Two Column Details: Contact & Trust
        JPanel detailsRow = new JPanel(new GridLayout(1, 2, 16, 0));
        detailsRow.setOpaque(false);
        detailsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Details A: Contact & Schedule
        JPanel contactCard = UITheme.createRoundedCard(12);
        contactCard.setLayout(new BoxLayout(contactCard, BoxLayout.Y_AXIS));
        contactCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel contactTitle = new JLabel("Contact & Schedule");
        contactTitle.setFont(UITheme.FONT_HEADER);
        contactTitle.setForeground(UITheme.TEXT_MAIN);

        emailLbl = new JLabel("📧 Email: user@university.edu");
        emailLbl.setFont(UITheme.FONT_BODY);
        emailLbl.setForeground(UITheme.TEXT_MUTED);

        phoneLbl = new JLabel("📱 Phone: +91 98765 43210");
        phoneLbl.setFont(UITheme.FONT_BODY);
        phoneLbl.setForeground(UITheme.TEXT_MUTED);

        availLbl = new JLabel("🕒 Availability: Weekdays 4:00 PM - 7:00 PM");
        availLbl.setFont(UITheme.FONT_BODY);
        availLbl.setForeground(UITheme.TEXT_MUTED);

        contactCard.add(contactTitle);
        contactCard.add(Box.createRigidArea(new Dimension(0, 10)));
        contactCard.add(emailLbl);
        contactCard.add(Box.createRigidArea(new Dimension(0, 6)));
        contactCard.add(phoneLbl);
        contactCard.add(Box.createRigidArea(new Dimension(0, 6)));
        contactCard.add(availLbl);

        // Details B: Platform Trust & Reputation
        JPanel trustCard = UITheme.createRoundedCard(12);
        trustCard.setLayout(new BoxLayout(trustCard, BoxLayout.Y_AXIS));
        trustCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel trustTitle = new JLabel("Platform Reliability & Trust");
        trustTitle.setFont(UITheme.FONT_HEADER);
        trustTitle.setForeground(UITheme.TEXT_MAIN);

        trustScoreLbl = new JLabel("⭐ 100% Peer Reliability");
        trustScoreLbl.setFont(UITheme.FONT_HERO);
        trustScoreLbl.setForeground(UITheme.ACCENT_EMERALD);

        JLabel trustDesc = new JLabel("<html><body style='color: #6A6F73;'>Calculated dynamically based on completed peer exchanges, punctuality, and verified ratings from peer reviews.</body></html>");
        trustDesc.setFont(UITheme.FONT_SMALL);

        trustCard.add(trustTitle);
        trustCard.add(Box.createRigidArea(new Dimension(0, 8)));
        trustCard.add(trustScoreLbl);
        trustCard.add(Box.createRigidArea(new Dimension(0, 6)));
        trustCard.add(trustDesc);

        detailsRow.add(contactCard);
        detailsRow.add(trustCard);

        mainContainer.add(detailsRow);

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData(User currentUser) {
        this.currentUser = currentUser;
        if (currentUser == null) return;

        nameLbl.setText(currentUser.getFullName());
        usernameLbl.setText(currentUser.getEmail());
        rolePill.setText(currentUser.getRole().toUpperCase());

        String initials = getInitials(currentUser.getFullName());
        avatarContainer.removeAll();
        avatarContainer.add(UITheme.createAvatarCircle(initials, 64, UITheme.BRAND_PURPLE_LIGHT, UITheme.BRAND_PURPLE));

        deptYearLbl.setText(currentUser.getUserTypeDescription());

        bioLbl.setText("<html><body style='width: 480px; color: #6A6F73;'>" +
                (currentUser.getBio() != null && !currentUser.getBio().isEmpty() ? currentUser.getBio() : "No bio provided yet.") +
                "</body></html>");

        emailLbl.setText("📧 Email: " + currentUser.getEmail());
        phoneLbl.setText("🛡️ Account Type: " + currentUser.getRole());
        availLbl.setText("🕒 Availability: " + (currentUser.getAvailability() != null ? currentUser.getAvailability() : "Flexible"));

        trustScoreLbl.setText("⭐ " + String.format("%.0f%%", currentUser.getReliabilityScore()) + " Peer Reliability");

        revalidate();
        repaint();
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "SB";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
