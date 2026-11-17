package com.skillbridge.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.skillbridge.model.AchievementBadge;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;

// Utility demonstrating Java Unit 3 File I/O: Streams, Readers, Writers, and Object Serialization
public class FileExportUtil {

    // 1. Export comprehensive learning portfolio to formatted text report using FileWriter & BufferedWriter
    public static File exportLearningReport(
            User user,
            List<UserSkillOffered> offeredSkills,
            List<UserSkillWanted> wantedSkills,
            List<LearningGoal> goals,
            List<AchievementBadge> badges,
            int totalPoints,
            String targetDirectoryPath) throws IOException {

        File exportDir = new File(targetDirectoryPath != null ? targetDirectoryPath : "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String fileName = "SkillBridge_Portfolio_" + user.getUserId() + "_" + System.currentTimeMillis() + ".txt";
        File outputFile = new File(exportDir, fileName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("====================================================================\n");
            writer.write("             SKILLBRIDGE STUDENT LEARNING PORTFOLIO                 \n");
            writer.write("====================================================================\n");
            writer.write("Generated On: " + dateFormat.format(new Date()) + "\n");
            writer.write("User ID     : #" + user.getUserId() + "\n");
            writer.write("Full Name   : " + user.getFullName() + "\n");
            writer.write("Email       : " + user.getEmail() + "\n");
            writer.write("Role        : " + user.getRole() + "\n");
            writer.write("Reliability : " + user.getReliabilityScore() + "%\n");
            writer.write("Total Points: " + totalPoints + " pts\n");
            writer.write("Availability: " + (user.getAvailability() != null ? user.getAvailability() : "Not specified") + "\n");
            writer.write("Bio         : " + (user.getBio() != null ? user.getBio() : "") + "\n\n");

            writer.write("--------------------------------------------------------------------\n");
            writer.write("  SKILLS OFFERED (CAN TEACH)\n");
            writer.write("--------------------------------------------------------------------\n");
            if (offeredSkills == null || offeredSkills.isEmpty()) {
                writer.write("  None added yet.\n");
            } else {
                for (UserSkillOffered off : offeredSkills) {
                    writer.write("  * " + off.getSkillName() + " (" + off.getProficiencyLevel() + ", " + off.getYearsExperience() + " yrs exp)\n");
                }
            }
            writer.write("\n");

            writer.write("--------------------------------------------------------------------\n");
            writer.write("  SKILLS WANTED (WANTS TO LEARN)\n");
            writer.write("--------------------------------------------------------------------\n");
            if (wantedSkills == null || wantedSkills.isEmpty()) {
                writer.write("  None added yet.\n");
            } else {
                for (UserSkillWanted want : wantedSkills) {
                    writer.write("  * " + want.getSkillName() + " (Target: " + want.getTargetLevel() + ", Priority: " + want.getPriority() + ")\n");
                }
            }
            writer.write("\n");

            writer.write("--------------------------------------------------------------------\n");
            writer.write("  LEARNING GOALS & PROGRESS\n");
            writer.write("--------------------------------------------------------------------\n");
            if (goals == null || goals.isEmpty()) {
                writer.write("  No active learning goals.\n");
            } else {
                for (LearningGoal g : goals) {
                    writer.write("  * [" + g.getStatus() + "] " + g.getTitle() + " - " + g.getSkillName() + "\n");
                    writer.write("    Progress: " + g.getCompletedSessions() + "/" + g.getTargetSessions() + " sessions (" + String.format("%.1f", g.getProgressPercentage()) + "%)\n");
                }
            }
            writer.write("\n");

            writer.write("--------------------------------------------------------------------\n");
            writer.write("  UNLOCKED ACHIEVEMENT BADGES\n");
            writer.write("--------------------------------------------------------------------\n");
            if (badges == null || badges.isEmpty()) {
                writer.write("  No badges unlocked yet.\n");
            } else {
                for (AchievementBadge b : badges) {
                    writer.write("  * [BADGE] " + b.getBadgeName() + ": " + b.getBadgeDescription() + "\n");
                }
            }
            writer.write("\n====================================================================\n");
            writer.write("             End of SkillBridge Portfolio Export                    \n");
            writer.write("====================================================================\n");
        }

        return outputFile;
    }

    // 2. Serializable wrapper demonstrating ObjectOutputStream and ObjectInputStream
    public static class SerializableProfileBackup implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int userId;
        private final String fullName;
        private final String email;
        private final int totalPoints;
        private final long backupTimestamp;

        public SerializableProfileBackup(int userId, String fullName, String email, int totalPoints) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.totalPoints = totalPoints;
            this.backupTimestamp = System.currentTimeMillis();
        }

        public int getUserId() {
            return userId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public int getTotalPoints() {
            return totalPoints;
        }

        public long getBackupTimestamp() {
            return backupTimestamp;
        }
    }

    // Save backup using ObjectOutputStream (Serialization)
    public static File serializeProfile(SerializableProfileBackup backup, File targetFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetFile))) {
            oos.writeObject(backup);
        }
        return targetFile;
    }

    // Load backup using ObjectInputStream (Deserialization)
    public static SerializableProfileBackup deserializeProfile(File sourceFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sourceFile))) {
            return (SerializableProfileBackup) ois.readObject();
        }
    }
}
