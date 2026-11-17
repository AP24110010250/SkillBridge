package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import com.skillbridge.gui.MainFrame;
import com.skillbridge.gui.UITheme;
import com.skillbridge.gui.components.ScrollablePanel;
import com.skillbridge.gui.components.SkillCard;
import com.skillbridge.model.Skill;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.model.User;
import com.skillbridge.service.SkillCategoryService;
import com.skillbridge.service.SkillService;

/**
 * Discover Skills Marketplace View:
 * Features prominent category tiles, search bar, active filter counts, and responsive SkillCards.
 */
public class DiscoverSkillsView extends JPanel {

    private final MainFrame mainFrame;
    private final SkillService skillService;
    private final SkillCategoryService categoryService;

    private JTextField searchField;
    private JPanel categoryTilesRow;
    private JPanel cardsGrid;
    private JLabel resultsCountLbl;

    private String activeCategoryFilter = "ALL";
    private List<Skill> allSkills = new ArrayList<>();
    private final Map<Integer, String> categoryNames = new HashMap<>();

    public DiscoverSkillsView(MainFrame mainFrame, SkillService skillService, SkillCategoryService categoryService) {
        this.mainFrame = mainFrame;
        this.skillService = skillService;
        this.categoryService = categoryService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // 1. Page Header & Count
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel pageTitle = new JLabel("Explore Skills Catalog");
        pageTitle.setFont(UITheme.FONT_PAGE_TITLE);
        pageTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel pageSubtitle = new JLabel("Browse campus skill topics, add desired skills to your wishlist, or offer your own strengths to peers.");
        pageSubtitle.setFont(UITheme.FONT_BODY);
        pageSubtitle.setForeground(UITheme.TEXT_MUTED);

        titleBlock.add(pageTitle);
        titleBlock.add(pageSubtitle);

        resultsCountLbl = new JLabel("Showing all available topics");
        resultsCountLbl.setFont(UITheme.FONT_SUPPORTING_BOLD);
        resultsCountLbl.setForeground(UITheme.BRAND_PURPLE);

        titlePanel.add(titleBlock, BorderLayout.WEST);
        titlePanel.add(resultsCountLbl, BorderLayout.EAST);

        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Visual Category Tiles Row
        categoryTilesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        categoryTilesRow.setOpaque(false);
        categoryTilesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        mainContainer.add(categoryTilesRow);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Search & Filter Bar Card
        JPanel filterCard = UITheme.createRoundedCard(10);
        filterCard.setLayout(new BorderLayout(14, 0));
        filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        filterCard.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        searchField = UITheme.createSearchField("Search skills by keyword, framework, or language (e.g. Java, Python, UI/UX)...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilter();
            }
        });

        JButton resetBtn = UITheme.createSecondaryButton("Reset Filter");
        resetBtn.setFont(UITheme.FONT_SUPPORTING_BOLD);
        resetBtn.addActionListener(e -> {
            searchField.setText("Search skills by keyword, framework, or language (e.g. Java, Python, UI/UX)...");
            searchField.setForeground(UITheme.TEXT_MUTED);
            activeCategoryFilter = "ALL";
            buildCategoryTiles();
            applyFilter();
        });

        filterCard.add(searchField, BorderLayout.CENTER);
        filterCard.add(resetBtn, BorderLayout.EAST);

        mainContainer.add(filterCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Grid of Skill Cards
        cardsGrid = new JPanel(new GridLayout(0, 3, 16, 16));
        cardsGrid.setOpaque(false);

        mainContainer.add(cardsGrid);

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData(User currentUser) {
        try {
            categoryNames.clear();
            List<SkillCategory> categories = categoryService.getAllCategories();
            for (SkillCategory cat : categories) {
                categoryNames.put(cat.getCategoryId(), cat.getCategoryName());
            }

            allSkills = skillService.getAllSkills();
            buildCategoryTiles();
            applyFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildCategoryTiles() {
        categoryTilesRow.removeAll();

        addCategoryTile("ALL", "🌐 All Categories");
        addCategoryTile("Programming", "💻 Programming");
        addCategoryTile("Web Development", "🌐 Web Dev");
        addCategoryTile("AI / Machine Learning", "🤖 AI & Data");
        addCategoryTile("Design & Creativity", "🎨 Design");
        addCategoryTile("Academics & Science", "🔬 Academics");

        categoryTilesRow.revalidate();
        categoryTilesRow.repaint();
    }

    private void addCategoryTile(String key, String label) {
        boolean isActive = activeCategoryFilter.equalsIgnoreCase(key);
        JPanel tile = new JPanel(new BorderLayout());
        tile.setOpaque(false);
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel textLbl = new JLabel(label, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isActive ? UITheme.BRAND_PURPLE : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(isActive ? UITheme.BRAND_PURPLE : UITheme.BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        textLbl.setFont(UITheme.FONT_SUPPORTING_BOLD);
        textLbl.setForeground(isActive ? Color.WHITE : UITheme.TEXT_MAIN);
        textLbl.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));

        tile.add(textLbl, BorderLayout.CENTER);
        tile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activeCategoryFilter = key;
                buildCategoryTiles();
                applyFilter();
            }
        });

        categoryTilesRow.add(tile);
    }

    private void applyFilter() {
        String query = searchField.getText().trim();
        if (query.startsWith("Search skills")) {
            query = "";
        }

        cardsGrid.removeAll();
        int matchedCount = 0;

        for (Skill skill : allSkills) {
            String catName = categoryNames.getOrDefault(skill.getCategoryId(), "General");

            // Category Filter
            if (!"ALL".equalsIgnoreCase(activeCategoryFilter)) {
                if (!catName.toLowerCase().contains(activeCategoryFilter.toLowerCase())) {
                    continue;
                }
            }

            // Keyword Filter
            if (!query.isEmpty()) {
                String combined = (skill.getSkillName() + " " + skill.getDescription() + " " + catName).toLowerCase();
                if (!combined.contains(query.toLowerCase())) {
                    continue;
                }
            }

            matchedCount++;
            SkillCard card = new SkillCard(skill, catName, new SkillCard.SkillActionListener() {
                @Override
                public void onWantToLearn(Skill s) {
                    mainFrame.showAddWantedSkillDialog(s);
                }

                @Override
                public void onOfferSkill(Skill s) {
                    mainFrame.showAddOfferedSkillDialog(s);
                }
            });

            // Click card opens detail dialog
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSkillDetailModal(skill, catName);
                }
            });

            cardsGrid.add(card);
        }

        resultsCountLbl.setText("Showing " + matchedCount + " matching topics");

        if (matchedCount == 0) {
            JPanel emptyPanel = UITheme.createRoundedCard(12);
            emptyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 36));
            JLabel emptyLbl = new JLabel("No topics match '" + query + "'. Try different keywords or select 'All Categories'.");
            emptyLbl.setFont(UITheme.FONT_BODY);
            emptyLbl.setForeground(UITheme.TEXT_MUTED);
            emptyPanel.add(emptyLbl);
            cardsGrid.setLayout(new GridLayout(1, 1));
            cardsGrid.add(emptyPanel);
        } else {
            cardsGrid.setLayout(new GridLayout(0, 3, 16, 16));
        }

        cardsGrid.revalidate();
        cardsGrid.repaint();
    }

    private void showSkillDetailModal(Skill skill, String category) {
        JDialog dialog = new JDialog(mainFrame, "Topic Details: " + skill.getSkillName(), true);
        dialog.setSize(460, 320);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UITheme.CARD_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));

        JLabel titleLbl = new JLabel(skill.getSkillName());
        titleLbl.setFont(UITheme.FONT_PAGE_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel catLbl = UITheme.createPillBadge(category, UITheme.BRAND_PURPLE, UITheme.BRAND_PURPLE_LIGHT);

        JLabel descLbl = new JLabel("<html><body style='width: 380px; color: #64748B; font-family: Segoe UI; font-size: 11pt; line-height: 1.4;'>" +
                (skill.getDescription() != null && !skill.getDescription().isEmpty()
                        ? skill.getDescription()
                        : "Peer-to-peer exchange topic for students and mentors in higher education.") +
                "</body></html>");

        content.add(catLbl);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(titleLbl);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(descLbl);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnRow.setOpaque(false);

        JButton learnBtn = UITheme.createPrimaryButton("Want to Learn This");
        learnBtn.addActionListener(e -> {
            dialog.dispose();
            mainFrame.showAddWantedSkillDialog(skill);
        });

        JButton offerBtn = UITheme.createSecondaryButton("Offer to Teach");
        offerBtn.addActionListener(e -> {
            dialog.dispose();
            mainFrame.showAddOfferedSkillDialog(skill);
        });

        JButton closeBtn = UITheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());

        btnRow.add(offerBtn);
        btnRow.add(learnBtn);
        btnRow.add(closeBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
