-- SkillBridge Sample Seed Data

USE skillbridge_db;

-- Insert Skill Categories
INSERT INTO skill_categories (category_name, description) VALUES
('Programming & Tech', 'Software development, programming languages, and web engineering'),
('Design & Creative', 'Graphic design, UI/UX, video editing, and multimedia arts'),
('Languages', 'Foreign spoken and written languages'),
('Academics & Science', 'Mathematics, Physics, Engineering concepts');

-- Insert Skills
INSERT INTO skills (category_id, skill_name, description) VALUES
(1, 'Java', 'Core Java, OOP, Collections, Multi-threading'),
(1, 'Python', 'Python programming, data structures, and scripting'),
(1, 'C++', 'Object-oriented C++ and algorithms'),
(2, 'Photoshop', 'Digital photo editing, banners, graphic design'),
(2, 'Figma', 'UI/UX interface prototyping and design systems'),
(3, 'French', 'Conversational and foundational French language'),
(4, 'Linear Algebra', 'Vector spaces, matrices, linear transformations');

-- Insert Sample Users (Passwords hashed or plain for initial verification)
INSERT INTO users (full_name, email, password_hash, role, bio, availability, reliability_score) VALUES
('Koushik Chava', 'koushik@example.com', 'password123', 'STUDENT', 'B.Tech CSE student passionate about Java and backend development', 'Weekdays 5 PM - 8 PM', 98.0),
('Rahul Sharma', 'rahul@example.com', 'password123', 'STUDENT', 'Design enthusiast and digital artist looking to learn backend coding', 'Weekdays 6 PM - 9 PM', 95.0),
('Arjun Verma', 'arjun@example.com', 'password123', 'STUDENT', 'Python developer interested in learning Java for competitive programming', 'Weekends 10 AM - 4 PM', 90.0),
('Admin User', 'admin@skillbridge.com', 'admin123', 'ADMIN', 'Platform Administrator', 'Full-time', 100.0);

-- Insert Skills Offered
-- Koushik can teach Java (skill 1) and C++ (skill 3)
INSERT INTO user_skills_offered (user_id, skill_id, proficiency_level, years_experience) VALUES
(1, 1, 'Advanced', 2),
(1, 3, 'Intermediate', 1);

-- Rahul can teach Photoshop (skill 4)
INSERT INTO user_skills_offered (user_id, skill_id, proficiency_level, years_experience) VALUES
(2, 4, 'Advanced', 3);

-- Arjun can teach Python (skill 2)
INSERT INTO user_skills_offered (user_id, skill_id, proficiency_level, years_experience) VALUES
(3, 2, 'Advanced', 2);

-- Insert Skills Wanted
-- Koushik wants to learn Photoshop (skill 4)
INSERT INTO user_skills_wanted (user_id, skill_id, target_level, priority) VALUES
(1, 4, 'Intermediate', 'High');

-- Rahul wants to learn Java (skill 1)
INSERT INTO user_skills_wanted (user_id, skill_id, target_level, priority) VALUES
(2, 1, 'Intermediate', 'High');

-- Arjun wants to learn Java (skill 1)
INSERT INTO user_skills_wanted (user_id, skill_id, target_level, priority) VALUES
(3, 1, 'Intermediate', 'Medium');

-- Insert Initial Achievement Badges
INSERT INTO achievement_badges (badge_name, badge_description, required_points) VALUES
('First Step', 'Registered and created profile on SkillBridge', 10),
('Skill Contributor', 'Added at least one offered skill', 25),
('Knowledge Exchanger', 'Completed first skill exchange session', 50),
('Master Mentor', 'Maintained 95%+ reliability score over 5 sessions', 150);
