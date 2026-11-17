package com.skillbridge.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.skillbridge.gui.UITheme;
import com.skillbridge.model.Message;

/**
 * WhatsApp/Slack style chat bubble with sent/received alignment, styling, and timestamp.
 */
public class ChatBubble extends JPanel {

    public ChatBubble(Message message, boolean isMe) {
        setLayout(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 12, 4));
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel bubble = new JPanel(new BorderLayout(8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isMe ? UITheme.BRAND_PURPLE : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                if (!isMe) {
                    g2.setColor(UITheme.BORDER_LIGHT);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));

        String textHtml = "<html><body style='max-width: 380px; font-family: Segoe UI; font-size: 11pt; color: " +
                (isMe ? "#FFFFFF" : "#1C1D1F") + ";'>" + escapeHtml(message.getMessageText()) + "</body></html>";
        JLabel contentLbl = new JLabel(textHtml);

        String timeStr = message.getSentAt() != null ? message.getSentAt().toString().substring(11, 16) : "Just now";
        JLabel timeLbl = new JLabel(timeStr);
        timeLbl.setFont(UITheme.FONT_SMALL);
        timeLbl.setForeground(isMe ? new Color(220, 210, 255) : UITheme.TEXT_MUTED);
        timeLbl.setHorizontalAlignment(JLabel.RIGHT);

        bubble.add(contentLbl, BorderLayout.CENTER);
        bubble.add(timeLbl, BorderLayout.SOUTH);

        add(bubble);
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }
}
