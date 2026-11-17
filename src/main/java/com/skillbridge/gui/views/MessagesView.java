package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.ChatBubble;
import com.skillbridge.model.Message;
import com.skillbridge.model.User;
import com.skillbridge.service.ExchangeService;
import com.skillbridge.service.UserService;

/**
 * Messages View: WhatsApp/Slack-inspired peer messaging with contact roster on the left
 * and visual chat bubbles on the right.
 */
public class MessagesView extends JPanel {

    private final MainFrame mainFrame;
    private final ExchangeService exchangeService;
    private final UserService userService;

    private JPanel contactsListPanel;
    private JPanel messagesListPanel;
    private JScrollPane messagesScrollPane;
    private JTextField messageInput;
    private JLabel activeChatTitle;
    private JLabel activeChatSubtitle;

    private User currentUser;
    private User activePartner;
    private List<User> allContacts = new ArrayList<>();

    public MessagesView(MainFrame mainFrame, ExchangeService exchangeService, UserService userService) {
        this.mainFrame = mainFrame;
        this.exchangeService = exchangeService;
        this.userService = userService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        // 1. Left Contact Roster
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(280, getHeight()));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_LIGHT));

        JPanel rosterHeader = new JPanel(new BorderLayout());
        rosterHeader.setBackground(Color.WHITE);
        rosterHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel rosterTitle = new JLabel("Peer Conversations");
        rosterTitle.setFont(UITheme.FONT_HEADER);
        rosterTitle.setForeground(UITheme.TEXT_MAIN);
        rosterHeader.add(rosterTitle, BorderLayout.WEST);

        leftPanel.add(rosterHeader, BorderLayout.NORTH);

        contactsListPanel = new JPanel();
        contactsListPanel.setLayout(new BoxLayout(contactsListPanel, BoxLayout.Y_AXIS));
        contactsListPanel.setBackground(Color.WHITE);

        JScrollPane contactsScroll = new JScrollPane(contactsListPanel);
        contactsScroll.setBorder(BorderFactory.createEmptyBorder());
        contactsScroll.getVerticalScrollBar().setUnitIncrement(14);
        leftPanel.add(contactsScroll, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // 2. Right Conversation Pane
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(UITheme.APP_BG);

        // Header: Active Contact Info
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(Color.WHITE);
        chatHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JPanel headerInfo = new JPanel();
        headerInfo.setLayout(new BoxLayout(headerInfo, BoxLayout.Y_AXIS));
        headerInfo.setOpaque(false);

        activeChatTitle = new JLabel("Select a contact to begin messaging");
        activeChatTitle.setFont(UITheme.FONT_HEADER);
        activeChatTitle.setForeground(UITheme.TEXT_MAIN);

        activeChatSubtitle = new JLabel("Encrypted direct student-to-mentor messaging channel");
        activeChatSubtitle.setFont(UITheme.FONT_SMALL);
        activeChatSubtitle.setForeground(UITheme.TEXT_MUTED);

        headerInfo.add(activeChatTitle);
        headerInfo.add(Box.createRigidArea(new Dimension(0, 2)));
        headerInfo.add(activeChatSubtitle);

        chatHeader.add(headerInfo, BorderLayout.WEST);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Center: Messages Bubble Stream
        messagesListPanel = new JPanel();
        messagesListPanel.setLayout(new BoxLayout(messagesListPanel, BoxLayout.Y_AXIS));
        messagesListPanel.setBackground(UITheme.APP_BG);
        messagesListPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        messagesScrollPane = new JScrollPane(messagesListPanel);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(messagesScrollPane, BorderLayout.CENTER);

        // Bottom: Composer Bar
        JPanel composerBar = new JPanel(new BorderLayout(10, 0));
        composerBar.setBackground(Color.WHITE);
        composerBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        messageInput = new JTextField();
        messageInput.setFont(UITheme.FONT_BODY);
        messageInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        JButton sendBtn = UITheme.createPrimaryButton("Send  ➤");
        sendBtn.setFont(UITheme.FONT_BODY_BOLD);
        sendBtn.addActionListener(e -> sendMessage());

        composerBar.add(messageInput, BorderLayout.CENTER);
        composerBar.add(sendBtn, BorderLayout.EAST);
        rightPanel.add(composerBar, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }

    public void refreshData(User currentUser) {
        this.currentUser = currentUser;
        if (currentUser == null) return;

        try {
            allContacts = userService.getAllUsers();
            contactsListPanel.removeAll();

            User defaultPartner = null;
            for (User u : allContacts) {
                if (u.getUserId() == currentUser.getUserId()) continue;

                if (defaultPartner == null) {
                    defaultPartner = u;
                }

                JPanel item = createContactItem(u);
                contactsListPanel.add(item);
            }

            contactsListPanel.revalidate();
            contactsListPanel.repaint();

            if (activePartner == null && defaultPartner != null) {
                selectContact(defaultPartner);
            } else if (activePartner != null) {
                loadConversation();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createContactItem(User user) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(true);
        boolean isSelected = activePartner != null && activePartner.getUserId() == user.getUserId();
        item.setBackground(isSelected ? UITheme.BRAND_PURPLE_LIGHT : Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        item.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        String initials = getInitials(user.getFullName());
        JPanel avatar = UITheme.createAvatarCircle(initials, 34, UITheme.BRAND_PURPLE_LIGHT, UITheme.BRAND_PURPLE);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel(user.getFullName());
        name.setFont(UITheme.FONT_BODY_BOLD);
        name.setForeground(isSelected ? UITheme.BRAND_PURPLE : UITheme.TEXT_MAIN);

        JLabel role = new JLabel(user.getRole() + " • ⭐ " + String.format("%.0f%%", user.getReliabilityScore()));
        role.setFont(UITheme.FONT_SMALL);
        role.setForeground(UITheme.TEXT_MUTED);

        info.add(name);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(role);

        item.add(avatar, BorderLayout.WEST);
        item.add(info, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectContact(user);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (activePartner == null || activePartner.getUserId() != user.getUserId()) {
                    item.setBackground(new Color(248, 249, 250));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activePartner == null || activePartner.getUserId() != user.getUserId()) {
                    item.setBackground(Color.WHITE);
                }
            }
        });

        return item;
    }

    public void selectContact(User partner) {
        this.activePartner = partner;
        activeChatTitle.setText(partner.getFullName() + " (" + partner.getRole() + ")");
        activeChatSubtitle.setText("⭐ " + String.format("%.0f%%", partner.getReliabilityScore()) + " Reliability Score • " +
                (partner.getAvailability() != null ? partner.getAvailability() : "Online"));

        refreshData(currentUser);
        loadConversation();
    }

    private void loadConversation() {
        if (currentUser == null || activePartner == null) return;

        messagesListPanel.removeAll();
        try {
            List<Message> messages = exchangeService.getConversation(currentUser.getUserId(), activePartner.getUserId());
            for (Message m : messages) {
                boolean isMe = m.getSenderId() == currentUser.getUserId();
                ChatBubble bubble = new ChatBubble(m, isMe);
                messagesListPanel.add(bubble);
            }

            if (messages.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(12);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 24));
                JLabel emptyLbl = new JLabel("No messages yet with " + activePartner.getFullName() + ". Send a message below to coordinate your session!");
                emptyLbl.setFont(UITheme.FONT_BODY);
                emptyLbl.setForeground(UITheme.TEXT_MUTED);
                empty.add(emptyLbl);
                messagesListPanel.add(empty);
            }

            messagesListPanel.revalidate();
            messagesListPanel.repaint();

            // Scroll to bottom
            SwingUtilities.invokeLater(() -> {
                JScrollBar vBar = messagesScrollPane.getVerticalScrollBar();
                vBar.setValue(vBar.getMaximum());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || currentUser == null || activePartner == null) return;

        try {
            exchangeService.sendMessage(currentUser.getUserId(), activePartner.getUserId(), text);
            messageInput.setText("");
            loadConversation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "SB";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
