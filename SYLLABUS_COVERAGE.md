# SkillBridge Syllabus Coverage Record

This document tracks Java syllabus concepts meaningfully demonstrated in the SkillBridge project, recording each concept, file location, purpose, and the phase in which it was introduced.

---

## Unit 1 — Object-Oriented Programming

| Concept | Java File | Purpose | Phase |
| :--- | :--- | :--- | :--- |
| **Classes & Objects** | `com.skillbridge.model.Skill` | Encapsulates skill entity properties and behaviors. | Phase 1 |
| **Constructors** | `com.skillbridge.model.User`, `LearningGoal.java` | Parameterized and default constructors for initializing model states. | Phase 1, Phase 6 |
| **Encapsulation** | `com.skillbridge.model.SkillCategory`, `LearningGoal.java` | Private state variables accessed via getters and setters with progress boundary checks. | Phase 1, Phase 6 |
| **Inheritance** | `com.skillbridge.model.Student`, `com.skillbridge.model.Mentor`, `com.skillbridge.model.Admin` | Inherits attributes and methods from base abstract `User`. | Phase 1, Phase 2 |
| **Abstract Classes** | `com.skillbridge.model.User` | Common user base class defining abstract contract `getUserTypeDescription()`. | Phase 1 |
| **Interfaces** | `com.skillbridge.dao.BaseDao`, `MatchingStrategy.java` | Generic contracts defining standard database and matching operations. | Phase 1, Phase 4 |
| **Method Overriding & Polymorphism** | `Student.java`, `Mentor.java`, `Admin.java` | Overriding `getUserTypeDescription()` for polymorphic role descriptions. | Phase 1, Phase 2 |
| **this & final Keywords** | `User.java`, `SkillCategoryService.java`, `UserService.java`, `GamificationService.java` | `this` used for field disambiguation; `final` used for immutable references and lambda captures. | Phase 1, Phase 2, Phase 6 |

---

## Unit 2 — Exceptions and Multithreading

| Concept | Java File | Purpose | Phase |
| :--- | :--- | :--- | :--- |
| **Custom Exceptions** | `DatabaseException.java`, `InvalidLoginException.java`, `UserAlreadyExistsException.java`, `DuplicateSkillException.java`, `UnauthorizedAccessException.java` | Custom checked exceptions for authentication, duplicate skills, admin access control, and database error handling. | Phase 1, Phase 2, Phase 3, Phase 7 |
| **try-catch-finally & throws** | `DatabaseConnection.java`, `UserDao.java`, `SkillDao.java`, `AdminDao.java`, `AdminService.java` | Resource management, error propagation, and connection cleanup. | Phase 1, Phase 2, Phase 3, Phase 7 |
| **Thread Class & Runnable Interface** | `NotificationManager.java` | Background worker implementing `Runnable` executing on a dedicated daemon thread. | Phase 5 |
| **Synchronization (`synchronized`, `wait`, `notify`)** | `NotificationManager.java` | Thread-safe singleton, message queuing, and inter-thread signaling without polling. | Phase 5 |

---

## Unit 3 — Java I/O

| Concept | Java File | Purpose | Phase |
| :--- | :--- | :--- | :--- |
| **File Handling & InputStreams** | `DatabaseConnection.java`, `DatabaseInitializer.java` | Reading resource configuration files and SQL script definitions. | Phase 1 |
| **Character Streams (FileWriter, BufferedWriter, BufferedReader)** | `FileExportUtil.java` | Generating formatted textual learning portfolio reports for download/export. | Phase 8 |
| **Byte Streams (FileOutputStream, FileInputStream)** | `FileExportUtil.java` | Writing and reading raw binary profile backups. | Phase 8 |
| **Object Serialization (`Serializable`, `ObjectOutputStream`, `ObjectInputStream`)** | `FileExportUtil.java` | Serializing and deserializing user profile backup objects to and from disk. | Phase 8 |

---

## Unit 4 — Collections and JDBC

| Concept | Java File | Purpose | Phase |
| :--- | :--- | :--- | :--- |
| **ArrayList & List Interface** | `SkillCategoryDao.java`, `SkillDao.java`, `MatchingEngine.java` | Storing and returning query result sets and matching lists dynamically. | Phase 1, Phase 3, Phase 4 |
| **HashSet & Set Interface** | `SkillService.java`, `DefaultMatchingStrategy.java` | Fast O(1) membership testing for skill overlaps and deduplication. | Phase 3, Phase 4 |
| **HashMap & Map Interface** | `SkillService.java`, `FairOpportunityAllocator.java` | Mapping category names and user active workloads for fairness calculations. | Phase 3, Phase 4 |
| **TreeSet & SortedSet** | `FairOpportunityAllocator.java` | Maintaining automatically sorted unique candidates based on Comparable score. | Phase 4 |
| **Comparable Interface** | `MatchResult.java` | Natural ordering of candidates based on overall compatibility scores. | Phase 4 |
| **JDBC Connection & DriverManager** | `DatabaseConnection.java` | Managing MySQL JDBC driver and establishing database sessions. | Phase 1 |
| **PreparedStatement & ResultSet** | `SkillCategoryDao.java`, `UserDao.java`, `SkillDao.java` | Executing parameterized SQL queries securely to prevent SQL injection. | Phase 1, Phase 2, Phase 3 |

---

## Unit 5 — Swing and Event Handling

| Concept | Java File | Purpose | Phase |
| :--- | :--- | :--- | :--- |
| **JFrame & Application Windows** | `MainFrame.java` | Main desktop application window with minimum dimensions and centralized placement. | Phase 9 |
| **JDialog** | `MainFrame.java` | Modal dialogs for user authentication (Login and Registration workflows). | Phase 9 |
| **JPanel & Section Organization** | `MainFrame.java`, `UITheme.java` | Structured panel cards separating different primary operational tasks. | Phase 9 |
| **Layout Managers (BorderLayout, GridLayout, BoxLayout, FlowLayout, CardLayout)** | `MainFrame.java` | Responsive, non-absolute GUI layout structuring cards, sidebars, forms, and grids. | Phase 9 |
| **JButton & ActionListeners** | `MainFrame.java` | User action triggers invoking backend service methods across modules. | Phase 9 |
| **JTable & DefaultTableModel** | `MainFrame.java` | Dynamic rendering of structured tabular records (skills, matches, requests, sessions, badges). | Phase 9 |
| **JTextField, JPasswordField, JTextArea** | `MainFrame.java` | User input collection and password concealment during login and registration. | Phase 9 |
| **JComboBox** | `MainFrame.java` | Dropdown selection for skills, categories, proficiency levels, and partners. | Phase 9 |
| **JScrollPane** | `MainFrame.java` | Scrollable viewport containers for tables and multi-line text areas. | Phase 9 |
| **Delegation Event Model** | `MainFrame.java` | Event listeners decoupling Swing user interactions from background business services. | Phase 9 |
| **SwingUtilities.invokeLater** | `MainFrame.java` | Safe dispatching of GUI updates onto the Swing Event Dispatch Thread (EDT). | Phase 9 |
| **FlatLaf Modern Styling** | `MainFrame.java`, `UITheme.java` | Professional, clean academic productivity visual design system. | Phase 9 |
