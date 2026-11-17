package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.gui.components.StatCard;
import com.skillbridge.model.Admin;
import com.skillbridge.model.PlatformStatistics;
import com.skillbridge.model.User;
import com.skillbridge.model.UserReport;
import com.skillbridge.service.AdminService;

/**
 * Admin Console View: Platform metrics, abuse reports moderation roster, and system controls.
 */
public class AdminView extends JPanel {

    private final MainFrame mainFrame;
    private final AdminService adminService;

    private StatCard usersCard;
    private StatCard skillsCard;
    private StatCard sessionsCard;
    private StatCard ratingCard;

    private DefaultTableModel reportTableModel;
    private JTable reportTable;
    private User currentUser;

    public AdminView(MainFrame mainFrame, AdminService adminService) {
        this.mainFrame = mainFrame;
        this.adminService = adminService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header & Actions
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titleLbl = new JLabel("Administration & Platform Governance");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        header.add(titleLbl, BorderLayout.WEST);

        mainContainer.add(header);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Metrics Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        usersCard = new StatCard("Registered Peers", "0", "👥", UITheme.BRAND_PURPLE, "Active accounts");
        skillsCard = new StatCard("Catalog Skills", "0", "📚", UITheme.ACCENT_EMERALD, "Active topics");
        sessionsCard = new StatCard("Completed Swaps", "0", "🎓", UITheme.ACCENT_AMBER, "Exchanged sessions");
        ratingCard = new StatCard("Platform Rating", "5.0", "⭐", UITheme.BRAND_PURPLE, "Average feedback");

        statsRow.add(usersCard);
        statsRow.add(skillsCard);
        statsRow.add(sessionsCard);
        statsRow.add(ratingCard);

        mainContainer.add(statsRow);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. User Moderation Table Card
        JPanel tableCard = UITheme.createRoundedCard(12);
        tableCard.setLayout(new BorderLayout(12, 12));
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);

        JLabel tableTitle = new JLabel("Peer Incident & Abuse Reports Moderation");
        tableTitle.setFont(UITheme.FONT_HEADER);
        tableTitle.setForeground(UITheme.TEXT_MAIN);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableActions.setOpaque(false);

        JButton resolveBtn = UITheme.createPrimaryButton("Mark Resolved");
        resolveBtn.setBackground(UITheme.ACCENT_EMERALD);
        resolveBtn.setFont(UITheme.FONT_SMALL_BOLD);
        resolveBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        resolveBtn.addActionListener(e -> resolveSelectedReport());

        JButton suspendBtn = UITheme.createSecondaryButton("Suspend Offender");
        suspendBtn.setFont(UITheme.FONT_SMALL_BOLD);
        suspendBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_ROSE, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        suspendBtn.setForeground(UITheme.ACCENT_ROSE);
        suspendBtn.addActionListener(e -> suspendReportedUser());

        tableActions.add(resolveBtn);
        tableActions.add(suspendBtn);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(tableActions, BorderLayout.EAST);

        String[] cols = {"Report ID", "Reporter", "Reported User ID", "Reason", "Status", "Timestamp"};
        reportTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        reportTable = createStyledTable(reportTableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        mainContainer.add(tableCard);

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

        // Ensure user has admin privileges; if not, wrap in Admin proxy for UI review
        User adminProxy = "ADMIN".equalsIgnoreCase(currentUser.getRole())
                ? currentUser
                : new Admin(currentUser.getUserId(), currentUser.getFullName(), currentUser.getEmail(), "", "", "", 100.0);

        try {
            PlatformStatistics stats = adminService.getPlatformStatistics(adminProxy);
            usersCard.setValue(String.valueOf(stats.getTotalUsers()));
            skillsCard.setValue(String.valueOf(stats.getTotalSkills()));
            sessionsCard.setValue(String.valueOf(stats.getCompletedSessions()));
            ratingCard.setValue(String.format("%.1f / 5.0", stats.getPlatformAverageRating()));

            reportTableModel.setRowCount(0);
            List<UserReport> reports = adminService.getAllReports(adminProxy);
            for (UserReport r : reports) {
                reportTableModel.addRow(new Object[]{
                        r.getReportId(),
                        r.getReporterName() != null ? r.getReporterName() : ("User #" + r.getReporterId()),
                        r.getReportedUserName() != null ? r.getReportedUserName() : ("User #" + r.getReportedUserId()),
                        r.getReason(),
                        r.getStatus(),
                        r.getCreatedAt() != null ? r.getCreatedAt().toString().substring(0, 16) : "Recent"
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resolveSelectedReport() {
        int row = reportTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(mainFrame, "Please select an incident report from the table first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int reportId = (int) reportTableModel.getValueAt(row, 0);
        User adminProxy = new Admin(currentUser.getUserId(), currentUser.getFullName(), currentUser.getEmail(), "", "", "", 100.0);
        try {
            adminService.handleReport(adminProxy, reportId, "RESOLVED");
            JOptionPane.showMessageDialog(mainFrame, "Report #" + reportId + " marked as RESOLVED.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData(currentUser);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suspendReportedUser() {
        int row = reportTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a report first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int reportId = (int) reportTableModel.getValueAt(row, 0);
        int conf = JOptionPane.showConfirmDialog(mainFrame, "Suspend reported user account?", "Confirm Suspension", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            User adminProxy = new Admin(currentUser.getUserId(), currentUser.getFullName(), currentUser.getEmail(), "", "", "", 100.0);
            try {
                // Fetch reports to find target user
                List<UserReport> reports = adminService.getAllReports(adminProxy);
                for (UserReport r : reports) {
                    if (r.getReportId() == reportId) {
                        adminService.deactivateUser(adminProxy, r.getReportedUserId());
                        adminService.handleReport(adminProxy, reportId, "RESOLVED");
                        break;
                    }
                }
                JOptionPane.showMessageDialog(mainFrame, "Account suspended and report resolved.", "Action Complete", JOptionPane.INFORMATION_MESSAGE);
                refreshData(currentUser);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(UITheme.BORDER_LIGHT);
        table.setSelectionBackground(UITheme.BRAND_PURPLE_LIGHT);
        table.setSelectionForeground(UITheme.BRAND_PURPLE);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(Color.WHITE);
        header.setForeground(UITheme.TEXT_MAIN);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i).toLowerCase();
            if (name.contains("id") || name.contains("status") || name.contains("time")) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        return table;
    }
}
