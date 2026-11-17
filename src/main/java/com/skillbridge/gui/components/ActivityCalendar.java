package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.ExchangeSession;

/**
 * Monthly Learning Activity Calendar Component
 * Visualizes peer-to-peer teaching, learning sessions, and milestones on actual dates.
 */
public class ActivityCalendar extends JPanel {

    private final Calendar currentMonthCal;
    private int selectedDay = -1;
    private final JLabel monthTitleLbl;
    private final JPanel daysGrid;
    private final JLabel dateDetailsLbl;

    // Map of day of month -> list of activities on that day
    private final Map<Integer, List<CalendarActivity>> monthActivities = new HashMap<>();
    private List<ExchangeSession> rawSessions = new ArrayList<>();

    public static class CalendarActivity {
        public final String title;
        public final String type; // "COMPLETED", "SCHEDULED", "MILESTONE"
        public final Color color;

        public CalendarActivity(String title, String type, Color color) {
            this.title = title;
            this.type = type;
            this.color = color;
        }
    }

    public ActivityCalendar() {
        this.currentMonthCal = Calendar.getInstance();
        this.selectedDay = currentMonthCal.get(Calendar.DAY_OF_MONTH);

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBackground(UITheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(18, 20, 16, 20)
        ));

        // 1. Month Header Bar
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);

        JLabel iconLbl = new JLabel("📅");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        monthTitleLbl = new JLabel();
        monthTitleLbl.setFont(UITheme.FONT_CARD_HEADING);
        monthTitleLbl.setForeground(UITheme.TEXT_MAIN);

        titleBox.add(iconLbl);
        titleBox.add(monthTitleLbl);

        JPanel navBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        navBox.setOpaque(false);

        JButton prevBtn = createNavButton("◀");
        prevBtn.addActionListener(e -> {
            currentMonthCal.add(Calendar.MONTH, -1);
            refreshCalendar();
        });

        JButton nextBtn = createNavButton("▶");
        nextBtn.addActionListener(e -> {
            currentMonthCal.add(Calendar.MONTH, 1);
            refreshCalendar();
        });

        navBox.add(prevBtn);
        navBox.add(nextBtn);

        header.add(titleBox, BorderLayout.WEST);
        header.add(navBox, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. Weekdays Header & Day Grid
        JPanel gridContainer = new JPanel(new BorderLayout(0, 6));
        gridContainer.setOpaque(false);

        JPanel weekdaysPanel = new JPanel(new GridLayout(1, 7, 6, 0));
        weekdaysPanel.setOpaque(false);
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String d : days) {
            JLabel dLbl = new JLabel(d, SwingConstants.CENTER);
            dLbl.setFont(UITheme.FONT_SUPPORTING_BOLD);
            dLbl.setForeground(UITheme.TEXT_FAINT);
            weekdaysPanel.add(dLbl);
        }
        gridContainer.add(weekdaysPanel, BorderLayout.NORTH);

        daysGrid = new JPanel(new GridLayout(0, 7, 6, 6));
        daysGrid.setOpaque(false);
        gridContainer.add(daysGrid, BorderLayout.CENTER);

        add(gridContainer, BorderLayout.CENTER);

        // 3. Bottom Legend & Selected Day Activity Preview
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);

        // Legend Row
        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        legendRow.setOpaque(false);
        legendRow.add(createLegendChip("● Completed Exchange", UITheme.GREEN));
        legendRow.add(createLegendChip("● Scheduled Session", UITheme.BLUE));
        legendRow.add(createLegendChip("● Learning Milestone", UITheme.BRAND_PURPLE));

        // Details Row
        dateDetailsLbl = new JLabel("Click any date with activity dots to view session records.");
        dateDetailsLbl.setFont(UITheme.FONT_SUPPORTING);
        dateDetailsLbl.setForeground(UITheme.TEXT_MUTED);
        dateDetailsLbl.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));

        footer.add(legendRow);
        footer.add(dateDetailsLbl);
        add(footer, BorderLayout.SOUTH);

        refreshCalendar();
    }

    public void setSessions(List<ExchangeSession> sessions) {
        this.rawSessions = sessions != null ? sessions : new ArrayList<>();
        indexActivities();
        refreshCalendar();
    }

    private void indexActivities() {
        monthActivities.clear();
        int activeMonth = currentMonthCal.get(Calendar.MONTH);
        int activeYear = currentMonthCal.get(Calendar.YEAR);

        Calendar sessionCal = Calendar.getInstance();
        for (ExchangeSession s : rawSessions) {
            if (s.getScheduledTime() == null) continue;
            sessionCal.setTimeInMillis(s.getScheduledTime().getTime());
            if (sessionCal.get(Calendar.MONTH) == activeMonth && sessionCal.get(Calendar.YEAR) == activeYear) {
                int day = sessionCal.get(Calendar.DAY_OF_MONTH);
                monthActivities.computeIfAbsent(day, k -> new ArrayList<>());

                boolean isCompleted = "COMPLETED".equalsIgnoreCase(s.getStatus());
                Color c = isCompleted ? UITheme.GREEN : UITheme.BLUE;
                String type = isCompleted ? "COMPLETED" : "SCHEDULED";
                String title = (s.getNotes() != null && !s.getNotes().isEmpty() ? s.getNotes() : "Peer Exchange Session #" + s.getSessionId());
                monthActivities.get(day).add(new CalendarActivity(title, type, c));
            }
        }
    }

    private void refreshCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        monthTitleLbl.setText(sdf.format(currentMonthCal.getTime()));

        indexActivities();
        daysGrid.removeAll();

        Calendar temp = (Calendar) currentMonthCal.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        int startDayOfWeek = temp.get(Calendar.DAY_OF_WEEK); // Sunday is 1, Monday is 2
        // Shift to Monday-first (0 to 6)
        int shift = (startDayOfWeek + 5) % 7;

        for (int i = 0; i < shift; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setOpaque(false);
            daysGrid.add(emptyCell);
        }

        int maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();
        boolean isCurrentMonth = today.get(Calendar.MONTH) == currentMonthCal.get(Calendar.MONTH)
                && today.get(Calendar.YEAR) == currentMonthCal.get(Calendar.YEAR);
        int todayDay = isCurrentMonth ? today.get(Calendar.DAY_OF_MONTH) : -1;

        for (int day = 1; day <= maxDays; day++) {
            final int dayNum = day;
            List<CalendarActivity> activities = monthActivities.getOrDefault(dayNum, new ArrayList<>());
            boolean isToday = (dayNum == todayDay);
            boolean isSelected = (dayNum == selectedDay);

            JPanel cell = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (isSelected) {
                        g2.setColor(UITheme.BRAND_PURPLE_LIGHT);
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                        g2.setColor(UITheme.BRAND_PURPLE);
                        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                    } else if (isToday) {
                        g2.setColor(UITheme.SECONDARY_BG);
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                        g2.setColor(UITheme.BORDER_ACTIVE);
                        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                    }

                    // Draw Day Number
                    g2.setColor(isSelected ? UITheme.BRAND_PURPLE : (isToday ? UITheme.TEXT_MAIN : UITheme.TEXT_MAIN));
                    g2.setFont(isSelected || isToday ? UITheme.FONT_SUPPORTING_BOLD : UITheme.FONT_SUPPORTING);
                    String dayStr = String.valueOf(dayNum);
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(dayStr);
                    g2.drawString(dayStr, (getWidth() - tw) / 2, 16);

                    // Draw Activity Indicator Dots
                    if (!activities.isEmpty()) {
                        int dotSize = 5;
                        int totalW = activities.size() * (dotSize + 3) - 3;
                        int startX = (getWidth() - totalW) / 2;
                        for (int k = 0; k < Math.min(activities.size(), 3); k++) {
                            g2.setColor(activities.get(k).color);
                            g2.fillOval(startX + k * (dotSize + 3), getHeight() - 8, dotSize, dotSize);
                        }
                    }

                    g2.dispose();
                }
            };

            cell.setPreferredSize(new Dimension(36, 32));
            cell.setOpaque(false);
            cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (!activities.isEmpty()) {
                cell.setToolTipText(activities.size() + " exchange activity record(s) on " + sdf.format(currentMonthCal.getTime()) + " " + dayNum);
            }

            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedDay = dayNum;
                    updateDetails(dayNum, activities);
                    refreshCalendar();
                }
            });

            daysGrid.add(cell);
        }

        daysGrid.revalidate();
        daysGrid.repaint();

        // Update details for initially selected day
        updateDetails(selectedDay, monthActivities.getOrDefault(selectedDay, new ArrayList<>()));
    }

    private void updateDetails(int day, List<CalendarActivity> activities) {
        if (activities.isEmpty()) {
            dateDetailsLbl.setText("No skill sessions scheduled for " + day + " " +
                    new SimpleDateFormat("MMMM").format(currentMonthCal.getTime()) + ".");
            dateDetailsLbl.setForeground(UITheme.TEXT_MUTED);
        } else {
            StringBuilder sb = new StringBuilder("<html><b>" + day + " " +
                    new SimpleDateFormat("MMMM").format(currentMonthCal.getTime()) + ":</b> ");
            for (int i = 0; i < activities.size(); i++) {
                CalendarActivity a = activities.get(i);
                String hex = a.type.equals("COMPLETED") ? "#059669" : "#2563EB";
                sb.append("<font color='").append(hex).append("'>[").append(a.type).append("] ")
                        .append(a.title).append("</font>");
                if (i < activities.size() - 1) sb.append(" • ");
            }
            sb.append("</html>");
            dateDetailsLbl.setText(sb.toString());
            dateDetailsLbl.setForeground(UITheme.TEXT_MAIN);
        }
    }

    private JButton createNavButton(String arrow) {
        JButton btn = new JButton(arrow);
        btn.setFont(UITheme.FONT_METADATA_BOLD);
        btn.setForeground(UITheme.TEXT_MUTED);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createLegendChip(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_METADATA);
        lbl.setForeground(color);
        return lbl;
    }
}
