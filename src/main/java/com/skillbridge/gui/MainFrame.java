package com.skillbridge.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import com.skillbridge.gui.views.AchievementsView;
import com.skillbridge.gui.views.AdminView;
import com.skillbridge.gui.views.DiscoverSkillsView;
import com.skillbridge.gui.views.ExchangeRequestsView;
import com.skillbridge.gui.views.LearningProgressView;
import com.skillbridge.gui.views.LiveSessionsView;
import com.skillbridge.gui.views.MessagesView;
import com.skillbridge.gui.views.MySkillsView;
import com.skillbridge.gui.views.OverviewView;
import com.skillbridge.gui.views.ProfileView;
import com.skillbridge.gui.views.SettingsView;
import com.skillbridge.gui.views.SmartMatchesView;
import com.skillbridge.matching.MatchResult;
import com.skillbridge.matching.MatchingEngine;
import com.skillbridge.model.ExchangeSession;
import com.skillbridge.model.Skill;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.model.User;
import com.skillbridge.service.AdminService;
import com.skillbridge.service.ExchangeService;
import com.skillbridge.service.GamificationService;
import com.skillbridge.service.SkillCategoryService;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserService;

/**
 * SkillBridge Main Application Frame
 * Designed as a premium Udemy-inspired desktop peer learning platform.
 */
public class MainFrame extends JFrame {

    private User currentUser;

    // Services
    private final UserService userService;
    private final SkillService skillService;
    private final SkillCategoryService categoryService;
    private final MatchingEngine matchingEngine;
    private final ExchangeService exchangeService;
    private final GamificationService gamificationService;
    private final AdminService adminService;

    // Shell Components
    private JPanel sidebarPanel;
    private JPanel headerPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JLabel currentScreenTitleLbl;
    private JLabel headerUserPill;
    private JPanel headerAvatar;
    private String currentScreenName = "OVERVIEW";

    // Navigation state
    private final Map<String, JButton> navButtonMap = new HashMap<>();

    // Views
    private OverviewView overviewView;
    private DiscoverSkillsView discoverView;
    private SmartMatchesView matchesView;
    private ExchangeRequestsView requestsView;
    private LiveSessionsView sessionsView;
    private MessagesView messagesView;
    private MySkillsView mySkillsView;
    private LearningProgressView progressView;
    private AchievementsView achievementsView;
    private ProfileView profileView;
    private SettingsView settingsView;
    private AdminView adminView;

    public MainFrame() {
        this.userService = new UserService();
        this.skillService = new SkillService();
        this.categoryService = new SkillCategoryService();
        this.matchingEngine = new MatchingEngine();
        this.exchangeService = new ExchangeService();
        this.gamificationService = new GamificationService();
        this.adminService = new AdminService();

        initFrame();

        // Load default student (Koushik Chava, ID: 1) for instant visual data
        try {
            this.currentUser = userService.getUserById(1);
        } catch (Exception e) {
            System.err.println("Default user load: " + e.getMessage());
        }

        buildApplicationShell();
        setVisible(true);
    }

    private void initFrame() {
        setTitle("SkillBridge — Peer-to-Peer Learning & Skill Exchange Platform");
        setSize(1220, 800);
        setMinimumSize(new Dimension(1080, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.APP_BG);
        setLayout(new BorderLayout());
    }

    private void buildApplicationShell() {
        getContentPane().removeAll();

        // 1. Left Sidebar
        buildSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Right Container (Header + Main Views)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(UITheme.APP_BG);

        buildHeader();
        rightContainer.add(headerPanel, BorderLayout.NORTH);

        // 3. Main Views Host (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.APP_BG);

        initViews();
        rightContainer.add(mainContentPanel, BorderLayout.CENTER);

        add(rightContainer, BorderLayout.CENTER);

        // Show default view
        navigateTo("OVERVIEW");
        revalidate();
        repaint();
    }

    // -------------------------------------------------------------
    // SIDEBAR NAVIGATION
    // -------------------------------------------------------------

    private void buildSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(UITheme.SIDEBAR_BG);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_LIGHT));

        // Brand Banner
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 18));
        brandRow.setOpaque(false);
        brandRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel logoPill = new JLabel("SB") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BRAND_PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoPill.setOpaque(false);
        logoPill.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoPill.setForeground(Color.WHITE);
        logoPill.setHorizontalAlignment(SwingConstants.CENTER);
        logoPill.setPreferredSize(new Dimension(36, 36));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel brandTitle = new JLabel("SkillBridge");
        brandTitle.setFont(UITheme.FONT_TITLE);
        brandTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel brandSub = new JLabel("Peer Learning Platform");
        brandSub.setFont(UITheme.FONT_SMALL);
        brandSub.setForeground(UITheme.TEXT_MUTED);

        titleBlock.add(brandTitle);
        titleBlock.add(brandSub);

        brandRow.add(logoPill);
        brandRow.add(titleBlock);
        sidebarPanel.add(brandRow);

        // Nav Roster
        navButtonMap.clear();

        addNavSectionHeader("DISCOVER & MATCH");
        addNavButton("Overview Dashboard", "OVERVIEW", "📊");
        addNavButton("Discover Skills", "DISCOVER", "🔍");
        addNavButton("Smart Matches", "MATCHES", "⚡");

        addNavSectionHeader("EXCHANGES & CHAT");
        addNavButton("Exchange Requests", "REQUESTS", "📥");
        addNavButton("Live Sessions", "SESSIONS", "🎓");
        addNavButton("Messages", "MESSAGES", "💬");

        addNavSectionHeader("LEARNING & GROWTH");
        addNavButton("My Skills Portfolio", "MY_SKILLS", "📚");
        addNavButton("Learning Goals", "PROGRESS", "🎯");
        addNavButton("Achievements & Points", "ACHIEVEMENTS", "🏅");
        addNavButton("My Profile", "PROFILE", "👤");

        if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            addNavSectionHeader("ADMINISTRATION");
            addNavButton("Admin Console", "ADMIN", "🛡️");
        }

        addNavSectionHeader("SYSTEM");
        addNavButton("Settings & Export", "SETTINGS", "⚙️");

        sidebarPanel.add(Box.createVerticalGlue());

        // Bottom User Quick Card
        JPanel bottomUserCard = UITheme.createRoundedCard(10);
        bottomUserCard.setLayout(new BorderLayout(10, 0));
        bottomUserCard.setMaximumSize(new Dimension(226, 60));
        bottomUserCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        bottomUserCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        String initials = getInitials(currentUser != null ? currentUser.getFullName() : "Learner");
        JPanel bAvatar = UITheme.createAvatarCircle(initials, 36, UITheme.BRAND_PURPLE_LIGHT, UITheme.BRAND_PURPLE);

        JPanel bMeta = new JPanel();
        bMeta.setLayout(new BoxLayout(bMeta, BoxLayout.Y_AXIS));
        bMeta.setOpaque(false);

        JLabel bName = new JLabel(currentUser != null ? currentUser.getFullName() : "Guest User");
        bName.setFont(UITheme.FONT_BODY_BOLD);
        bName.setForeground(UITheme.TEXT_MAIN);

        JLabel bRole = new JLabel(currentUser != null ? currentUser.getRole() + " • ⭐ " + String.format("%.0f%%", currentUser.getReliabilityScore()) : "");
        bRole.setFont(UITheme.FONT_SMALL);
        bRole.setForeground(UITheme.ACCENT_EMERALD);

        bMeta.add(bName);
        bMeta.add(Box.createRigidArea(new Dimension(0, 2)));
        bMeta.add(bRole);

        bottomUserCard.add(bAvatar, BorderLayout.WEST);
        bottomUserCard.add(bMeta, BorderLayout.CENTER);

        sidebarPanel.add(bottomUserCard);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton switchBtn = UITheme.createSecondaryButton("Switch User / Login");
        switchBtn.setFont(UITheme.FONT_SMALL_BOLD);
        switchBtn.setMaximumSize(new Dimension(226, 34));
        switchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchBtn.addActionListener(e -> showLoginDialog());

        sidebarPanel.add(switchBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 16)));
    }

    private void addNavSectionHeader(String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(UITheme.FONT_SMALL_BOLD);
        lbl.setForeground(UITheme.TEXT_FAINT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 0));
        sidebarPanel.add(lbl);
    }

    private void addNavButton(String title, String cardKey, String icon) {
        JButton btn = new JButton("  " + icon + "  " + title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isActive = cardKey.equals(currentScreenName);
                if (isActive) {
                    g2.setColor(UITheme.BRAND_PURPLE_LIGHT);
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
                    g2.setColor(UITheme.BRAND_PURPLE);
                    g2.fillRect(8, 6, 4, getHeight() - 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT_MAIN);
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(236, 36));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> navigateTo(cardKey));

        navButtonMap.put(cardKey, btn);
        sidebarPanel.add(btn);
    }

    // -------------------------------------------------------------
    // TOP APPLICATION HEADER
    // -------------------------------------------------------------

    private void buildHeader() {
        headerPanel = new JPanel(new BorderLayout(14, 0));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBackground(UITheme.HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(10, 26, 10, 26)
        ));

        // Left: Screen Title
        currentScreenTitleLbl = new JLabel("Overview Dashboard");
        currentScreenTitleLbl.setFont(UITheme.FONT_TITLE);
        currentScreenTitleLbl.setForeground(UITheme.TEXT_MAIN);

        headerPanel.add(currentScreenTitleLbl, BorderLayout.WEST);

        // Right Controls: Quick Search + Notifications + User Avatar
        JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightRow.setOpaque(false);

        JLabel notifBell = UITheme.createPillBadge("🔔 Active", UITheme.BRAND_PURPLE, UITheme.BRAND_PURPLE_LIGHT);
        notifBell.setFont(UITheme.FONT_SMALL_BOLD);

        headerUserPill = new JLabel(currentUser != null ? currentUser.getFullName() : "Learner");
        headerUserPill.setFont(UITheme.FONT_BODY_BOLD);
        headerUserPill.setForeground(UITheme.TEXT_MAIN);

        String initials = getInitials(currentUser != null ? currentUser.getFullName() : "SB");
        headerAvatar = UITheme.createAvatarCircle(initials, 32, UITheme.BRAND_PURPLE_LIGHT, UITheme.BRAND_PURPLE);

        JButton switchBtn = UITheme.createSecondaryButton("Switch User");
        switchBtn.setFont(UITheme.FONT_SMALL_BOLD);
        switchBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        switchBtn.addActionListener(e -> showLoginDialog());

        JButton regBtn = UITheme.createPrimaryButton("+ Sign Up");
        regBtn.setFont(UITheme.FONT_SMALL_BOLD);
        regBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        regBtn.addActionListener(e -> showRegisterDialog());

        rightRow.add(notifBell);
        rightRow.add(headerAvatar);
        rightRow.add(headerUserPill);
        rightRow.add(switchBtn);
        rightRow.add(regBtn);

        headerPanel.add(rightRow, BorderLayout.EAST);
    }

    // -------------------------------------------------------------
    // VIEWS INITIALIZATION
    // -------------------------------------------------------------

    private void initViews() {
        overviewView = new OverviewView(this, matchingEngine, exchangeService, gamificationService, userService, skillService);
        discoverView = new DiscoverSkillsView(this, skillService, categoryService);
        matchesView = new SmartMatchesView(this, matchingEngine);
        requestsView = new ExchangeRequestsView(this, exchangeService, userService, skillService);
        sessionsView = new LiveSessionsView(this, exchangeService, userService);
        messagesView = new MessagesView(this, exchangeService, userService);
        mySkillsView = new MySkillsView(this, skillService, categoryService);
        progressView = new LearningProgressView(this, gamificationService, skillService);
        achievementsView = new AchievementsView(this, gamificationService);
        profileView = new ProfileView(this, userService);
        settingsView = new SettingsView(this, skillService, gamificationService);
        adminView = new AdminView(this, adminService);

        mainContentPanel.add(overviewView, "OVERVIEW");
        mainContentPanel.add(discoverView, "DISCOVER");
        mainContentPanel.add(matchesView, "MATCHES");
        mainContentPanel.add(requestsView, "REQUESTS");
        mainContentPanel.add(sessionsView, "SESSIONS");
        mainContentPanel.add(messagesView, "MESSAGES");
        mainContentPanel.add(mySkillsView, "MY_SKILLS");
        mainContentPanel.add(progressView, "PROGRESS");
        mainContentPanel.add(achievementsView, "ACHIEVEMENTS");
        mainContentPanel.add(profileView, "PROFILE");
        mainContentPanel.add(settingsView, "SETTINGS");
        mainContentPanel.add(adminView, "ADMIN");
    }

    public void navigateTo(String cardKey) {
        this.currentScreenName = cardKey;
        cardLayout.show(mainContentPanel, cardKey);

        // Update Nav button styles
        for (Map.Entry<String, JButton> entry : navButtonMap.entrySet()) {
            boolean active = entry.getKey().equals(cardKey);
            entry.getValue().setForeground(active ? UITheme.BRAND_PURPLE : UITheme.TEXT_MAIN);
            entry.getValue().setFont(active ? UITheme.FONT_BODY_BOLD : UITheme.FONT_BODY);
            entry.getValue().repaint();
        }

        // Update Title & Refresh Target View
        switch (cardKey) {
            case "OVERVIEW":
                currentScreenTitleLbl.setText("Overview Dashboard");
                overviewView.refreshData(currentUser);
                break;
            case "DISCOVER":
                currentScreenTitleLbl.setText("Discover Skills & Topics");
                discoverView.refreshData(currentUser);
                break;
            case "MATCHES":
                currentScreenTitleLbl.setText("Smart Skill Matches");
                matchesView.refreshData(currentUser);
                break;
            case "REQUESTS":
                currentScreenTitleLbl.setText("Exchange Requests");
                requestsView.refreshData(currentUser);
                break;
            case "SESSIONS":
                currentScreenTitleLbl.setText("Live Learning Sessions");
                sessionsView.refreshData(currentUser);
                break;
            case "MESSAGES":
                currentScreenTitleLbl.setText("Peer Messaging");
                messagesView.refreshData(currentUser);
                break;
            case "MY_SKILLS":
                currentScreenTitleLbl.setText("My Skills Portfolio");
                mySkillsView.refreshData(currentUser);
                break;
            case "PROGRESS":
                currentScreenTitleLbl.setText("Learning Milestones & Goals");
                progressView.refreshData(currentUser);
                break;
            case "ACHIEVEMENTS":
                currentScreenTitleLbl.setText("Gamification & Achievements");
                achievementsView.refreshData(currentUser);
                break;
            case "PROFILE":
                currentScreenTitleLbl.setText("User Profile & Credentials");
                profileView.refreshData(currentUser);
                break;
            case "SETTINGS":
                currentScreenTitleLbl.setText("Settings & Data Management");
                settingsView.refreshData(currentUser);
                break;
            case "ADMIN":
                currentScreenTitleLbl.setText("Administration & Platform Governance");
                adminView.refreshData(currentUser);
                break;
        }

        sidebarPanel.repaint();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        buildApplicationShell();
    }

    // -------------------------------------------------------------
    // MODAL DIALOGS
    // -------------------------------------------------------------

    public void showLoginDialog() {
        JDialog dialog = new JDialog(this, "Switch User / Sign In", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JLabel titleLbl = new JLabel("Sign In to SkillBridge");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JTextField userField = new JTextField("koushik@skillbridge.edu");
        userField.setFont(UITheme.FONT_BODY);
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPasswordField passField = new JPasswordField("password123");
        passField.setFont(UITheme.FONT_BODY);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        form.add(titleLbl);
        form.add(new JLabel("Email Address (e.g. koushik@skillbridge.edu, rohan@skillbridge.edu):"));
        form.add(userField);
        form.add(passField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.addActionListener(e -> {
            try {
                String input = userField.getText().trim();
                String email = input.contains("@") ? input : input + "@skillbridge.edu";
                User u = userService.loginUser(email, new String(passField.getPassword()));
                setCurrentUser(u);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Signed in as " + u.getFullName() + " (" + u.getRole() + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Sign In Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(loginBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Create New Account", true);
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"STUDENT", "MENTOR"});
        JTextField bioField = new JTextField("Passionate peer learner");

        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Email Address:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        form.add(new JLabel("Role:"));
        form.add(roleCombo);
        form.add(new JLabel("Bio / Summary:"));
        form.add(bioField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = UITheme.createPrimaryButton("Create Account");
        submitBtn.addActionListener(e -> {
            try {
                String role = (String) roleCombo.getSelectedItem();
                User u = userService.registerUser(
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        new String(passField.getPassword()),
                        role,
                        bioField.getText().trim(),
                        "Flexible weekdays"
                );
                setCurrentUser(u);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Account created! Welcome to SkillBridge, " + u.getFullName(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(submitBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showCreateRequestDialog(MatchResult match) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Request Skill Exchange", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(5, 1, 8, 8));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        User partner = match.getPartner();
        JLabel headerLbl = new JLabel("<html><b>Send Exchange Proposal to:</b> " + partner.getFullName() + " (" + partner.getRole() + ")</html>");
        headerLbl.setFont(UITheme.FONT_HEADER);

        JComboBox<Skill> offCombo = new JComboBox<>();
        JComboBox<Skill> wantCombo = new JComboBox<>();

        try {
            List<Skill> all = skillService.getAllSkills();
            for (Skill s : all) {
                offCombo.addItem(s);
                wantCombo.addItem(s);
            }
        } catch (Exception ignored) {}

        JTextArea noteArea = new JTextArea("Hi " + partner.getFullName() + ", I noticed we have reciprocal skills. Let's schedule an exchange session!");
        noteArea.setFont(UITheme.FONT_BODY);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        form.add(headerLbl);
        form.add(new JLabel("Skill you will teach them (Offered):"));
        form.add(offCombo);
        form.add(new JLabel("Skill you want them to teach you (Wanted):"));
        form.add(wantCombo);

        JPanel centerPanel = new JPanel(new BorderLayout(8, 6));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 10, 24));
        centerPanel.add(form, BorderLayout.NORTH);
        centerPanel.add(new JLabel("Invitation Note:"), BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(noteArea), BorderLayout.SOUTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton sendBtn = UITheme.createPrimaryButton("Send Proposal");
        sendBtn.addActionListener(e -> {
            Skill off = (Skill) offCombo.getSelectedItem();
            Skill want = (Skill) wantCombo.getSelectedItem();
            if (off == null || want == null) return;
            try {
                exchangeService.createRequest(currentUser.getUserId(), partner.getUserId(), off.getSkillId(), want.getSkillId(), noteArea.getText().trim());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Exchange request sent to " + partner.getFullName() + "!", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
                navigateTo("REQUESTS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(sendBtn);

        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showAddOfferedSkillDialog(Skill preset) {
        if (currentUser == null) return;

        JDialog dialog = new JDialog(this, "Offer Skill to Teach", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JComboBox<Skill> skillCombo = new JComboBox<>();
        try {
            for (Skill s : skillService.getAllSkills()) skillCombo.addItem(s);
            if (preset != null) {
                for (int i = 0; i < skillCombo.getItemCount(); i++) {
                    if (skillCombo.getItemAt(i).getSkillId() == preset.getSkillId()) {
                        skillCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        JComboBox<String> profCombo = new JComboBox<>(new String[]{"INTERMEDIATE", "BEGINNER", "ADVANCED"});
        JTextField expField = new JTextField("2");

        form.add(new JLabel("Skill to Teach:"));
        form.add(skillCombo);
        form.add(new JLabel("Proficiency Level:"));
        form.add(profCombo);
        form.add(new JLabel("Years Experience:"));
        form.add(expField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton addBtn = UITheme.createPrimaryButton("Add to Portfolio");
        addBtn.addActionListener(e -> {
            Skill s = (Skill) skillCombo.getSelectedItem();
            if (s == null) return;
            try {
                int exp = Integer.parseInt(expField.getText().trim());
                skillService.addSkillOffered(currentUser.getUserId(), s.getSkillId(), (String) profCombo.getSelectedItem(), exp);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, s.getSkillName() + " added to your offered skills!", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateTo("MY_SKILLS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(addBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showAddWantedSkillDialog(Skill preset) {
        if (currentUser == null) return;

        JDialog dialog = new JDialog(this, "Add Skill to Learn", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JComboBox<Skill> skillCombo = new JComboBox<>();
        try {
            for (Skill s : skillService.getAllSkills()) skillCombo.addItem(s);
            if (preset != null) {
                for (int i = 0; i < skillCombo.getItemCount(); i++) {
                    if (skillCombo.getItemAt(i).getSkillId() == preset.getSkillId()) {
                        skillCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        JComboBox<String> prioCombo = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"});
        JComboBox<String> targetCombo = new JComboBox<>(new String[]{"Working Proficiency", "Mastery / Advanced", "Foundational / Basics"});

        form.add(new JLabel("Skill to Learn:"));
        form.add(skillCombo);
        form.add(new JLabel("Priority Level:"));
        form.add(prioCombo);
        form.add(new JLabel("Target Proficiency:"));
        form.add(targetCombo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton addBtn = UITheme.createPrimaryButton("Add to Wishlist");
        addBtn.addActionListener(e -> {
            Skill s = (Skill) skillCombo.getSelectedItem();
            if (s == null) return;
            try {
                skillService.addSkillWanted(currentUser.getUserId(), s.getSkillId(), (String) prioCombo.getSelectedItem(), (String) targetCombo.getSelectedItem());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, s.getSkillName() + " added to your wanted learning list!", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateTo("MY_SKILLS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(addBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showCreateGoalDialog() {
        if (currentUser == null) return;

        JDialog dialog = new JDialog(this, "Set New Learning Milestone", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JTextField titleField = new JTextField("Master React Hooks");
        JComboBox<Skill> skillCombo = new JComboBox<>();
        try {
            for (Skill s : skillService.getAllSkills()) skillCombo.addItem(s);
        } catch (Exception ignored) {}
        JTextField targetField = new JTextField("5");

        form.add(new JLabel("Milestone Title:"));
        form.add(titleField);
        form.add(new JLabel("Associated Skill:"));
        form.add(skillCombo);
        form.add(new JLabel("Target Sessions:"));
        form.add(targetField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton addBtn = UITheme.createPrimaryButton("Save Goal");
        addBtn.addActionListener(e -> {
            Skill s = (Skill) skillCombo.getSelectedItem();
            if (s == null) return;
            try {
                int target = Integer.parseInt(targetField.getText().trim());
                gamificationService.createLearningGoal(currentUser.getUserId(), s.getSkillId(), titleField.getText().trim(), target);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Learning goal created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateTo("PROGRESS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(addBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showReviewDialog(ExchangeSession session) {
        if (currentUser == null) return;

        JDialog dialog = new JDialog(this, "Leave Peer Review & Rating", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(3, 1, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JLabel hLbl = new JLabel("Rate Session #" + session.getSessionId());
        hLbl.setFont(UITheme.FONT_HEADER);

        JComboBox<String> ratingCombo = new JComboBox<>(new String[]{
                "⭐⭐⭐⭐⭐ (5 - Exceptional Mentor)",
                "⭐⭐⭐⭐ (4 - Very Helpful & Knowledgeable)",
                "⭐⭐⭐ (3 - Good Exchange)",
                "⭐⭐ (2 - Needs Improvement)",
                "⭐ (1 - Unsatisfactory)"
        });

        JTextArea commentArea = new JTextArea("Great session! Clear explanations and practical examples.");
        commentArea.setFont(UITheme.FONT_BODY);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        form.add(hLbl);
        form.add(ratingCombo);
        form.add(new JScrollPane(commentArea));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Review");
        submitBtn.addActionListener(e -> {
            int stars = 5 - ratingCombo.getSelectedIndex();
            // Default reviewee is other user in session
            int revieweeId = currentUser.getUserId() == 1 ? 2 : 1;
            try {
                gamificationService.submitReview(session.getSessionId(), currentUser.getUserId(), revieweeId, stars, commentArea.getText().trim());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Review submitted! 50 points awarded.", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateTo("SESSIONS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(submitBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showEditProfileDialog() {
        if (currentUser == null) return;

        JDialog dialog = new JDialog(this, "Edit Profile Information", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JTextField nameField = new JTextField(currentUser.getFullName());
        JTextField availField = new JTextField(currentUser.getAvailability() != null ? currentUser.getAvailability() : "");
        JTextArea bioArea = new JTextArea(currentUser.getBio() != null ? currentUser.getBio() : "");
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Availability:"));
        form.add(availField);
        form.add(new JLabel("Bio / About Me:"));
        form.add(new JScrollPane(bioArea));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Save Profile");
        saveBtn.addActionListener(e -> {
            try {
                String fullName = nameField.getText().trim();
                String avail = availField.getText().trim();
                String bio = bioArea.getText().trim();
                userService.updateProfile(currentUser.getUserId(), fullName, bio, avail);
                currentUser.setFullName(fullName);
                currentUser.setAvailability(avail);
                currentUser.setBio(bio);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigateTo("PROFILE");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "SB";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
