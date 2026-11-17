-- SkillBridge Database Schema
-- Normalized schema for Peer-to-Peer Intelligent Skill Sharing Platform

CREATE DATABASE IF NOT EXISTS skillbridge_db;
USE skillbridge_db;

-- 1. Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT', -- STUDENT, MENTOR, ADMIN
    bio TEXT,
    availability VARCHAR(255),
    reliability_score DOUBLE NOT NULL DEFAULT 100.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Skill categories table
CREATE TABLE IF NOT EXISTS skill_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 3. Skills master table
CREATE TABLE IF NOT EXISTS skills (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    CONSTRAINT fk_skill_category FOREIGN KEY (category_id) 
        REFERENCES skill_categories(category_id) ON DELETE CASCADE
);

-- 4. User skills offered (skills a user can teach)
CREATE TABLE IF NOT EXISTS user_skills_offered (
    offered_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    proficiency_level VARCHAR(50) DEFAULT 'Intermediate', -- Beginner, Intermediate, Advanced, Expert
    years_experience INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_offered_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_offered_skill FOREIGN KEY (skill_id) 
        REFERENCES skills(skill_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_skill_offered UNIQUE (user_id, skill_id)
);

-- 5. User skills wanted (skills a user wants to learn)
CREATE TABLE IF NOT EXISTS user_skills_wanted (
    wanted_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    target_level VARCHAR(50) DEFAULT 'Intermediate',
    priority VARCHAR(20) DEFAULT 'Medium', -- Low, Medium, High
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wanted_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_wanted_skill FOREIGN KEY (skill_id) 
        REFERENCES skills(skill_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_skill_wanted UNIQUE (user_id, skill_id)
);

-- 6. Exchange requests table
CREATE TABLE IF NOT EXISTS exchange_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    offered_skill_id INT NOT NULL,
    wanted_skill_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED, CANCELLED
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_req_sender FOREIGN KEY (sender_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_receiver FOREIGN KEY (receiver_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_offered_skill FOREIGN KEY (offered_skill_id) 
        REFERENCES skills(skill_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_wanted_skill FOREIGN KEY (wanted_skill_id) 
        REFERENCES skills(skill_id) ON DELETE CASCADE
);

-- 7. Exchange sessions table
CREATE TABLE IF NOT EXISTS exchange_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    scheduled_time DATETIME NOT NULL,
    duration_minutes INT DEFAULT 60,
    meeting_link VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, COMPLETED, CANCELLED
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_request FOREIGN KEY (request_id) 
        REFERENCES exchange_requests(request_id) ON DELETE CASCADE
);

-- 8. Messages table
CREATE TABLE IF NOT EXISTS messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_text TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_receiver FOREIGN KEY (receiver_id) 
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- 9. Reviews and ratings table
CREATE TABLE IF NOT EXISTS reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    reviewee_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rev_session FOREIGN KEY (session_id) 
        REFERENCES exchange_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_reviewer FOREIGN KEY (reviewer_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_reviewee FOREIGN KEY (reviewee_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_session_reviewer UNIQUE (session_id, reviewer_id)
);

-- 10. Gamification: Skill points and transactions
CREATE TABLE IF NOT EXISTS skill_points (
    point_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    points INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_points_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- 11. Gamification: Achievement badges master table
CREATE TABLE IF NOT EXISTS achievement_badges (
    badge_id INT AUTO_INCREMENT PRIMARY KEY,
    badge_name VARCHAR(100) NOT NULL UNIQUE,
    badge_description VARCHAR(255) NOT NULL,
    required_points INT DEFAULT 0
);

-- 12. User achievements unlocked
CREATE TABLE IF NOT EXISTS user_achievements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    badge_id INT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ach_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ach_badge FOREIGN KEY (badge_id) 
        REFERENCES achievement_badges(badge_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_badge UNIQUE (user_id, badge_id)
);

-- 13. Learning goals and progress
CREATE TABLE IF NOT EXISTS learning_goals (
    goal_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    target_sessions INT DEFAULT 5,
    completed_sessions INT DEFAULT 0,
    progress_percentage DOUBLE DEFAULT 0.0,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_goal_skill FOREIGN KEY (skill_id) 
        REFERENCES skills(skill_id) ON DELETE CASCADE
);

-- 14. Admin activity and reports
CREATE TABLE IF NOT EXISTS user_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    reporter_id INT NOT NULL,
    reported_user_id INT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, RESOLVED, DISMISSED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rep_reporter FOREIGN KEY (reporter_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_rep_reported FOREIGN KEY (reported_user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE
);
