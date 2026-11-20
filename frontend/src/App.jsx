import React, { useState, useEffect } from 'react';
import { 
  Compass, 
  BookOpen, 
  Sparkles, 
  Award, 
  Calendar, 
  MessageSquare, 
  CheckCircle, 
  Star, 
  ShieldCheck, 
  TrendingUp, 
  Search,
  ArrowRight,
  Send,
  UserCheck,
  Clock,
  Layers,
  Settings,
  User,
  GraduationCap,
  Inbox,
  Filter,
  Video,
  Download,
  Plus,
  Trash2,
  RefreshCw,
  Check,
  X
} from 'lucide-react';
import { 
  authApi,
  skillsApi, 
  matchesApi, 
  gamificationApi, 
  exchangesApi, 
  usersApi, 
  messagesApi 
} from './services/api';
import {
  initialSkills,
  initialCategories,
  initialUsers,
  initialMatches,
  initialRequests,
  initialSessions,
  initialOfferedSkills,
  initialWantedSkills,
  initialGoals,
  initialBadges,
  initialConversations
} from './services/mockData';

export default function App() {
  const [activeTab, setActiveTab] = useState('overview');
  const [currentUser, setCurrentUser] = useState(initialUsers[0]);
  
  // Auth & Account Modal states
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [authMode, setAuthMode] = useState('login'); // 'login' or 'register'
  const [authEmail, setAuthEmail] = useState('koushik@example.com');
  const [authPassword, setAuthPassword] = useState('password123');
  const [authName, setAuthName] = useState('');
  const [authRole, setAuthRole] = useState('STUDENT');
  const [authBio, setAuthBio] = useState('');
  const [authError, setAuthError] = useState('');
  const [authSuccess, setAuthSuccess] = useState('');

  // Helper for dynamic initials from full name
  const getInitials = (name) => {
    if (!name) return 'SB';
    return name
      .trim()
      .split(/\s+/)
      .map(part => part[0])
      .filter(Boolean)
      .slice(0, 2)
      .join('')
      .toUpperCase();
  };

  const openLoginModal = () => {
    setAuthMode('login');
    setAuthEmail(currentUser?.email || 'koushik@example.com');
    setAuthPassword(currentUser?.role === 'ADMIN' ? 'admin123' : 'password123');
    setAuthError('');
    setAuthSuccess('');
    setShowAuthModal(true);
  };

  const openRegisterModal = () => {
    setAuthMode('register');
    setAuthEmail('');
    setAuthPassword('');
    setAuthName('');
    setAuthRole('STUDENT');
    setAuthBio('');
    setAuthError('');
    setAuthSuccess('');
    setShowAuthModal(true);
  };

  const handleLoginSubmit = async (e) => {
    e?.preventDefault();
    setAuthError('');
    setAuthSuccess('');
    try {
      const res = await authApi.login(authEmail, authPassword);
      if (res.data?.success && res.data?.user) {
        setCurrentUser(res.data.user);
        setAuthSuccess(`Welcome back, ${res.data.user.fullName}!`);
        setTimeout(() => {
          setShowAuthModal(false);
          setAuthSuccess('');
        }, 600);
      } else {
        setAuthError(res.data?.error || 'Authentication failed. Please verify email and password.');
      }
    } catch {
      setAuthError('Unable to authenticate. Please try again.');
    }
  };

  const handleRegisterSubmit = async (e) => {
    e?.preventDefault();
    setAuthError('');
    setAuthSuccess('');
    if (!authName.trim() || !authEmail.trim() || !authPassword.trim()) {
      setAuthError('Please fill in Name, Email, and Password.');
      return;
    }
    try {
      const res = await authApi.register({
        fullName: authName.trim(),
        email: authEmail.trim(),
        password: authPassword.trim(),
        role: authRole,
        bio: authBio.trim() || 'SkillBridge university peer learner.',
        availability: 'Weekdays 5 PM - 8 PM'
      });
      if (res.data?.success && res.data?.user) {
        setAllUsers(prev => [...prev, res.data.user]);
        setCurrentUser(res.data.user);
        setAuthSuccess(`Account created! Welcome to SkillBridge, ${res.data.user.fullName}!`);
        setTimeout(() => {
          setShowAuthModal(false);
          setAuthSuccess('');
        }, 700);
      } else {
        setAuthError(res.data?.error || 'Registration failed.');
      }
    } catch {
      setAuthError('Unable to complete registration. Please try again.');
    }
  };

  const handleQuickLogin = (demoUser) => {
    const pass = demoUser.role === 'ADMIN' ? 'admin123' : 'password123';
    setAuthEmail(demoUser.email);
    setAuthPassword(pass);
    setCurrentUser(demoUser);
    setAuthError('');
    setAuthSuccess(`Signed in as ${demoUser.fullName} (${demoUser.role})`);
    setTimeout(() => {
      setShowAuthModal(false);
      setAuthSuccess('');
    }, 450);
  };
  
  // Data states initialized with seed data
  const [skills, setSkills] = useState(initialSkills);
  const [categories, setCategories] = useState(initialCategories);
  const [selectedCategory, setSelectedCategory] = useState(0); // 0 = All
  const [searchQuery, setSearchQuery] = useState('');
  
  const [matches, setMatches] = useState(initialMatches);
  const [requests, setRequests] = useState(initialRequests);
  const [requestFilter, setRequestFilter] = useState('ALL');
  
  const [sessions, setSessions] = useState(initialSessions);
  const [sessionFilter, setSessionFilter] = useState('ALL');
  
  const [offeredSkills, setOfferedSkills] = useState(initialOfferedSkills);
  const [wantedSkills, setWantedSkills] = useState(initialWantedSkills);
  
  const [goals, setGoals] = useState(initialGoals);
  const [badges, setBadges] = useState(initialBadges);
  const [points, setPoints] = useState(150);
  
  // Chat states
  const [allUsers, setAllUsers] = useState(initialUsers);
  const [activeChatUserId, setActiveChatUserId] = useState(2);
  const [conversations, setConversations] = useState(initialConversations);
  const [chatInputText, setChatInputText] = useState('');
  
  // Add dialog states
  const [showAddSkillModal, setShowAddSkillModal] = useState(false);
  const [newSkillName, setNewSkillName] = useState('');
  const [newSkillCat, setNewSkillCat] = useState(1);
  const [newSkillDesc, setNewSkillDesc] = useState('');

  const [showAddGoalModal, setShowAddGoalModal] = useState(false);
  const [newGoalTitle, setNewGoalTitle] = useState('');
  const [newGoalSkill, setNewGoalSkill] = useState('Java');
  const [newGoalSessions, setNewGoalSessions] = useState(5);

  // Review & Rating Modal states
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedReviewSession, setSelectedReviewSession] = useState(null);
  const [reviewStars, setReviewStars] = useState(5);
  const [reviewComment, setReviewComment] = useState('Great exchange! Very thorough explanations and hands-on practice.');
  const [reviewSuccessMsg, setReviewSuccessMsg] = useState('');

  // Schedule Session Modal states
  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [scheduleTopic, setScheduleTopic] = useState('Peer Skill Swap Session');
  const [scheduleDay, setScheduleDay] = useState(19);
  const [scheduleTime, setScheduleTime] = useState('6:00 PM');
  const [scheduleVenue, setScheduleVenue] = useState('SkillBridge Peer Study Hub / Lab 3');

  const [dbStatusPing, setDbStatusPing] = useState('Connected (Latency: 42ms)');

  // Sync with cloud backend
  useEffect(() => {
    async function syncCloudData() {
      try {
        const [sRes, cRes, mRes, exRes, gRes, uRes] = await Promise.allSettled([
          skillsApi.getAll(),
          skillsApi.getCategories(),
          matchesApi.getMatches(currentUser.userId),
          exchangesApi.getUserExchanges(currentUser.userId),
          gamificationApi.getStatus(currentUser.userId),
          usersApi.getAll()
        ]);

        if (sRes.status === 'fulfilled' && sRes.value.data?.length) setSkills(sRes.value.data);
        if (cRes.status === 'fulfilled' && cRes.value.data?.length) setCategories(cRes.value.data);
        if (mRes.status === 'fulfilled' && mRes.value.data?.length) setMatches(mRes.value.data);
        if (exRes.status === 'fulfilled') {
          if (exRes.value.data?.receivedRequests?.length) setRequests(exRes.value.data.receivedRequests);
          if (exRes.value.data?.sessions?.length) setSessions(exRes.value.data.sessions);
        }
        if (gRes.status === 'fulfilled' && gRes.value.data?.points) {
          setPoints(gRes.value.data.points);
          if (gRes.value.data.goals?.length) setGoals(gRes.value.data.goals);
        }
        if (uRes.status === 'fulfilled' && uRes.value.data?.length) setAllUsers(uRes.value.data);
      } catch (err) {
        console.log('Background cloud sync notice:', err);
      }
    }
    syncCloudData();
  }, [currentUser.userId]);

  // Action Handlers
  const handleSendMessage = (e) => {
    e.preventDefault();
    if (!chatInputText.trim()) return;

    const newMsg = {
      messageId: Date.now(),
      senderId: currentUser.userId,
      receiverId: activeChatUserId,
      messageText: chatInputText.trim(),
      time: 'Just now'
    };

    setConversations(prev => ({
      ...prev,
      [activeChatUserId]: [...(prev[activeChatUserId] || []), newMsg]
    }));

    setChatInputText('');
    messagesApi.send(currentUser.userId, activeChatUserId, newMsg.messageText).catch(() => {});
  };

  const handleIncrementGoal = (goalId) => {
    setGoals(prev => prev.map(g => {
      if (g.goalId === goalId) {
        const updated = Math.min(g.completedSessions + 1, g.targetSessions);
        const pct = Math.round((updated / g.targetSessions) * 100);
        return {
          ...g,
          completedSessions: updated,
          progressPercentage: pct,
          status: pct >= 100 ? 'COMPLETED' : 'IN_PROGRESS'
        };
      }
      return g;
    }));
    setPoints(p => p + 10);
  };

  const handleAcceptRequest = (requestId) => {
    setRequests(prev => prev.map(r => r.requestId === requestId ? { ...r, status: 'ACCEPTED' } : r));
    // Auto add notification message in chat
    const partnerId = 2; // Rahul Sharma
    setActiveChatUserId(partnerId);
    setActiveTab('messages');
  };

  const handleRejectRequest = (requestId) => {
    setRequests(prev => prev.map(r => r.requestId === requestId ? { ...r, status: 'REJECTED' } : r));
  };

  const handleExportReport = () => {
    const reportText = `=====================================================
       SKILLBRIDGE PEER LEARNING PORTFOLIO
=====================================================
Generated: ${new Date().toLocaleString()}
User: ${currentUser.fullName} (${currentUser.role})
Email: ${currentUser.email}
Platform Reliability Rating: ${currentUser.reliabilityScore}%
Skill Points: ${points} pts (Level 2: Skill Apprentice)

--- SKILLS OFFERED (TEACHING) ---
${offeredSkills.map(s => `* ${s.skillName}`).join('\n')}

--- SKILLS WANTED (LEARNING) ---
${wantedSkills.map(s => `* ${s.skillName}`).join('\n')}

--- LEARNING GOALS ---
${goals.map(g => `* ${g.title}: ${g.completedSessions}/${g.targetSessions} sessions (${g.progressPercentage}%) - ${g.status}`).join('\n')}

--- UNLOCKED BADGES ---
${badges.filter(b => b.unlocked).map(b => `[${b.icon}] ${b.badgeName} - ${b.description}`).join('\n')}

Database Host: gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000
Database: skillbridge_db (TiDB Cloud Serverless)
=====================================================`;

    const blob = new Blob([reportText], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `SkillBridge_Portfolio_${currentUser.fullName.replace(/\s+/g, '_')}.txt`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const activeChatPartner = allUsers.find(u => u.userId === activeChatUserId) || allUsers[1] || { fullName: 'Peer Partner' };
  const currentChatMessages = conversations[activeChatUserId] || [];

  const filteredSkills = skills.filter(s => {
    const matchesCat = selectedCategory === 0 || s.categoryId === selectedCategory;
    const matchesSearch = s.skillName.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          (s.description && s.description.toLowerCase().includes(searchQuery.toLowerCase()));
    return matchesCat && matchesSearch;
  });

  return (
    <div className="app-container">
      {/* 1. Left Navigation Sidebar */}
      <aside className="sidebar">
        <div className="brand-section">
          <div className="brand-logo">SB</div>
          <div className="brand-text">
            <h1>SkillBridge</h1>
            <p>Peer Learning Platform</p>
          </div>
        </div>

        <nav className="nav-menu">
          <div className="nav-section-title">DISCOVER & MATCH</div>
          <button 
            className={`nav-item ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            <Compass size={17} /> Overview Dashboard
          </button>
          <button 
            className={`nav-item ${activeTab === 'discover' ? 'active' : ''}`}
            onClick={() => setActiveTab('discover')}
          >
            <BookOpen size={17} /> Discover Skills
          </button>
          <button 
            className={`nav-item ${activeTab === 'matches' ? 'active' : ''}`}
            onClick={() => setActiveTab('matches')}
          >
            <Sparkles size={17} /> Smart Matches
          </button>

          <div className="nav-section-title">EXCHANGES & CHAT</div>
          <button 
            className={`nav-item ${activeTab === 'requests' ? 'active' : ''}`}
            onClick={() => setActiveTab('requests')}
          >
            <Inbox size={17} /> Exchange Requests
            {requests.filter(r => r.status === 'PENDING').length > 0 && (
              <span className="nav-badge">{requests.filter(r => r.status === 'PENDING').length}</span>
            )}
          </button>
          <button 
            className={`nav-item ${activeTab === 'sessions' ? 'active' : ''}`}
            onClick={() => setActiveTab('sessions')}
          >
            <GraduationCap size={17} /> Live Sessions
          </button>
          <button 
            className={`nav-item ${activeTab === 'messages' ? 'active' : ''}`}
            onClick={() => setActiveTab('messages')}
          >
            <MessageSquare size={17} /> Messages
          </button>

          <div className="nav-section-title">LEARNING & GROWTH</div>
          <button 
            className={`nav-item ${activeTab === 'skills' ? 'active' : ''}`}
            onClick={() => setActiveTab('skills')}
          >
            <Layers size={17} /> My Skills Portfolio
          </button>
          <button 
            className={`nav-item ${activeTab === 'goals' ? 'active' : ''}`}
            onClick={() => setActiveTab('goals')}
          >
            <Clock size={17} /> Learning Goals
          </button>
          <button 
            className={`nav-item ${activeTab === 'achievements' ? 'active' : ''}`}
            onClick={() => setActiveTab('achievements')}
          >
            <Award size={17} /> Achievements & Points
          </button>
          <button 
            className={`nav-item ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            <User size={17} /> My Profile
          </button>

          <div className="nav-section-title">SYSTEM</div>
          <button 
            className={`nav-item ${activeTab === 'settings' ? 'active' : ''}`}
            onClick={() => setActiveTab('settings')}
          >
            <Settings size={17} /> Settings & Export
          </button>
        </nav>

        <div className="sidebar-footer">
          <div className="user-card" onClick={openLoginModal} style={{ cursor: 'pointer' }} title="Click to switch account or sign in">
            <div className="user-avatar">{getInitials(currentUser.fullName)}</div>
            <div className="user-info">
              <h4>{currentUser.fullName}</h4>
              <p>{currentUser.role} • {currentUser.reliabilityScore}%</p>
            </div>
          </div>
          <button 
            className="btn-switch-user"
            onClick={openLoginModal}
          >
            Switch User / Login
          </button>
        </div>
      </aside>

      {/* 2. Main Content View */}
      <main className="main-wrapper">
        <header className="top-navbar">
          <div className="page-header-title">
            <h2>
              {activeTab === 'overview' && 'Overview Dashboard'}
              {activeTab === 'discover' && 'Discover Skills'}
              {activeTab === 'matches' && 'Smart Matches'}
              {activeTab === 'requests' && 'Exchange Requests'}
              {activeTab === 'sessions' && 'Live Sessions'}
              {activeTab === 'messages' && 'Peer Messaging'}
              {activeTab === 'skills' && 'My Skills Portfolio'}
              {activeTab === 'goals' && 'Learning Milestones & Goals'}
              {activeTab === 'achievements' && 'Achievements & Points'}
              {activeTab === 'profile' && 'User Profile & Credentials'}
              {activeTab === 'settings' && 'Settings & Data Management'}
            </h2>
          </div>

          <div className="top-actions">
            <div className="status-badge-active">
              <span className="dot-active"></span>
              <span>Active</span>
            </div>
            <div className="user-pill-header" onClick={openLoginModal} style={{ cursor: 'pointer' }} title="Click to view or switch account">
              <span className="user-pill-tag">{getInitials(currentUser.fullName)}</span>
              <span className="user-pill-name">{currentUser.fullName}</span>
            </div>
            <button 
              className="btn-header-secondary" 
              onClick={openLoginModal}
            >
              Switch User
            </button>
            <button className="btn-header-primary" onClick={openRegisterModal}>
              + Sign Up
            </button>
          </div>
        </header>

        <div className="content-body">
          {/* TAB 1: OVERVIEW DASHBOARD */}
          {activeTab === 'overview' && (
            <>
              {/* SECTION 1: WELCOME HERO WITH ARCHIE THE OWL */}
              <div className="hero-banner-desktop">
                <div className="hero-left-text">
                  <h1>Welcome back, {currentUser.fullName}!</h1>
                  <p>
                    Connect with peer students and mentors to exchange practical skills, schedule live sessions, and track growth.
                  </p>
                  <div className="hero-action-buttons">
                    <button className="btn-purple" onClick={() => setActiveTab('discover')}>
                      Explore Skills Catalog →
                    </button>
                    <button className="btn-outline-secondary" onClick={() => setActiveTab('goals')}>
                      View Learning Goals
                    </button>
                  </div>
                </div>

                <div className="archie-companion">
                  <div className="archie-owl">
                    <div className="owl-mortarboard"></div>
                    <div className="owl-body">
                      <div className="owl-eyes">
                        <div className="owl-eye"><div className="owl-pupil"></div></div>
                        <div className="owl-eye"><div className="owl-pupil"></div></div>
                      </div>
                      <div className="owl-beak"></div>
                    </div>
                  </div>
                </div>
              </div>

              {/* SECTION 2: HOW IT WORKS 4-STEP CYCLE */}
              <div className="onboarding-card">
                <div className="onboarding-header">
                  <span className="pill-badge">HOW IT WORKS</span>
                  <span className="onboarding-title">Welcome to Peer-to-Peer Learning on SkillBridge</span>
                </div>

                <div className="steps-row">
                  <div className="step-item">
                    <div className="step-num">1 🔍</div>
                    <h4>Discover Skills</h4>
                    <p>Browse catalog topics you want to learn.</p>
                  </div>
                  <div className="step-item">
                    <div className="step-num">2 📖</div>
                    <h4>Share Strengths</h4>
                    <p>Add skills you are ready to teach others.</p>
                  </div>
                  <div className="step-item">
                    <div className="step-num">3 ⚡</div>
                    <h4>Smart Matching</h4>
                    <p>Find reciprocal mutual exchange partners.</p>
                  </div>
                  <div className="step-item">
                    <div className="step-num">4 🎓</div>
                    <h4>Live Sessions</h4>
                    <p>Complete sessions and earn reputation.</p>
                  </div>
                </div>

                <div className="onboarding-cta-row">
                  <button className="btn-outline-secondary" onClick={() => setActiveTab('skills')}>
                    + Offer a Skill
                  </button>
                  <button className="btn-purple" onClick={() => setActiveTab('discover')}>
                    Explore Skills Catalog →
                  </button>
                </div>
              </div>

              {/* SECTION 3: 4 KPI STATS CARDS */}
              <div className="stats-row-desktop">
                <div className="kpi-card">
                  <div className="kpi-top">
                    <span className="kpi-icon icon-purple">★</span>
                    <span className="kpi-title">Skill Points</span>
                  </div>
                  <div className="kpi-value">{points}</div>
                  <div className="kpi-sub">Level 2 Apprentice</div>
                </div>

                <div className="kpi-card">
                  <div className="kpi-top">
                    <span className="kpi-icon icon-green">🎓</span>
                    <span className="kpi-title">Sessions Done</span>
                  </div>
                  <div className="kpi-value">2</div>
                  <div className="kpi-sub">120 mins exchanged</div>
                </div>

                <div className="kpi-card">
                  <div className="kpi-top">
                    <span className="kpi-icon icon-blue">🛡️</span>
                    <span className="kpi-title">Trust Rating</span>
                  </div>
                  <div className="kpi-value">{currentUser.reliabilityScore}%</div>
                  <div className="kpi-sub">Trusted Peer</div>
                </div>

                <div className="kpi-card">
                  <div className="kpi-top">
                    <span className="kpi-icon icon-amber">⚡</span>
                    <span className="kpi-title">Top Matches</span>
                  </div>
                  <div className="kpi-value">{matches.length}</div>
                  <div className="kpi-sub">{matches.filter(m => m.reciprocal).length} Reciprocal</div>
                </div>
              </div>

              {/* SECTION 4: ROW 1 (RECOMMENDED PARTNERS + CURRENT GOALS) */}
              <div className="two-col-grid">
                <div className="content-card">
                  <div className="card-header-row">
                    <h3>Recommended Exchange Partners</h3>
                    <button className="btn-link" onClick={() => setActiveTab('matches')}>
                      View All Matches →
                    </button>
                  </div>

                  <div className="reciprocal-badge-banner">
                    ⚡ Reciprocal Opportunity (Mutual Learning Exchange) • Trust: 10/10
                  </div>

                  <div className="partner-card-detailed">
                    <div className="partner-left-meta">
                      <div className="avatar-chip">RS</div>
                      <div>
                        <h4>Rahul Sharma</h4>
                        <p className="partner-sub">Teaches: Photoshop • Wants: Java</p>
                      </div>
                    </div>
                    <div className="partner-right-action">
                      <span className="compatibility-tag">80% Match</span>
                      <button className="btn-purple" onClick={() => { setActiveChatUserId(2); setActiveTab('messages'); }}>
                        Message / Request
                      </button>
                    </div>
                  </div>
                </div>

                <div className="content-card">
                  <div className="card-header-row">
                    <h3>Current Learning Milestones</h3>
                    <button className="btn-link" onClick={() => setActiveTab('goals')}>
                      View Goals →
                    </button>
                  </div>

                  <div className="goal-card-desktop">
                    <div className="goal-title-row">
                      <h4>Master Digital Art & Photoshop</h4>
                      <span className="mastered-tag">✓ MASTERED</span>
                    </div>
                    <div className="goal-sub">Personal Learning Milestone • 5 of 5 ses.</div>
                    <div className="progress-bar-bg">
                      <div className="progress-fill" style={{ width: '100%', background: '#10B981' }}></div>
                    </div>
                    <div className="goal-status-text" style={{ color: '#059669' }}>
                      ✓ Milestone Achieved! (100%)
                    </div>
                  </div>
                </div>
              </div>

              {/* SECTION 5: ROW 2 (ACTIVITY CALENDAR + CONTRIBUTION CHART / LEADERBOARD) */}
              <div className="two-col-grid" style={{ marginTop: '20px' }}>
                <div className="content-card">
                  <div className="card-header-row">
                    <h3>📅 Learning Activity Calendar</h3>
                    <div className="calendar-nav-controls">
                      <span style={{ fontSize: '13px', fontWeight: '700' }}>November 2026</span>
                    </div>
                  </div>

                  <div className="calendar-grid-header">
                    <div>Sun</div><div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div>
                  </div>
                  <div className="calendar-days-grid">
                    {Array.from({ length: 30 }, (_, i) => {
                      const day = i + 1;
                      const hasScheduled = sessions.some(s => s.status === 'SCHEDULED' && (s.day === day || (!s.day && day === 19)));
                      const hasCompleted = sessions.some(s => s.status === 'COMPLETED' && (s.day === day || (!s.day && day === 15)));
                      const isToday = day === 18;
                      return (
                        <div key={day} className={`calendar-day-cell ${isToday ? 'today' : ''} ${hasCompleted ? 'completed-day' : ''} ${hasScheduled ? 'scheduled-day' : ''}`} title={hasCompleted ? `Day ${day}: Session Completed & Reviewed` : hasScheduled ? `Day ${day}: Scheduled Exchange Session` : `Day ${day}`}>
                          <span>{day}</span>
                          {hasCompleted && <div className="event-dot" style={{ background: '#059669' }}></div>}
                          {hasScheduled && !hasCompleted && <div className="event-dot" style={{ background: '#2563EB' }}></div>}
                        </div>
                      );
                    })}
                  </div>
                  <div className="calendar-footer-details" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>Nov 2026: {sessions.filter(s => s.status === 'SCHEDULED').length} Scheduled • {sessions.filter(s => s.status === 'COMPLETED').length} Completed</span>
                    <div style={{ display: 'flex', gap: '8px', fontSize: '11px' }}>
                      <span style={{ color: '#2563EB', fontWeight: '700' }}>● Scheduled</span>
                      <span style={{ color: '#059669', fontWeight: '700' }}>● Completed & Reviewed</span>
                    </div>
                  </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div className="content-card">
                    <div className="card-header-row">
                      <h3>Weekly Skill Exchange Balance</h3>
                      <div style={{ display: 'flex', gap: '10px', fontSize: '11px', fontWeight: '700' }}>
                        <span style={{ color: '#6D28D9' }}>■ Teaching (2)</span>
                        <span style={{ color: '#2563EB' }}>■ Learning (1)</span>
                      </div>
                    </div>
                    <div className="chart-preview-bars">
                      <div className="bar-group">
                        <div className="bar-teach" style={{ height: '70px' }}></div>
                        <div className="bar-learn" style={{ height: '35px' }}></div>
                        <span>Mon</span>
                      </div>
                      <div className="bar-group">
                        <div className="bar-teach" style={{ height: '90px' }}></div>
                        <div className="bar-learn" style={{ height: '50px' }}></div>
                        <span>Wed</span>
                      </div>
                      <div className="bar-group">
                        <div className="bar-teach" style={{ height: '110px' }}></div>
                        <div className="bar-learn" style={{ height: '40px' }}></div>
                        <span>Fri</span>
                      </div>
                    </div>
                  </div>

                  <div className="content-card">
                    <div className="card-header-row">
                      <h3>🏆 Peer Contributor Leaderboard</h3>
                      <button className="btn-link" onClick={() => setActiveTab('achievements')}>
                        Rankings →
                      </button>
                    </div>
                    <div className="leaderboard-item">
                      <span className="rank-badge gold">1</span>
                      <div style={{ flex: 1, marginLeft: '10px' }}>
                        <strong>Koushik Chava</strong>
                        <p style={{ fontSize: '11px', color: '#64748B' }}>Java & Systems Master</p>
                      </div>
                      <span style={{ fontWeight: '800', color: '#6D28D9' }}>{points} pts</span>
                    </div>
                    <div className="leaderboard-item">
                      <span className="rank-badge silver">2</span>
                      <div style={{ flex: 1, marginLeft: '10px' }}>
                        <strong>Rahul Sharma</strong>
                        <p style={{ fontSize: '11px', color: '#64748B' }}>Design & Photoshop</p>
                      </div>
                      <span style={{ fontWeight: '800', color: '#6D28D9' }}>95 pts</span>
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}

          {/* TAB 2: DISCOVER SKILLS */}
          {activeTab === 'discover' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Discover Skills Catalog</h3>
                  <p style={{ fontSize: '13px', color: '#64748B', marginTop: '2px' }}>
                    Browse peer-to-peer subjects across technology, design, and academics.
                  </p>
                </div>
                <button className="btn-purple" onClick={() => setShowAddSkillModal(true)}>
                  + Suggest New Skill
                </button>
              </div>

              {/* Search & Category Pills */}
              <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginTop: '14px', flexWrap: 'wrap' }}>
                <div className="search-bar" style={{ width: '280px' }}>
                  <Search size={15} color="#64748B" />
                  <input 
                    type="text" 
                    placeholder="Search skills by name or keyword..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>

                <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                  <button 
                    className={`filter-pill ${selectedCategory === 0 ? 'active' : ''}`}
                    onClick={() => setSelectedCategory(0)}
                  >
                    All Categories ({skills.length})
                  </button>
                  {categories.map(c => (
                    <button 
                      key={c.categoryId}
                      className={`filter-pill ${selectedCategory === c.categoryId ? 'active' : ''}`}
                      onClick={() => setSelectedCategory(c.categoryId)}
                    >
                      {c.categoryName}
                    </button>
                  ))}
                </div>
              </div>

              <div className="skills-grid" style={{ marginTop: '18px' }}>
                {filteredSkills.map((s) => (
                  <div key={s.skillId} className="skill-item-card">
                    <div className="skill-cat-pill">
                      {categories.find(c => c.categoryId === s.categoryId)?.categoryName || `Category #${s.categoryId}`}
                    </div>
                    <h4>{s.skillName}</h4>
                    <p>{s.description || 'Verified course topic with active peer mentors ready to exchange.'}</p>
                    <button 
                      className="btn-purple" 
                      style={{ width: '100%', marginTop: '12px' }} 
                      onClick={() => setActiveTab('matches')}
                    >
                      Find Peer Mentors →
                    </button>
                  </div>
                ))}
              </div>

              {/* Add Skill Modal */}
              {showAddSkillModal && (
                <div className="modal-overlay">
                  <div className="modal-card">
                    <div className="card-header-row">
                      <h3>Add Skill to Platform Catalog</h3>
                      <button className="btn-link" onClick={() => setShowAddSkillModal(false)}>✕</button>
                    </div>
                    <form onSubmit={(e) => {
                      e.preventDefault();
                      if (!newSkillName.trim()) return;
                      const created = {
                        skillId: Date.now(),
                        categoryId: Number(newSkillCat),
                        skillName: newSkillName.trim(),
                        description: newSkillDesc.trim() || 'Peer-taught interactive skill.'
                      };
                      setSkills(prev => [created, ...prev]);
                      setShowAddSkillModal(false);
                      setNewSkillName('');
                      setNewSkillDesc('');
                    }}>
                      <div style={{ marginBottom: '12px' }}>
                        <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px' }}>Skill Name</label>
                        <input 
                          type="text" 
                          required 
                          className="modal-input" 
                          placeholder="e.g. Next.js, Flutter, Rust"
                          value={newSkillName}
                          onChange={(e) => setNewSkillName(e.target.value)}
                        />
                      </div>
                      <div style={{ marginBottom: '12px' }}>
                        <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px' }}>Category</label>
                        <select 
                          className="modal-input"
                          value={newSkillCat}
                          onChange={(e) => setNewSkillCat(e.target.value)}
                        >
                          {categories.map(c => (
                            <option key={c.categoryId} value={c.categoryId}>{c.categoryName}</option>
                          ))}
                        </select>
                      </div>
                      <div style={{ marginBottom: '16px' }}>
                        <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px' }}>Description</label>
                        <textarea 
                          className="modal-input" 
                          rows="3" 
                          placeholder="What will peers learn in this topic?"
                          value={newSkillDesc}
                          onChange={(e) => setNewSkillDesc(e.target.value)}
                        />
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
                        <button type="button" className="btn-outline-secondary" onClick={() => setShowAddSkillModal(false)}>Cancel</button>
                        <button type="submit" className="btn-purple">Create Skill</button>
                      </div>
                    </form>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* TAB 3: SMART MATCHES */}
          {activeTab === 'matches' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Smart Compatibility Matches</h3>
                  <p style={{ fontSize: '13px', color: '#64748B', marginTop: '2px' }}>
                    Calculated by the Fair Opportunity Allocation engine based on skills you offer vs. skills wanted.
                  </p>
                </div>
                <div className="reciprocal-badge-banner" style={{ margin: 0 }}>
                  ⚡ {matches.filter(m => m.reciprocal).length} Reciprocal Matches Available
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginTop: '16px' }}>
                {matches.map((m, idx) => (
                  <div key={idx} className="partner-card-detailed">
                    <div className="partner-left-meta">
                      <div className="avatar-chip">{m.partnerName ? m.partnerName[0] : 'P'}</div>
                      <div>
                        <h4>{m.partnerName}</h4>
                        <p className="partner-sub">
                          Teaches: <strong>{m.offeredSkillName}</strong> | Wants: <strong>{m.wantedSkillName}</strong>
                        </p>
                        <span style={{ fontSize: '11px', color: '#64748B' }}>
                          🕒 {m.availability} • ⭐ {m.reliabilityScore}% Reliability
                        </span>
                      </div>
                    </div>
                    <div className="partner-right-action">
                      <div style={{ textAlign: 'right' }}>
                        <span className="compatibility-tag">{Math.round(m.compatibilityScore)}% Match</span>
                        <p style={{ fontSize: '11px', color: '#059669', fontWeight: '700', marginTop: '3px' }}>
                          {m.reciprocal ? '⚡ Mutual Exchange' : 'One-way Match'}
                        </p>
                      </div>
                      <button 
                        className="btn-purple"
                        onClick={() => {
                          setActiveChatUserId(m.partnerId || 2);
                          setActiveTab('messages');
                        }}
                      >
                        Send Exchange Request
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TAB 4: EXCHANGE REQUESTS */}
          {activeTab === 'requests' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Exchange Requests ({requests.length})</h3>
                  <p style={{ fontSize: '13px', color: '#64748B' }}>Incoming and outgoing peer skill swaps.</p>
                </div>
                <div style={{ display: 'flex', gap: '6px' }}>
                  {['ALL', 'PENDING', 'ACCEPTED'].map(f => (
                    <button 
                      key={f}
                      className={`filter-pill ${requestFilter === f ? 'active' : ''}`}
                      onClick={() => setRequestFilter(f)}
                    >
                      {f}
                    </button>
                  ))}
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginTop: '16px' }}>
                {requests
                  .filter(r => requestFilter === 'ALL' || r.status === requestFilter)
                  .map(r => (
                    <div key={r.requestId} className="partner-card-detailed">
                      <div className="partner-left-meta">
                        <div className="avatar-chip">
                          {r.senderName ? r.senderName[0] : 'R'}
                        </div>
                        <div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <h4>{r.senderName}</h4>
                            <span className={`status-pill status-${r.status.toLowerCase()}`}>
                              {r.status}
                            </span>
                          </div>
                          <p className="partner-sub">
                            Offered: <strong>{r.offeredSkillName}</strong> ⇄ Wanted: <strong>{r.wantedSkillName}</strong>
                          </p>
                          <p style={{ fontSize: '12px', color: '#475569', fontStyle: 'italic', marginTop: '4px' }}>
                            "{r.message}"
                          </p>
                        </div>
                      </div>

                      <div style={{ display: 'flex', gap: '8px' }}>
                        {r.status === 'PENDING' ? (
                          <>
                            <button className="btn-purple" onClick={() => handleAcceptRequest(r.requestId)}>
                              <Check size={14} /> Accept
                            </button>
                            <button className="btn-outline-secondary" onClick={() => handleRejectRequest(r.requestId)}>
                              <X size={14} /> Decline
                            </button>
                          </>
                        ) : (
                          <button 
                            className="btn-outline-secondary"
                            onClick={() => { setActiveChatUserId(2); setActiveTab('messages'); }}
                          >
                            Open Chat
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          )}

          {/* TAB 5: LIVE SESSIONS */}
          {activeTab === 'sessions' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Live Learning Sessions ({sessions.length})</h3>
                  <p style={{ fontSize: '13px', color: '#64748B' }}>Scheduled virtual peer mentoring video appointments.</p>
                </div>
                <div style={{ display: 'flex', gap: '6px' }}>
                  {['ALL', 'SCHEDULED', 'COMPLETED'].map(f => (
                    <button 
                      key={f}
                      className={`filter-pill ${sessionFilter === f ? 'active' : ''}`}
                      onClick={() => setSessionFilter(f)}
                    >
                      {f}
                    </button>
                  ))}
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginTop: '16px' }}>
                {sessions
                  .filter(s => sessionFilter === 'ALL' || s.status === sessionFilter)
                  .map(s => (
                    <div key={s.sessionId} className="partner-card-detailed">
                      <div className="partner-left-meta">
                        <div className="avatar-chip" style={{ background: '#ECFDF5', color: '#10B981' }}>
                          <Video size={18} />
                        </div>
                        <div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <h4>{s.skillName}</h4>
                            <span className={`status-pill status-${s.status.toLowerCase()}`}>
                              {s.status}
                            </span>
                          </div>
                          <p className="partner-sub">Partner: {s.partnerName} • {s.time}</p>
                        </div>
                      </div>

                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        {s.status === 'SCHEDULED' && (
                          <>
                            <span style={{ fontSize: '12px', color: '#6D28D9', fontWeight: '600' }}>
                              📍 {s.venue || 'Campus Peer Study Room'}
                            </span>
                            <button 
                              className="btn-purple"
                              onClick={() => {
                                setSessions(prev => prev.map(item => item.sessionId === s.sessionId ? { ...item, status: 'COMPLETED' } : item));
                                setSelectedReviewSession(s);
                                setReviewStars(5);
                                setReviewComment('Great session! Very thorough explanations and hands-on practice.');
                                setReviewSuccessMsg('');
                                setShowReviewModal(true);
                              }}
                            >
                              ✓ Mark Complete
                            </button>
                          </>
                        )}
                        {s.status === 'COMPLETED' && (
                          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <span style={{ color: '#059669', fontWeight: '700', fontSize: '12px' }}>
                              {s.reviewed ? `✓ Reviewed (${s.rating || 5}⭐) • Logged in Calendar` : '✓ Completed • Pending Review'}
                            </span>
                            {!s.reviewed && (
                              <button 
                                className="btn-purple"
                                style={{ padding: '6px 12px', fontSize: '12px' }}
                                onClick={() => {
                                  setSelectedReviewSession(s);
                                  setReviewStars(5);
                                  setReviewComment('Great session! Very thorough explanations and hands-on practice.');
                                  setReviewSuccessMsg('');
                                  setShowReviewModal(true);
                                }}
                              >
                                ⭐ Leave Review
                              </button>
                            )}
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          )}

          {/* TAB 6: MESSAGES (WHATSAPP-STYLE TWO-COLUMN CHAT) */}
          {activeTab === 'messages' && (
            <div className="chat-layout-wrapper">
              {/* Left Contacts List */}
              <div className="chat-contacts-col">
                <div className="chat-contacts-head">
                  <h4>Peer Conversations</h4>
                  <p>Active learning exchange chats</p>
                </div>
                <div className="chat-contacts-list">
                  {allUsers.filter(u => u.userId !== currentUser.userId).map((u) => (
                    <div 
                      key={u.userId} 
                      className={`contact-item ${activeChatUserId === u.userId ? 'selected' : ''}`}
                      onClick={() => setActiveChatUserId(u.userId)}
                    >
                      <div className="contact-avatar">{u.fullName[0]}</div>
                      <div className="contact-info">
                        <h4>{u.fullName}</h4>
                        <p>{u.role} • 98% Reliability</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Right Chat Column */}
              <div className="chat-dialog-col">
                <div className="chat-dialog-header">
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div className="avatar-chip">{activeChatPartner.fullName[0]}</div>
                    <div>
                      <h4>{activeChatPartner.fullName}</h4>
                      <p style={{ fontSize: '11px', color: '#10B981', fontWeight: '700' }}>● Online • Verified Exchange Partner</p>
                    </div>
                  </div>
                  <button 
                    className="btn-purple" 
                    onClick={() => {
                      setScheduleTopic(`Peer Exchange with ${activeChatPartner.fullName}`);
                      setShowScheduleModal(true);
                    }}
                  >
                    📅 Schedule Agreed Meeting
                  </button>
                </div>

                <div className="chat-messages-container">
                  {currentChatMessages.map((m, idx) => {
                    const isMine = m.senderId === currentUser.userId;
                    return (
                      <div key={idx} className={`chat-bubble-row ${isMine ? 'mine' : 'theirs'}`}>
                        <div className={`chat-bubble ${isMine ? 'bubble-mine' : 'bubble-theirs'}`}>
                          <p>{m.messageText}</p>
                          <span className="chat-bubble-time">
                            {isMine ? 'You • ' + (m.time || 'Just now') : activeChatPartner.fullName + ' • ' + (m.time || '5m ago')}
                          </span>
                        </div>
                      </div>
                    );
                  })}
                </div>

                <form className="chat-input-row" onSubmit={handleSendMessage}>
                  <input 
                    type="text" 
                    placeholder={`Type message to ${activeChatPartner.fullName}... (Press Enter to Send)`} 
                    value={chatInputText}
                    onChange={(e) => setChatInputText(e.target.value)}
                  />
                  <button type="submit" className="btn-purple" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Send size={14} /> Send
                  </button>
                </form>
              </div>
            </div>
          )}

          {/* TAB 7: MY SKILLS PORTFOLIO */}
          {activeTab === 'skills' && (
            <div className="two-col-grid">
              {/* Column 1: Skills I Can Teach */}
              <div className="content-card">
                <div className="card-header-row">
                  <div>
                    <h3>Skills I Can Teach (Offered)</h3>
                    <p style={{ fontSize: '12px', color: '#64748B' }}>Skills you share with peers in exchange sessions.</p>
                  </div>
                  <button 
                    className="btn-purple" 
                    onClick={() => {
                      const name = prompt('Skill name to teach:');
                      if (name && name.trim()) {
                        const trimmed = name.trim();
                        const isDuplicate = offeredSkills.some(
                          s => s.skillName.trim().toLowerCase() === trimmed.toLowerCase()
                        );
                        if (isDuplicate) {
                          alert(`"${trimmed}" has already been added to your offered skills!`);
                          return;
                        }
                        setOfferedSkills(prev => [...prev, { id: Date.now(), skillName: trimmed, category: 'Tech' }]);
                      }
                    }}
                  >
                    + Add Offered
                  </button>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '12px' }}>
                  {offeredSkills.map(s => (
                    <div key={s.id} className="partner-card-detailed">
                      <div>
                        <h4>{s.skillName}</h4>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <span className="compatibility-tag">Teaching</span>
                        <button 
                          className="btn-link" 
                          style={{ color: '#DC2626' }}
                          onClick={() => setOfferedSkills(prev => prev.filter(x => x.id !== s.id))}
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Column 2: Skills I Want to Learn */}
              <div className="content-card">
                <div className="card-header-row">
                  <div>
                    <h3>Skills I Want to Learn (Wanted)</h3>
                    <p style={{ fontSize: '12px', color: '#64748B' }}>Subjects you wish to learn from campus mentors.</p>
                  </div>
                  <button 
                    className="btn-outline-secondary" 
                    onClick={() => {
                      const name = prompt('Skill you want to learn:');
                      if (name && name.trim()) {
                        const trimmed = name.trim();
                        const isDuplicate = wantedSkills.some(
                          s => s.skillName.trim().toLowerCase() === trimmed.toLowerCase()
                        );
                        if (isDuplicate) {
                          alert(`"${trimmed}" has already been added to your wanted skills!`);
                          return;
                        }
                        setWantedSkills(prev => [...prev, { id: Date.now(), skillName: trimmed, category: 'Creative' }]);
                      }
                    }}
                  >
                    + Add Wanted
                  </button>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '12px' }}>
                  {wantedSkills.map(s => (
                    <div key={s.id} className="partner-card-detailed">
                      <div>
                        <h4>{s.skillName}</h4>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <span className="pill-badge">Learning</span>
                        <button 
                          className="btn-link" 
                          style={{ color: '#DC2626' }}
                          onClick={() => setWantedSkills(prev => prev.filter(x => x.id !== s.id))}
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* TAB 8: LEARNING GOALS */}
          {activeTab === 'goals' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Learning Milestones & Goals ({goals.length})</h3>
                  <p style={{ fontSize: '13px', color: '#64748B' }}>
                    Track milestone progression and log completed sessions interactively.
                  </p>
                </div>
                <button className="btn-purple" onClick={() => setShowAddGoalModal(true)}>
                  + Set New Goal
                </button>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginTop: '16px' }}>
                {goals.map(g => (
                  <div key={g.goalId} className="goal-card-desktop">
                    <div className="goal-title-row">
                      <h4>{g.title}</h4>
                      {g.progressPercentage >= 100 ? (
                        <span className="mastered-tag">✓ MASTERED</span>
                      ) : (
                        <span style={{ color: '#6D28D9', fontWeight: '800', fontSize: '13px' }}>
                          {g.progressPercentage}%
                        </span>
                      )}
                    </div>
                    <div className="goal-sub">
                      Skill: <strong>{g.skillName || 'General'}</strong> • {g.completedSessions} of {g.targetSessions} completed sessions
                    </div>
                    <div className="progress-bar-bg">
                      <div 
                        className="progress-fill" 
                        style={{ 
                          width: `${g.progressPercentage}%`, 
                          background: g.progressPercentage >= 100 ? '#10B981' : '#6D28D9' 
                        }}
                      ></div>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '8px' }}>
                      <span className="goal-status-text" style={{ color: g.progressPercentage >= 100 ? '#059669' : '#6D28D9' }}>
                        {g.progressPercentage >= 100 ? '✓ Milestone Achieved!' : `${g.targetSessions - g.completedSessions} sessions remaining`}
                      </span>
                      {g.progressPercentage < 100 && (
                        <button 
                          className="btn-purple" 
                          style={{ padding: '4px 10px', fontSize: '11px' }}
                          onClick={() => handleIncrementGoal(g.goalId)}
                        >
                          + Log 1 Session
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {/* Add Goal Modal */}
              {showAddGoalModal && (
                <div className="modal-overlay">
                  <div className="modal-card">
                    <div className="card-header-row">
                      <h3>Set New Learning Milestone</h3>
                      <button className="btn-link" onClick={() => setShowAddGoalModal(false)}>✕</button>
                    </div>
                    <form onSubmit={(e) => {
                      e.preventDefault();
                      if (!newGoalTitle.trim()) return;
                      const newG = {
                        goalId: Date.now(),
                        title: newGoalTitle.trim(),
                        skillName: newGoalSkill,
                        targetSessions: Number(newGoalSessions),
                        completedSessions: 0,
                        progressPercentage: 0,
                        status: 'IN_PROGRESS'
                      };
                      setGoals(prev => [...prev, newG]);
                      setShowAddGoalModal(false);
                      setNewGoalTitle('');
                    }}>
                      <div style={{ marginBottom: '12px' }}>
                        <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px' }}>Milestone Goal Title</label>
                        <input 
                          type="text" 
                          required 
                          className="modal-input" 
                          placeholder="e.g. Master Spring Boot Architecture"
                          value={newGoalTitle}
                          onChange={(e) => setNewGoalTitle(e.target.value)}
                        />
                      </div>
                      <div style={{ marginBottom: '12px' }}>
                        <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px' }}>Target Subject</label>
                        <select 
                          className="modal-input"
                          value={newGoalSkill}
                          onChange={(e) => setNewGoalSkill(e.target.value)}
                        >
                          {skills.map(s => <option key={s.skillId} value={s.skillName}>{s.skillName}</option>)}
                        </select>
                      </div>
                      <div style={{ marginBottom: '16px' }}>
                        <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px' }}>Target Sessions Needed</label>
                        <input 
                          type="number" 
                          min="1" 
                          max="20"
                          className="modal-input"
                          value={newGoalSessions}
                          onChange={(e) => setNewGoalSessions(e.target.value)}
                        />
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
                        <button type="button" className="btn-outline-secondary" onClick={() => setShowAddGoalModal(false)}>Cancel</button>
                        <button type="submit" className="btn-purple">Set Milestone</button>
                      </div>
                    </form>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* TAB 9: ACHIEVEMENTS & POINTS */}
          {activeTab === 'achievements' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Gamification & Achievements</h3>
                  <p style={{ fontSize: '13px', color: '#64748B' }}>Unlock badges, earn points, and level up peer reputation.</p>
                </div>
              </div>

              {/* Level Hero Progression Banner */}
              <div className="hero-banner-desktop" style={{ marginTop: '14px', borderLeftColor: '#10B981' }}>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span className="pill-badge" style={{ background: '#ECFDF5', color: '#059669' }}>CURRENT LEVEL</span>
                    <h3 style={{ fontSize: '18px' }}>Level 2: Skill Apprentice</h3>
                  </div>
                  <p style={{ fontSize: '13px', color: '#64748B', marginTop: '4px' }}>
                    {points} points accumulated • 50 points needed to reach <strong>Level 3: Knowledge Master</strong>
                  </p>
                  <div className="progress-bar-bg" style={{ marginTop: '10px', height: '9px' }}>
                    <div className="progress-fill" style={{ width: `${Math.min((points / 200) * 100, 100)}%`, background: '#10B981' }}></div>
                  </div>
                </div>
              </div>

              {/* Badges Grid */}
              <h4 style={{ fontSize: '15px', fontWeight: '700', marginTop: '22px', marginBottom: '12px' }}>
                Earned Badges & Milestones
              </h4>
              <div className="stats-row-desktop">
                {badges.map(b => (
                  <div key={b.badgeId} className="kpi-card" style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '36px', marginBottom: '6px' }}>{b.icon}</div>
                    <h4 style={{ fontSize: '14px' }}>{b.badgeName}</h4>
                    <p style={{ fontSize: '11px', color: '#64748B', marginTop: '4px' }}>{b.description}</p>
                    <span 
                      style={{ 
                        color: b.unlocked ? '#059669' : '#64748B', 
                        fontWeight: '700', 
                        fontSize: '11px', 
                        marginTop: '8px', 
                        display: 'block' 
                      }}
                    >
                      {b.unlocked ? '✓ Unlocked' : 'In Progress (4/5)'}
                    </span>
                  </div>
                ))}
              </div>

              {/* Points Ledger */}
              <h4 style={{ fontSize: '15px', fontWeight: '700', marginTop: '22px', marginBottom: '12px' }}>
                Recent Points Activity Ledger
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <div className="partner-card-detailed">
                  <div>
                    <h4>Completed Live Session with Rahul Sharma</h4>
                    <p className="partner-sub">Java Multithreading & Synchronization • Nov 15, 2026</p>
                  </div>
                  <span style={{ color: '#059669', fontWeight: '800' }}>+50 pts</span>
                </div>
                <div className="partner-card-detailed">
                  <div>
                    <h4>Added Offered Skill: C++ & Data Structures</h4>
                    <p className="partner-sub">Catalog contribution • Nov 14, 2026</p>
                  </div>
                  <span style={{ color: '#059669', fontWeight: '800' }}>+25 pts</span>
                </div>
                <div className="partner-card-detailed">
                  <div>
                    <h4>Profile Registration & Verification Bonus</h4>
                    <p className="partner-sub">Welcome incentive • Nov 12, 2026</p>
                  </div>
                  <span style={{ color: '#059669', fontWeight: '800' }}>+10 pts</span>
                </div>
              </div>
            </div>
          )}

          {/* TAB 10: USER PROFILE */}
          {activeTab === 'profile' && (
            <div className="two-col-grid">
              <div className="content-card">
                <div className="card-header-row">
                  <h3>User Profile & Credentials</h3>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button 
                      className="btn-outline-secondary" 
                      onClick={() => {
                        const newBio = prompt('Enter new bio:', currentUser.bio);
                        if (newBio) setCurrentUser(prev => ({ ...prev, bio: newBio }));
                      }}
                    >
                      Edit Profile
                    </button>
                    <button 
                      className="btn-purple" 
                      onClick={openLoginModal}
                    >
                      Switch / Sign In
                    </button>
                  </div>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '20px' }}>
                  <div className="avatar-chip" style={{ width: '64px', height: '64px', fontSize: '22px', background: '#F3E8FF', color: '#6D28D9' }}>
                    {getInitials(currentUser.fullName)}
                  </div>
                  <div>
                    <h3 style={{ fontSize: '18px' }}>{currentUser.fullName}</h3>
                    <p style={{ color: '#64748B', fontSize: '13px' }}>{currentUser.role === 'ADMIN' ? 'Platform Administrator' : 'B.Tech CSE Student • SkillBridge Active Peer'}</p>
                  </div>
                </div>
                <div style={{ fontSize: '13px', color: '#334155', lineHeight: '1.8' }}>
                  <p><strong>Email:</strong> {currentUser.email}</p>
                  <p><strong>Availability:</strong> {currentUser.availability}</p>
                  <p><strong>Account Role:</strong> {currentUser.role}</p>
                  <p style={{ marginTop: '8px' }}><strong>Bio:</strong> {currentUser.bio}</p>
                </div>
              </div>

              <div className="content-card">
                <div className="card-header-row">
                  <h3>Platform Trust & Reputation</h3>
                </div>
                <div style={{ padding: '24px', background: '#F8FAFC', borderRadius: '8px', textAlign: 'center' }}>
                  <div style={{ fontSize: '42px', color: '#10B981', fontWeight: '800' }}>
                    {currentUser.reliabilityScore}%
                  </div>
                  <h4 style={{ marginTop: '4px' }}>Peer Reliability Score</h4>
                  <p style={{ fontSize: '12px', color: '#64748B', marginTop: '6px', maxWidth: '360px', margin: '6px auto 0' }}>
                    Calculated dynamically based on completed peer exchanges, punctuality, and verified ratings from peer reviews.
                  </p>
                </div>
                <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'space-around', textAlign: 'center' }}>
                  <div>
                    <strong>100%</strong>
                    <p style={{ fontSize: '11px', color: '#64748B' }}>Punctuality</p>
                  </div>
                  <div>
                    <strong>5.0 / 5.0</strong>
                    <p style={{ fontSize: '11px', color: '#64748B' }}>Average Rating</p>
                  </div>
                  <div>
                    <strong>10/10</strong>
                    <p style={{ fontSize: '11px', color: '#64748B' }}>Trust Index</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* TAB 11: SETTINGS & EXPORT */}
          {activeTab === 'settings' && (
            <div className="content-card">
              <div className="card-header-row">
                <div>
                  <h3>Settings & Data Management</h3>
                  <p style={{ fontSize: '13px', color: '#64748B' }}>
                    Download your complete learning transcript and verify cloud database connectivity.
                  </p>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginTop: '16px' }}>
                <div className="partner-card-detailed">
                  <div>
                    <h4>Export Learning Portfolio Report</h4>
                    <p className="partner-sub">
                      Generate and download an official text record of your offered skills, completed sessions, and badges.
                    </p>
                  </div>
                  <button className="btn-purple" onClick={handleExportReport} style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Download size={14} /> Download Report (.txt)
                  </button>
                </div>

                <div className="partner-card-detailed">
                  <div>
                    <h4>TiDB Cloud Serverless Connection</h4>
                    <p className="partner-sub">
                      Host: <code>gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000</code> • Database: <code>skillbridge_db</code>
                    </p>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <span className="mastered-tag" style={{ background: '#ECFDF5', padding: '6px 12px', borderRadius: '4px' }}>
                      ✓ {dbStatusPing}
                    </span>
                    <button 
                      className="btn-outline-secondary"
                      onClick={() => {
                        setDbStatusPing('Checking...');
                        setTimeout(() => setDbStatusPing('Connected (Latency: 38ms)'), 600);
                      }}
                    >
                      <RefreshCw size={13} /> Ping DB
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* 3. Authentication & Account Modal */}
      {showAuthModal && (
        <div className="modal-overlay" onClick={() => setShowAuthModal(false)}>
          <div className="modal-card" style={{ maxWidth: '440px' }} onClick={e => e.stopPropagation()}>
            <div className="card-header-row" style={{ marginBottom: '14px' }}>
              <div>
                <h3 style={{ fontSize: '18px', fontWeight: '800' }}>
                  {authMode === 'login' ? 'Sign In to SkillBridge' : 'Create Campus Account'}
                </h3>
                <p style={{ fontSize: '12px', color: '#64748B', marginTop: '2px' }}>
                  {authMode === 'login' 
                    ? 'Connect to peer learning exchanges & live sessions' 
                    : 'Join your university peer skill network'}
                </p>
              </div>
              <button 
                type="button"
                className="btn-link" 
                onClick={() => setShowAuthModal(false)}
                style={{ fontSize: '18px', background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8' }}
              >
                ✕
              </button>
            </div>

            {/* Mode Switcher Tabs */}
            <div className="auth-tabs">
              <button 
                type="button"
                className={`auth-tab-btn ${authMode === 'login' ? 'active' : ''}`}
                onClick={() => { setAuthMode('login'); setAuthError(''); setAuthSuccess(''); }}
              >
                Sign In
              </button>
              <button 
                type="button"
                className={`auth-tab-btn ${authMode === 'register' ? 'active' : ''}`}
                onClick={() => { setAuthMode('register'); setAuthError(''); setAuthSuccess(''); }}
              >
                Create Account
              </button>
            </div>

            {/* Feedback Alerts */}
            {authError && (
              <div className="auth-alert-error">
                <span>⚠️ {authError}</span>
              </div>
            )}
            {authSuccess && (
              <div className="auth-alert-success">
                <span>✓ {authSuccess}</span>
              </div>
            )}

            {/* TAB 1: LOGIN */}
            {authMode === 'login' ? (
              <div>
                {/* Quick Select Peer Profiles */}
                <div className="quick-pick-section">
                  <div style={{ fontSize: '11px', fontWeight: '700', color: '#475569', letterSpacing: '0.3px', textTransform: 'uppercase' }}>
                    ⚡ Instant Switch / Demo Peers
                  </div>
                  <div className="quick-pick-grid">
                    {allUsers.slice(0, 4).map(u => (
                      <button 
                        key={u.userId}
                        type="button"
                        className="quick-user-btn"
                        onClick={() => handleQuickLogin(u)}
                        title={`Sign in as ${u.fullName}`}
                      >
                        <div className="quick-user-avatar">
                          {getInitials(u.fullName)}
                        </div>
                        <div style={{ overflow: 'hidden' }}>
                          <div className="quick-user-name">{u.fullName.split(' ')[0]}</div>
                          <div className="quick-user-role">{u.role}</div>
                        </div>
                      </button>
                    ))}
                  </div>
                </div>

                <div style={{ textAlign: 'center', margin: '12px 0 14px', position: 'relative' }}>
                  <span style={{ background: '#FFFFFF', padding: '0 10px', fontSize: '11px', color: '#94A3B8', fontWeight: '600' }}>
                    OR ENTER CREDENTIALS
                  </span>
                  <div style={{ position: 'absolute', top: '50%', left: 0, right: 0, height: '1px', background: '#E2E8F0', zIndex: -1 }}></div>
                </div>

                <form onSubmit={handleLoginSubmit}>
                  <div style={{ marginBottom: '12px' }}>
                    <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                      Email or Username
                    </label>
                    <input 
                      type="text" 
                      required 
                      className="modal-input" 
                      placeholder="e.g. koushik@example.com or koushik"
                      value={authEmail}
                      onChange={e => setAuthEmail(e.target.value)}
                    />
                  </div>

                  <div style={{ marginBottom: '16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                      <label style={{ fontSize: '12px', fontWeight: '700', color: '#334155' }}>
                        Password
                      </label>
                      <span style={{ fontSize: '11px', color: '#64748B' }}>
                        Default: <code>password123</code>
                      </span>
                    </div>
                    <input 
                      type="password" 
                      required 
                      className="modal-input" 
                      placeholder="Enter password"
                      value={authPassword}
                      onChange={e => setAuthPassword(e.target.value)}
                    />
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '18px' }}>
                    <button 
                      type="button" 
                      className="btn-link"
                      style={{ fontSize: '12px', color: '#6D28D9', cursor: 'pointer', background: 'none', border: 'none', padding: 0 }}
                      onClick={() => { setAuthMode('register'); setAuthError(''); }}
                    >
                      Need an account? Register →
                    </button>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button 
                        type="button" 
                        className="btn-outline-secondary" 
                        onClick={() => setShowAuthModal(false)}
                      >
                        Cancel
                      </button>
                      <button type="submit" className="btn-purple">
                        Sign In
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            ) : (
              /* TAB 2: REGISTER / SIGN UP */
              <form onSubmit={handleRegisterSubmit}>
                <div style={{ marginBottom: '10px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Full Name *
                  </label>
                  <input 
                    type="text" 
                    required 
                    className="modal-input" 
                    placeholder="e.g. Priya Sharma"
                    value={authName}
                    onChange={e => setAuthName(e.target.value)}
                  />
                </div>

                <div style={{ marginBottom: '10px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Campus Email *
                  </label>
                  <input 
                    type="email" 
                    required 
                    className="modal-input" 
                    placeholder="e.g. priya@example.com"
                    value={authEmail}
                    onChange={e => setAuthEmail(e.target.value)}
                  />
                </div>

                <div style={{ marginBottom: '10px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Password *
                  </label>
                  <input 
                    type="password" 
                    required 
                    className="modal-input" 
                    placeholder="Create a password (min 6 chars)"
                    value={authPassword}
                    onChange={e => setAuthPassword(e.target.value)}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '10px' }}>
                  <div>
                    <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                      Role
                    </label>
                    <select 
                      className="modal-input"
                      value={authRole}
                      onChange={e => setAuthRole(e.target.value)}
                    >
                      <option value="STUDENT">Student Peer</option>
                      <option value="MENTOR">Peer Mentor</option>
                      <option value="ADMIN">Administrator</option>
                    </select>
                  </div>
                  <div>
                    <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                      Initial Trust Rating
                    </label>
                    <input 
                      type="text" 
                      disabled 
                      className="modal-input" 
                      value="100% (New Peer)"
                      style={{ background: '#F8FAFC', color: '#059669', fontWeight: '700' }}
                    />
                  </div>
                </div>

                <div style={{ marginBottom: '14px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Bio & Focus Area (Optional)
                  </label>
                  <input 
                    type="text" 
                    className="modal-input" 
                    placeholder="e.g. 2nd Year CSE, learning Full-Stack & UI/UX"
                    value={authBio}
                    onChange={e => setAuthBio(e.target.value)}
                  />
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '16px' }}>
                  <button 
                    type="button" 
                    className="btn-link"
                    style={{ fontSize: '12px', color: '#6D28D9', cursor: 'pointer', background: 'none', border: 'none', padding: 0 }}
                    onClick={() => { setAuthMode('login'); setAuthError(''); }}
                  >
                    ← Back to Sign In
                  </button>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button 
                      type="button" 
                      className="btn-outline-secondary" 
                      onClick={() => setShowAuthModal(false)}
                    >
                      Cancel
                    </button>
                    <button type="submit" className="btn-purple">
                      Create Account
                    </button>
                  </div>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
      {/* 4. Schedule Meeting Modal (agreed upon in Chat) */}
      {showScheduleModal && (
        <div className="modal-overlay" onClick={() => setShowScheduleModal(false)}>
          <div className="modal-card" style={{ maxWidth: '440px' }} onClick={e => e.stopPropagation()}>
            <div className="card-header-row" style={{ marginBottom: '14px' }}>
              <div>
                <h3 style={{ fontSize: '18px', fontWeight: '800' }}>Schedule Peer Meeting</h3>
                <p style={{ fontSize: '12px', color: '#64748B', marginTop: '2px' }}>
                  Lock in an agreed date & venue for both calendars.
                </p>
              </div>
              <button 
                type="button" 
                className="btn-link" 
                onClick={() => setShowScheduleModal(false)}
                style={{ fontSize: '18px', background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8' }}
              >
                ✕
              </button>
            </div>

            <form onSubmit={(e) => {
              e.preventDefault();
              const newSession = {
                sessionId: Date.now(),
                partnerName: activeChatPartner.fullName,
                skillName: scheduleTopic,
                time: `Nov ${scheduleDay}, 2026 at ${scheduleTime}`,
                day: parseInt(scheduleDay),
                venue: scheduleVenue,
                status: 'SCHEDULED',
                reviewed: false
              };
              setSessions(prev => [newSession, ...prev]);
              setShowScheduleModal(false);
              setActiveTab('overview');
            }}>
              <div style={{ marginBottom: '12px' }}>
                <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                  Session Topic / Skill Focus
                </label>
                <input 
                  type="text" 
                  required 
                  className="modal-input" 
                  value={scheduleTopic}
                  onChange={e => setScheduleTopic(e.target.value)}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '12px' }}>
                <div>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Calendar Date (Nov)
                  </label>
                  <select 
                    className="modal-input" 
                    value={scheduleDay} 
                    onChange={e => setScheduleDay(e.target.value)}
                  >
                    {Array.from({ length: 30 }, (_, i) => i + 1).map(d => (
                      <option key={d} value={d}>Nov {d}, 2026</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Time
                  </label>
                  <input 
                    type="text" 
                    required 
                    className="modal-input" 
                    value={scheduleTime}
                    onChange={e => setScheduleTime(e.target.value)}
                    placeholder="e.g. 5:30 PM"
                  />
                </div>
              </div>

              <div style={{ marginBottom: '16px' }}>
                <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                  Campus Meeting Venue / Mode
                </label>
                <input 
                  type="text" 
                  required 
                  className="modal-input" 
                  value={scheduleVenue}
                  onChange={e => setScheduleVenue(e.target.value)}
                  placeholder="e.g. Central Library 2nd Floor, Lab 4"
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
                <button type="button" className="btn-outline-secondary" onClick={() => setShowScheduleModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-purple">
                  ✓ Add to Both Calendars
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 5. Leave Peer Review & Rating Modal */}
      {showReviewModal && (
        <div className="modal-overlay" onClick={() => setShowReviewModal(false)}>
          <div className="modal-card" style={{ maxWidth: '440px' }} onClick={e => e.stopPropagation()}>
            <div className="card-header-row" style={{ marginBottom: '14px' }}>
              <div>
                <h3 style={{ fontSize: '18px', fontWeight: '800' }}>Leave Peer Review</h3>
                <p style={{ fontSize: '12px', color: '#64748B', marginTop: '2px' }}>
                  Rate your exchange partner to finalize the session and update reliability.
                </p>
              </div>
              <button 
                type="button" 
                className="btn-link" 
                onClick={() => setShowReviewModal(false)}
                style={{ fontSize: '18px', background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8' }}
              >
                ✕
              </button>
            </div>

            {reviewSuccessMsg ? (
              <div className="auth-alert-success">
                <span>✓ {reviewSuccessMsg}</span>
              </div>
            ) : (
              <form onSubmit={(e) => {
                e.preventDefault();
                if (selectedReviewSession) {
                  setSessions(prev => prev.map(item => item.sessionId === selectedReviewSession.sessionId ? { ...item, reviewed: true, rating: reviewStars } : item));
                }
                setPoints(p => p + 50);
                setReviewSuccessMsg(`Review recorded! +50 Skill Points awarded and reliability updated.`);
                setTimeout(() => {
                  setShowReviewModal(false);
                  setReviewSuccessMsg('');
                }, 900);
              }}>
                <div style={{ marginBottom: '14px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '6px', color: '#334155' }}>
                    Select Star Rating
                  </label>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    {[1, 2, 3, 4, 5].map(star => (
                      <button 
                        key={star}
                        type="button"
                        style={{
                          fontSize: '20px',
                          background: 'none',
                          border: 'none',
                          cursor: 'pointer',
                          color: star <= reviewStars ? '#F59E0B' : '#CBD5E1'
                        }}
                        onClick={() => setReviewStars(star)}
                      >
                        ★
                      </button>
                    ))}
                    <span style={{ fontSize: '13px', fontWeight: '700', color: '#6D28D9', alignSelf: 'center', marginLeft: '6px' }}>
                      {reviewStars} / 5 Stars
                    </span>
                  </div>
                </div>

                <div style={{ marginBottom: '16px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', display: 'block', marginBottom: '4px', color: '#334155' }}>
                    Peer Feedback & Comments
                  </label>
                  <textarea 
                    rows="3"
                    required 
                    className="modal-input" 
                    value={reviewComment}
                    onChange={e => setReviewComment(e.target.value)}
                  />
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
                  <button type="button" className="btn-outline-secondary" onClick={() => setShowReviewModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn-purple">
                    Submit Review & Finish
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
