// SkillBridge Master Mock & Seed Data (matches TiDB Cloud sample_data.sql)

export const initialSkills = [
  { skillId: 1, categoryId: 1, skillName: 'Java', description: 'Core Java, OOP, Collections, Multi-threading & JDBC' },
  { skillId: 2, categoryId: 1, skillName: 'Python', description: 'Python programming, data structures, and automation scripting' },
  { skillId: 3, categoryId: 1, skillName: 'C++', description: 'Object-oriented C++, STL algorithms, and memory management' },
  { skillId: 4, categoryId: 2, skillName: 'Photoshop', description: 'Digital photo editing, banners, graphic design & typography' },
  { skillId: 5, categoryId: 2, skillName: 'Figma', description: 'UI/UX interface prototyping, design systems & wireframing' },
  { skillId: 6, categoryId: 3, skillName: 'French', description: 'Conversational and foundational French language practice' },
  { skillId: 7, categoryId: 4, skillName: 'Linear Algebra', description: 'Vector spaces, matrices, eigenvalues & linear transformations' }
];

export const initialCategories = [
  { categoryId: 1, categoryName: 'Programming & Tech' },
  { categoryId: 2, categoryName: 'Design & Creative' },
  { categoryId: 3, categoryName: 'Languages' },
  { categoryId: 4, categoryName: 'Academics & Science' }
];

export const initialUsers = [
  { userId: 1, fullName: 'Koushik Chava', email: 'koushik@example.com', role: 'STUDENT', bio: 'B.Tech CSE student passionate about Java, data structures, and backend systems.', availability: 'Weekdays 5 PM - 8 PM', reliabilityScore: 98.0 },
  { userId: 2, fullName: 'Rahul Sharma', email: 'rahul@example.com', role: 'STUDENT', bio: 'Design enthusiast and digital artist looking to learn backend coding.', availability: 'Weekdays 6 PM - 9 PM', reliabilityScore: 95.0 },
  { userId: 3, fullName: 'Arjun Verma', email: 'arjun@example.com', role: 'STUDENT', bio: 'Python developer interested in learning Java for competitive programming.', availability: 'Weekends 10 AM - 4 PM', reliabilityScore: 90.0 },
  { userId: 4, fullName: 'Admin User', email: 'admin@skillbridge.com', role: 'ADMIN', bio: 'Platform Administrator & Governance Lead.', availability: 'Full-time', reliabilityScore: 100.0 }
];

export const initialMatches = [
  {
    partnerId: 2,
    partnerName: 'Rahul Sharma',
    offeredSkillName: 'Photoshop',
    wantedSkillName: 'Java',
    compatibilityScore: 80.0,
    reciprocal: true,
    reliabilityScore: 95.0,
    availability: 'Weekdays 6 PM - 9 PM'
  },
  {
    partnerId: 3,
    partnerName: 'Arjun Verma',
    offeredSkillName: 'Python',
    wantedSkillName: 'Java',
    compatibilityScore: 75.0,
    reciprocal: false,
    reliabilityScore: 90.0,
    availability: 'Weekends 10 AM - 4 PM'
  }
];

export const initialRequests = [
  {
    requestId: 1,
    senderName: 'Rahul Sharma',
    receiverName: 'Koushik Chava',
    offeredSkillName: 'Photoshop',
    wantedSkillName: 'Java',
    message: 'Hey Koushik! I would love to swap Photoshop fundamentals for Java OOP concepts.',
    status: 'PENDING',
    date: 'Today, 2:30 PM'
  },
  {
    requestId: 2,
    senderName: 'Koushik Chava',
    receiverName: 'Arjun Verma',
    offeredSkillName: 'Java',
    wantedSkillName: 'Python',
    message: 'Hey Arjun! Let us exchange Java algorithms for Python data analysis.',
    status: 'ACCEPTED',
    date: 'Yesterday'
  }
];

export const initialSessions = [
  {
    sessionId: 1,
    partnerName: 'Rahul Sharma',
    skillName: 'Java Core & Multithreading Live Exchange',
    time: 'Thursday at 6:00 PM',
    status: 'SCHEDULED',
    meetingLink: 'https://meet.google.com/sb-java-peer'
  },
  {
    sessionId: 2,
    partnerName: 'Rahul Sharma',
    skillName: 'Photoshop Layers & Masking Masterclass',
    time: 'Completed Nov 15, 2026',
    status: 'COMPLETED',
    meetingLink: 'https://meet.google.com/sb-ps-peer'
  }
];

export const initialOfferedSkills = [
  { id: 1, skillName: 'Java Programming', category: 'Programming & Tech' },
  { id: 2, skillName: 'C++ & Data Structures', category: 'Programming & Tech' }
];

export const initialWantedSkills = [
  { id: 1, skillName: 'Photoshop & Digital Art', category: 'Design & Creative' },
  { id: 2, skillName: 'Figma UI/UX Prototyping', category: 'Design & Creative' }
];

export const initialGoals = [
  {
    goalId: 1,
    title: 'Master Digital Art & Photoshop',
    skillName: 'Photoshop',
    targetSessions: 5,
    completedSessions: 5,
    progressPercentage: 100.0,
    status: 'COMPLETED'
  },
  {
    goalId: 2,
    title: 'Complete 5 Advanced React Sessions',
    skillName: 'React',
    targetSessions: 5,
    completedSessions: 3,
    progressPercentage: 60.0,
    status: 'IN_PROGRESS'
  }
];

export const initialBadges = [
  { badgeId: 1, badgeName: 'First Step', description: 'Registered and verified profile on SkillBridge', unlocked: true, icon: '🚀' },
  { badgeId: 2, badgeName: 'Skill Contributor', description: 'Offered at least one technical skill to peers', unlocked: true, icon: '🎓' },
  { badgeId: 3, badgeName: 'Knowledge Exchanger', description: 'Completed first verified live skill exchange session', unlocked: true, icon: '💡' },
  { badgeId: 4, badgeName: 'Master Mentor', description: 'Maintained 95%+ reliability score over 5 sessions', unlocked: false, icon: '⭐' }
];

export const initialConversations = {
  2: [
    { messageId: 1, senderId: 2, receiverId: 1, messageText: "Hey Koushik! Excited to exchange Java and Photoshop skills!", time: "5:40 PM" },
    { messageId: 2, senderId: 1, receiverId: 2, messageText: "Awesome Rahul! I have prepared some great Java multithreading exercises for us.", time: "5:45 PM" },
    { messageId: 3, senderId: 2, receiverId: 1, messageText: "Perfect! I will have the Photoshop brush and layer masking file ready for Thursday.", time: "6:00 PM" }
  ],
  3: [
    { messageId: 4, senderId: 3, receiverId: 1, messageText: "Hi Koushik, saw your profile. Do you have time for a Java session this weekend?", time: "Nov 16, 2:15 PM" },
    { messageId: 5, senderId: 1, receiverId: 3, messageText: "Hey Arjun! Saturday afternoon works great for me. Let us do it!", time: "Nov 16, 2:30 PM" }
  ]
};
