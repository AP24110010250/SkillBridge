package com.skillbridge.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.skillbridge.model.Skill;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;
import com.skillbridge.service.SkillCategoryService;
import com.skillbridge.service.SkillService;

/**
 * My Skills View: Visual cards for skills offered to teach and skills wanted to learn,
 * with proficiency chips and deletion actions.
 */
public class MySkillsView extends JPanel {

    private final MainFrame mainFrame;
    private final SkillService skillService;
    private final SkillCategoryService categoryService;

    private JPanel offeredContainer;
    private JPanel wantedContainer;
    private JLabel offeredCountLbl;
    private JLabel wantedCountLbl;

    private final Map<Integer, String> skillNames = new HashMap<>();
    private final Map<Integer, String> catNames = new HashMap<>();
    private User currentUser;

    public MySkillsView(MainFrame mainFrame, SkillService skillService, SkillCategoryService categoryService) {
        this.mainFrame = mainFrame;
        this.skillService = skillService;
        this.categoryService = categoryService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        ScrollablePanel mainContainer = new ScrollablePanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UITheme.APP_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));

        // 1. Header with Quick Add Buttons
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titleLbl = new JLabel("My Skills Portfolio");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtns.setOpaque(false);

        JButton addOfferedBtn = UITheme.createPrimaryButton("+ Offer Skill to Teach");
        addOfferedBtn.setFont(UITheme.FONT_SMALL_BOLD);
        addOfferedBtn.addActionListener(e -> mainFrame.showAddOfferedSkillDialog(null));

        JButton addWantedBtn = UITheme.createSecondaryButton("+ Add Skill to Learn");
        addWantedBtn.setFont(UITheme.FONT_SMALL_BOLD);
        addWantedBtn.addActionListener(e -> mainFrame.showAddWantedSkillDialog(null));

        actionBtns.add(addOfferedBtn);
        actionBtns.add(addWantedBtn);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(actionBtns, BorderLayout.EAST);

        mainContainer.add(header);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Two Column Section (Offered vs Wanted)
        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        columnsPanel.setOpaque(false);

        // Column A: Skills I Can Teach
        JPanel colA = new JPanel(new BorderLayout(0, 10));
        colA.setOpaque(false);

        JPanel headA = new JPanel(new BorderLayout());
        headA.setOpaque(false);
        JLabel titleA = new JLabel("Skills I Can Teach (Offered)");
        titleA.setFont(UITheme.FONT_HEADER);
        titleA.setForeground(UITheme.TEXT_MAIN);
        offeredCountLbl = new JLabel("0 skills");
        offeredCountLbl.setFont(UITheme.FONT_SMALL);
        offeredCountLbl.setForeground(UITheme.TEXT_MUTED);
        headA.add(titleA, BorderLayout.WEST);
        headA.add(offeredCountLbl, BorderLayout.EAST);

        offeredContainer = new JPanel();
        offeredContainer.setLayout(new BoxLayout(offeredContainer, BoxLayout.Y_AXIS));
        offeredContainer.setOpaque(false);

        colA.add(headA, BorderLayout.NORTH);
        colA.add(offeredContainer, BorderLayout.CENTER);

        // Column B: Skills I Want to Learn
        JPanel colB = new JPanel(new BorderLayout(0, 10));
        colB.setOpaque(false);

        JPanel headB = new JPanel(new BorderLayout());
        headB.setOpaque(false);
        JLabel titleB = new JLabel("Skills I Want to Learn (Wanted)");
        titleB.setFont(UITheme.FONT_HEADER);
        titleB.setForeground(UITheme.TEXT_MAIN);
        wantedCountLbl = new JLabel("0 skills");
        wantedCountLbl.setFont(UITheme.FONT_SMALL);
        wantedCountLbl.setForeground(UITheme.TEXT_MUTED);
        headB.add(titleB, BorderLayout.WEST);
        headB.add(wantedCountLbl, BorderLayout.EAST);

        wantedContainer = new JPanel();
        wantedContainer.setLayout(new BoxLayout(wantedContainer, BoxLayout.Y_AXIS));
        wantedContainer.setOpaque(false);

        colB.add(headB, BorderLayout.NORTH);
        colB.add(wantedContainer, BorderLayout.CENTER);

        columnsPanel.add(colA);
        columnsPanel.add(colB);

        mainContainer.add(columnsPanel);

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

        try {
            // Cache categories and skills
            catNames.clear();
            for (SkillCategory c : categoryService.getAllCategories()) {
                catNames.put(c.getCategoryId(), c.getCategoryName());
            }
            skillNames.clear();
            for (Skill s : skillService.getAllSkills()) {
                skillNames.put(s.getSkillId(), s.getSkillName());
            }

            // Load Offered Skills
            offeredContainer.removeAll();
            List<UserSkillOffered> offeredList = skillService.getUserOfferedSkills(currentUser.getUserId());
            offeredCountLbl.setText(offeredList.size() + " skills");

            for (UserSkillOffered off : offeredList) {
                JPanel card = createOfferedCard(off);
                offeredContainer.add(card);
                offeredContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (offeredList.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(10);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 14, 20));
                empty.add(new JLabel("You have not offered any skills yet. Share your strengths!"));
                offeredContainer.add(empty);
            }

            // Load Wanted Skills
            wantedContainer.removeAll();
            List<UserSkillWanted> wantedList = skillService.getUserWantedSkills(currentUser.getUserId());
            wantedCountLbl.setText(wantedList.size() + " skills");

            for (UserSkillWanted want : wantedList) {
                JPanel card = createWantedCard(want);
                wantedContainer.add(card);
                wantedContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (wantedList.isEmpty()) {
                JPanel empty = UITheme.createRoundedCard(10);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 14, 20));
                empty.add(new JLabel("You haven't listed any wanted skills yet. Add what you want to learn!"));
                wantedContainer.add(empty);
            }

            offeredContainer.revalidate();
            offeredContainer.repaint();
            wantedContainer.revalidate();
            wantedContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createOfferedCard(UserSkillOffered off) {
        JPanel card = UITheme.createHoverCard(10);
        card.setLayout(new BorderLayout(10, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        String sName = skillNames.getOrDefault(off.getSkillId(), "Skill #" + off.getSkillId());
        JLabel titleLbl = new JLabel(sName);
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel profPill = UITheme.createPillBadge(
                off.getProficiencyLevel() != null ? off.getProficiencyLevel() : "INTERMEDIATE",
                UITheme.BRAND_PURPLE,
                UITheme.BRAND_PURPLE_LIGHT
        );

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLbl, BorderLayout.WEST);
        top.add(profPill, BorderLayout.EAST);

        JButton removeBtn = UITheme.createSecondaryButton("Remove");
        removeBtn.setFont(UITheme.FONT_SMALL);
        removeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        removeBtn.addActionListener(e -> {
            int conf = JOptionPane.showConfirmDialog(mainFrame, "Remove " + sName + " from offered skills?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    skillService.removeSkillOffered(off.getOfferedId());
                    refreshData(currentUser);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(removeBtn, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createWantedCard(UserSkillWanted want) {
        JPanel card = UITheme.createHoverCard(10);
        card.setLayout(new BorderLayout(10, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        String sName = skillNames.getOrDefault(want.getSkillId(), "Skill #" + want.getSkillId());
        JLabel titleLbl = new JLabel(sName);
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        String priority = want.getPriority() != null ? want.getPriority().toUpperCase() : "MEDIUM";
        Color priColor = "HIGH".equals(priority) ? UITheme.ACCENT_ROSE : ("LOW".equals(priority) ? UITheme.TEXT_MUTED : UITheme.ACCENT_AMBER);
        Color priBg = "HIGH".equals(priority) ? UITheme.ACCENT_ROSE_LIGHT : ("LOW".equals(priority) ? new Color(241, 245, 249) : UITheme.ACCENT_AMBER_LIGHT);

        JLabel priPill = UITheme.createPillBadge(priority + " PRIORITY", priColor, priBg);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLbl, BorderLayout.WEST);
        top.add(priPill, BorderLayout.EAST);

        JLabel targetLbl = new JLabel("Target: " + (want.getTargetLevel() != null ? want.getTargetLevel() : "Working Proficiency"));
        targetLbl.setFont(UITheme.FONT_SMALL);
        targetLbl.setForeground(UITheme.TEXT_MUTED);

        JButton removeBtn = UITheme.createSecondaryButton("Remove");
        removeBtn.setFont(UITheme.FONT_SMALL);
        removeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        removeBtn.addActionListener(e -> {
            int conf = JOptionPane.showConfirmDialog(mainFrame, "Remove " + sName + " from wanted skills?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    skillService.removeSkillWanted(want.getWantedId());
                    refreshData(currentUser);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(targetLbl, BorderLayout.WEST);
        bottom.add(removeBtn, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }
}
