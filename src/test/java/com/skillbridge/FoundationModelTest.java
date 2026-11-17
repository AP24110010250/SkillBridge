package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.skillbridge.model.Student;
import com.skillbridge.model.Mentor;
import com.skillbridge.model.User;
import com.skillbridge.model.SkillCategory;
import com.skillbridge.model.Skill;

public class FoundationModelTest {

    @Test
    public void testUserPolymorphism() {
        User student = new Student(1, "Koushik", "koushik@example.com", "hash", "Bio", "Evenings", 98.0);
        User mentor = new Mentor(2, "Prof. Rao", "rao@example.com", "hash", "Bio", "Mornings", 99.5);

        assertEquals("STUDENT", student.getRole());
        assertEquals("MENTOR", mentor.getRole());
        assertTrue(student.getUserTypeDescription().contains("Student"));
        assertTrue(mentor.getUserTypeDescription().contains("Mentor"));
    }

    @Test
    public void testSkillModelEncapsulation() {
        SkillCategory category = new SkillCategory(1, "Programming", "Tech skills");
        assertEquals(1, category.getCategoryId());
        assertEquals("Programming", category.getCategoryName());

        Skill skill = new Skill(10, 1, "Java", "Core Java Programming");
        assertEquals(10, skill.getSkillId());
        assertEquals(1, skill.getCategoryId());
        assertEquals("Java", skill.getSkillName());
    }
}
