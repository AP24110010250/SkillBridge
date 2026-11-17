package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.sql.Connection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.model.AchievementBadge;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;
import com.skillbridge.service.GamificationService;
import com.skillbridge.service.SkillService;
import com.skillbridge.util.DatabaseConnection;
import com.skillbridge.util.FileExportUtil;

/**
 * Settings & Export View: File export tools (TXT and Binary DAT serialization)
 * and system connectivity diagnostics.
 */
public class SettingsView extends JPanel {

    private final MainFrame mainFrame;
    private final SkillService skillService;
    private final GamificationService gamificationService;

    private JLabel statusLabel;
    private JLabel dbStatusLabel;
    private User currentUser;

    public SettingsView(MainFrame mainFrame, SkillService skillService, GamificationService gamificationService) {
        this.mainFrame = mainFrame;
        this.skillService = skillService;
        this.gamificationService = gamificationService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel titleLbl = new JLabel("Settings & Data Management");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        header.add(titleLbl, BorderLayout.WEST);
        mainContainer.add(header);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Data Export & File I/O Card
        JPanel exportCard = UITheme.createRoundedCard(12);
        exportCard.setLayout(new BoxLayout(exportCard, BoxLayout.Y_AXIS));
        exportCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        exportCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel expTitle = new JLabel("Data Export & Platform Backup (Java File I/O)");
        expTitle.setFont(UITheme.FONT_HEADER);
        expTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel expDesc = new JLabel("<html><body style='color: #6A6F73;'>Export complete learning portfolio, peer reviews, and skill rosters to text reports or serialize state to binary backups.</body></html>");
        expDesc.setFont(UITheme.FONT_BODY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        btnRow.setOpaque(false);

        JButton exportTxtBtn = UITheme.createPrimaryButton("Export Text Portfolio (.txt)");
        exportTxtBtn.setFont(UITheme.FONT_SMALL_BOLD);
        exportTxtBtn.addActionListener(e -> exportTextReport());

        JButton exportDatBtn = UITheme.createSecondaryButton("Serialize Binary Backup (.dat)");
        exportDatBtn.setFont(UITheme.FONT_SMALL_BOLD);
        exportDatBtn.addActionListener(e -> exportBinaryData());

        btnRow.add(exportTxtBtn);
        btnRow.add(exportDatBtn);

        statusLabel = new JLabel("Ready to export.");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);

        exportCard.add(expTitle);
        exportCard.add(Box.createRigidArea(new Dimension(0, 4)));
        exportCard.add(expDesc);
        exportCard.add(Box.createRigidArea(new Dimension(0, 8)));
        exportCard.add(btnRow);
        exportCard.add(Box.createRigidArea(new Dimension(0, 4)));
        exportCard.add(statusLabel);

        mainContainer.add(exportCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. Database Diagnostics Card
        JPanel dbCard = UITheme.createRoundedCard(12);
        dbCard.setLayout(new BoxLayout(dbCard, BoxLayout.Y_AXIS));
        dbCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        dbCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel dbTitle = new JLabel("Database Connectivity & Diagnostics");
        dbTitle.setFont(UITheme.FONT_HEADER);
        dbTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel dbUrlLbl = new JLabel("Database Endpoint: jdbc:mysql://localhost:3306/skillbridge_db");
        dbUrlLbl.setFont(UITheme.FONT_BODY);
        dbUrlLbl.setForeground(UITheme.TEXT_MUTED);

        dbStatusLabel = new JLabel("● Connection Status: Connected & Operational");
        dbStatusLabel.setFont(UITheme.FONT_BODY_BOLD);
        dbStatusLabel.setForeground(UITheme.ACCENT_EMERALD);

        JButton testDbBtn = UITheme.createSecondaryButton("Test Live Connection");
        testDbBtn.setFont(UITheme.FONT_SMALL_BOLD);
        testDbBtn.addActionListener(e -> testConnection());

        dbCard.add(dbTitle);
        dbCard.add(Box.createRigidArea(new Dimension(0, 6)));
        dbCard.add(dbUrlLbl);
        dbCard.add(Box.createRigidArea(new Dimension(0, 6)));
        dbCard.add(dbStatusLabel);
        dbCard.add(Box.createRigidArea(new Dimension(0, 8)));
        dbCard.add(testDbBtn);

        mainContainer.add(dbCard);

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData(User currentUser) {
        this.currentUser = currentUser;
    }

    private void exportTextReport() {
        if (currentUser == null) return;
        try {
            List<UserSkillOffered> offered = skillService.getUserOfferedSkills(currentUser.getUserId());
            List<UserSkillWanted> wanted = skillService.getUserWantedSkills(currentUser.getUserId());
            List<LearningGoal> goals = gamificationService.getUserGoals(currentUser.getUserId());
            List<AchievementBadge> badges = gamificationService.getUserBadges(currentUser.getUserId());
            int pts = gamificationService.getUserPoints(currentUser.getUserId());

            File out = FileExportUtil.exportLearningReport(currentUser, offered, wanted, goals, badges, pts, "exports");
            statusLabel.setText("✓ Report saved to " + out.getAbsolutePath());
            JOptionPane.showMessageDialog(mainFrame, "Report successfully written to:\n" + out.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            statusLabel.setText("Export error: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportBinaryData() {
        if (currentUser == null) return;
        try {
            int pts = gamificationService.getUserPoints(currentUser.getUserId());
            File exportDir = new File("exports");
            if (!exportDir.exists()) exportDir.mkdirs();
            File dest = new File(exportDir, "SkillBridge_User_" + currentUser.getUserId() + "_Backup.dat");
            FileExportUtil.SerializableProfileBackup backup = new FileExportUtil.SerializableProfileBackup(
                    currentUser.getUserId(),
                    currentUser.getFullName(),
                    currentUser.getEmail(),
                    pts
            );
            FileExportUtil.serializeProfile(backup, dest);
            statusLabel.setText("✓ Binary backup serialized to " + dest.getAbsolutePath());
            JOptionPane.showMessageDialog(mainFrame, "Data serialized to:\n" + dest.getAbsolutePath(), "Backup Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            statusLabel.setText("Serialization error: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Serialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                dbStatusLabel.setText("● Connection Status: Connected & Operational");
                dbStatusLabel.setForeground(UITheme.ACCENT_EMERALD);
                JOptionPane.showMessageDialog(mainFrame, "MySQL connection verified successfully!", "Diagnostics", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            dbStatusLabel.setText("● Connection Status: Error: " + ex.getMessage());
            dbStatusLabel.setForeground(UITheme.ACCENT_ROSE);
        }
    }
}
