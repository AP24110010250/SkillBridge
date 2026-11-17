# SkillBridge — Peer-to-Peer Intelligent Skill Sharing Desktop Application

SkillBridge is an academic B.Tech CSE desktop application built in Java Swing, designed to intelligently connect students for reciprocal skill exchanges.

---

## 1. Key Features
- **Authentication & Role Management**: Polymorphic `Student`, `Mentor`, and `Admin` roles with SHA-256 password hashing.
- **Skill Management & Discovery**: Browse, search, filter catalog skills, and manage skills offered and skills wanted with duplicate prevention.
- **Intelligent Matching Engine**: Formula-driven scoring assessing skill compatibility (50%), reciprocity (25%), availability overlap (15%), and reliability (10%).
- **Fair Opportunity Allocation**: Load-balancing algorithm preventing star mentors from being monopolized.
- **Exchange Requests & Session Scheduling**: End-to-end lifecycle management from request creation to auto-scheduled sessions.
- **Persistent Messaging**: Chat messaging stored directly in MySQL.
- **Gamification & Learning Progress**: Verified reviews, 1-5 star ratings, point ledger, milestone badge unlocking, and goal progress tracking.
- **Admin Dashboard**: System metrics, user reporting, and account administration with role security guards.
- **File I/O & Serialization**: Formatted text learning portfolio export and binary profile serialization.

---

## 2. Technology Stack
- **Language**: Java 17+ (Core Java)
- **GUI**: Java Swing with FlatLaf modern UI styling
- **Database**: MySQL 8.0 via JDBC
- **Build Tool**: Apache Maven

---

## 3. How to Run the Application

### Prerequisites
1. Ensure MySQL Server is running locally on port 3306.
2. Verify credentials in `src/main/resources/database.properties` (defaults: `user=root`, `password=root123`).

### Launch via Maven:
```bash
mvn compile exec:java "-Dexec.mainClass=com.skillbridge.gui.MainFrame"
```

### Pre-loaded Seed Accounts:
- **Koushik Chava (Student)**:
  - Email: `koushik@example.com`
  - Password: `password123`
- **Rahul Sharma (Student / Reciprocal Match)**:
  - Email: `rahul@example.com`
  - Password: `password123`
- **Admin User (Admin)**:
  - Email: `admin@skillbridge.com`
  - Password: `admin123`
