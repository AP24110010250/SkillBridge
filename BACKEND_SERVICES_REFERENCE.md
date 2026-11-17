# SkillBridge Backend Services Reference (For GUI Integration)

This document lists all backend service methods across modules that the Swing GUI in Phase 9 will call.

---

### 1. UserService (`com.skillbridge.service.UserService`)
- `User registerUser(String fullName, String email, String password, String role, String bio, String availability)`
- `User loginUser(String email, String password)`
- `boolean updateProfile(int userId, String fullName, String bio, String availability)`
- `User getUserById(int userId)`
- `User getUserByEmail(String email)`
- `List<User> getAllUsers()`

---

### 2. SkillService (`com.skillbridge.service.SkillService`)
- `Skill createSkill(int categoryId, String skillName, String description)`
- `List<Skill> getAllSkills()`
- `List<Skill> getSkillsByCategory(int categoryId)`
- `List<Skill> searchSkills(String keyword)`
- `UserSkillOffered addSkillOffered(int userId, int skillId, String proficiency, int yearsExperience)`
- `UserSkillWanted addSkillWanted(int userId, int skillId, String targetLevel, String priority)`
- `List<UserSkillOffered> getUserOfferedSkills(int userId)`
- `List<UserSkillWanted> getUserWantedSkills(int userId)`
- `boolean removeSkillOffered(int offeredId)`
- `boolean removeSkillWanted(int wantedId)`
- `Map<String, List<Skill>> getSkillsGroupedByCategory()`

---

### 3. SkillCategoryService (`com.skillbridge.service.SkillCategoryService`)
- `List<SkillCategory> getAllCategories()`
- `SkillCategory getCategoryById(int id)`
- `boolean addCategory(String categoryName, String description)`

---

### 4. MatchingEngine (`com.skillbridge.matching.MatchingEngine`)
- `List<MatchResult> findMatchesForUser(int userId)`
- Returns list of `MatchResult` objects with:
  - `getPartner()`: Target user
  - `getOverallScore()`: 0 - 100%
  - `isReciprocal()`: boolean
  - `getSkillsTheyTeachMe()`: `List<String>`
  - `getSkillsITeachThem()`: `List<String>`
  - `getSkillScore()`, `getReciprocityScore()`, `getAvailabilityScore()`, `getReliabilityScore()`

---

### 5. ExchangeService (`com.skillbridge.service.ExchangeService`)
- `ExchangeRequest createRequest(int senderId, int receiverId, int offeredSkillId, int wantedSkillId, String message)`
- `boolean acceptRequest(int requestId, Timestamp sessionTime, String meetingLink)`
- `boolean rejectRequest(int requestId)`
- `boolean cancelRequest(int requestId)`
- `List<ExchangeRequest> getReceivedRequests(int userId)`
- `List<ExchangeRequest> getSentRequests(int userId)`
- `List<ExchangeSession> getUserSessions(int userId)`
- `boolean completeSession(int sessionId, String completionNotes)`
- `boolean cancelSession(int sessionId)`
- `Message sendMessage(int senderId, int receiverId, String text)`
- `List<Message> getConversation(int user1Id, int user2Id)`

---

### 6. GamificationService (`com.skillbridge.service.GamificationService`)
- `Review submitReview(int sessionId, int reviewerId, int revieweeId, int rating, String feedback)`
- `int getUserPoints(int userId)`
- `List<SkillPoint> getPointHistory(int userId)`
- `List<AchievementBadge> getUserBadges(int userId)`
- `List<Review> getUserReviews(int userId)`
- `LearningGoal createLearningGoal(int userId, int skillId, String title, int targetSessions)`
- `boolean updateGoalProgress(int goalId, int completedSessions, int targetSessions)`
- `List<LearningGoal> getUserGoals(int userId)`

---

### 7. AdminService (`com.skillbridge.service.AdminService`)
- `UserReport reportUser(int reporterId, int reportedUserId, String reason)`
- `List<UserReport> getAllReports(User adminUser)`
- `boolean handleReport(User adminUser, int reportId, String newStatus)`
- `boolean deactivateUser(User adminUser, int targetUserId)`
- `PlatformStatistics getPlatformStatistics(User adminUser)`

---

### 8. FileExportUtil (`com.skillbridge.util.FileExportUtil`)
- `File exportLearningReport(User user, List<UserSkillOffered> offered, List<UserSkillWanted> wanted, List<LearningGoal> goals, List<AchievementBadge> badges, int totalPoints, String targetDir)`
- `File serializeProfile(SerializableProfileBackup backup, File targetFile)`
- `SerializableProfileBackup deserializeProfile(File sourceFile)`
