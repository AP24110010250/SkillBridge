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
      return res;
    } catch {
      const user = initialUsers.find(u => u.email.toLowerCase() === email.toLowerCase()) || initialUsers[0];
      return { data: { success: true, user } };
    }
  },
  register: async (userData) => {
    try {
      return await api.post('/auth/register', userData);
    } catch {
      return { data: { success: true, user: { ...userData, userId: Date.now() } } };
    }
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
