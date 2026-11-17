import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const authApi = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (userData) => api.post('/auth/register', userData),
};

export const skillsApi = {
  getAll: () => api.get('/skills'),
  getCategories: () => api.get('/categories'),
  createSkill: (skillData) => api.post('/skills', skillData),
};

export const matchesApi = {
  getMatches: (userId = 1) => api.get(`/matches?userId=${userId}`),
};

export const exchangesApi = {
  getUserExchanges: (userId = 1) => api.get(`/exchanges?userId=${userId}`),
  createRequest: (data) => api.post('/exchanges', data),
};

export const gamificationApi = {
  getStatus: (userId = 1) => api.get(`/gamification?userId=${userId}`),
};

export const usersApi = {
  getProfile: (userId = 1) => api.get(`/users?id=${userId}`),
  getAll: () => api.get('/users'),
};

export default api;
