package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import com.skillbridge.model.AchievementBadge;
import com.skillbridge.model.LearningGoal;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkillOffered;
import com.skillbridge.model.UserSkillWanted;
import com.skillbridge.util.FileExportUtil;

public class FileExportTest {

    @Test
    public void testLearningReportExport() throws Exception {
        User user = new Student(1, "Koushik Chava", "koushik@example.com", "pass", "Bio", "Evenings", 98.0);
        List<UserSkillOffered> offered = List.of(new UserSkillOffered(1, 1, 1, "Java", "Advanced", 2));
        List<UserSkillWanted> wanted = List.of(new UserSkillWanted(1, 1, 4, "Photoshop", "Intermediate", "High"));
        List<LearningGoal> goals = List.of(new LearningGoal(1, 1, 4, "Master Digital Art", 5, 3));
        List<AchievementBadge> badges = List.of(new AchievementBadge(1, "First Step", "Welcome badge", 10));

        File exported = FileExportUtil.exportLearningReport(user, offered, wanted, goals, badges, 120, "target/test-exports");
        assertNotNull(exported);
        assertTrue(exported.exists());
        assertTrue(exported.length() > 0);

        // Verify contents
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(exported))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        String text = content.toString();
        assertTrue(text.contains("SKILLBRIDGE STUDENT LEARNING PORTFOLIO"));
        assertTrue(text.contains("Koushik Chava"));
        assertTrue(text.contains("Java"));
        assertTrue(text.contains("Photoshop"));
        assertTrue(text.contains("Master Digital Art"));
        assertTrue(text.contains("First Step"));
    }

    @Test
    public void testSerializationAndDeserialization() throws Exception {
        FileExportUtil.SerializableProfileBackup backup =
                new FileExportUtil.SerializableProfileBackup(1, "Koushik Chava", "koushik@example.com", 250);

        File backupFile = new File("target/profile_backup.ser");
        FileExportUtil.serializeProfile(backup, backupFile);
        assertTrue(backupFile.exists());

        FileExportUtil.SerializableProfileBackup loaded = FileExportUtil.deserializeProfile(backupFile);
        assertNotNull(loaded);
        assertEquals(1, loaded.getUserId());
        assertEquals("Koushik Chava", loaded.getFullName());
        assertEquals("koushik@example.com", loaded.getEmail());
        assertEquals(250, loaded.getTotalPoints());
    }
}
