import axios from 'axios';
import { 
  initialSkills, 
  initialCategories, 
  initialUsers, 
  initialMatches, 
  initialRequests, 
  initialSessions, 
  initialGoals, 
  initialBadges, 
  initialConversations 
} from './mockData';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 3000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const authApi = {
  login: async (email, password) => {
    try {
      const res = await api.post('/auth/login', { email, password });
      if (res.data && (res.data.user || res.data.success)) {
        return res;
      }
    } catch {
      // fallback to mock state
    }

    const input = (email || '').trim().toLowerCase();
    const pass = (password || '').trim();

    // Match exact email, username prefix (e.g. 'koushik' for 'koushik@example.com'), or full name
    const user = initialUsers.find(u => {
      const uEmail = (u.email || '').toLowerCase();
      const uName = (u.fullName || '').toLowerCase();
      const uUser = uEmail.split('@')[0];
      return uEmail === input || uUser === input || uName === input;
    });
    
    if (!user) {
      return { data: { success: false, error: `No account found for "${email}". Pick a demo peer below or sign up!` } };
    }
    
    const expectedPass = user.password || (user.role === 'ADMIN' ? 'admin123' : 'password123');
    if (pass !== expectedPass && pass !== 'password123') {
      return { data: { success: false, error: `Incorrect password for ${user.fullName}. Demo password is: ${user.role === 'ADMIN' ? 'admin123' : 'password123'}` } };
    }
    
    return { data: { success: true, user } };
  },
  register: async (userData) => {
    try {
      const res = await api.post('/auth/register', userData);
      if (res.data && (res.data.user || res.data.success)) return res;
    } catch {
      // fallback
    }

    const newUser = {
      userId: Date.now(),
      fullName: userData.fullName,
      email: userData.email,
      password: userData.password || 'password123',
      role: userData.role || 'STUDENT',
      bio: userData.bio || 'SkillBridge member passionate about collaborative learning.',
      availability: userData.availability || 'Weekdays 5 PM - 8 PM',
      reliabilityScore: 100.0
    };
    initialUsers.push(newUser);
    return { data: { success: true, user: newUser } };
  },
};

export const skillsApi = {
  getAll: async () => {
    try {
      const res = await api.get('/skills');
      return res.data && res.data.length ? res : { data: initialSkills };
    } catch {
      return { data: initialSkills };
    }
  },
  getCategories: async () => {
    try {
      const res = await api.get('/categories');
      return res.data && res.data.length ? res : { data: initialCategories };
    } catch {
      return { data: initialCategories };
    }
  },
  createSkill: async (skillData) => {
    try {
      return await api.post('/skills', skillData);
    } catch {
      return { data: { ...skillData, skillId: Date.now() } };
    }
  },
};

export const matchesApi = {
  getMatches: async (userId = 1) => {
    try {
      const res = await api.get(`/matches?userId=${userId}`);
      return res.data && res.data.length ? res : { data: initialMatches };
    } catch {
      return { data: initialMatches };
    }
  },
};

export const exchangesApi = {
  getUserExchanges: async (userId = 1) => {
    try {
      const res = await api.get(`/exchanges?userId=${userId}`);
      return res;
    } catch {
      return { 
        data: { 
          receivedRequests: initialRequests, 
          sentRequests: [], 
          sessions: initialSessions 
        } 
      };
    }
  },
  createRequest: async (data) => {
    try {
      return await api.post('/exchanges', data);
    } catch {
      return { data: { ...data, requestId: Date.now(), status: 'PENDING' } };
    }
  },
};

export const gamificationApi = {
  getStatus: async (userId = 1) => {
    try {
      const res = await api.get(`/gamification?userId=${userId}`);
      return res;
    } catch {
      return { 
        data: {
          userId,
          points: 150,
          level: 'Level 2: Skill Apprentice',
          badges: initialBadges,
          goals: initialGoals
        } 
      };
    }
  },
};

export const usersApi = {
  getProfile: async (userId = 1) => {
    try {
      return await api.get(`/users?id=${userId}`);
    } catch {
      return { data: { user: initialUsers[0] } };
    }
  },
  getAll: async () => {
    try {
      const res = await api.get('/users');
      return res.data && res.data.length ? res : { data: initialUsers };
    } catch {
      return { data: initialUsers };
    }
  },
};

export const messagesApi = {
  getConversation: async (u1 = 1, u2 = 2) => {
    try {
      const res = await api.get(`/messages?user1=${u1}&user2=${u2}`);
      return res.data && res.data.length ? res : { data: initialConversations[u2] || [] };
    } catch {
      return { data: initialConversations[u2] || [] };
    }
  },
  send: async (senderId, receiverId, messageText) => {
    try {
      return await api.post('/messages', { senderId, receiverId, messageText });
    } catch {
      return { 
        data: { 
          messageId: Date.now(), 
          senderId, 
          receiverId, 
          messageText, 
          time: 'Just now' 
        } 
      };
    }
  },
};

export default api;
