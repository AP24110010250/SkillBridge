import React, { useState, useEffect } from 'react';
import { 
  Compass, 
  Users, 
  BookOpen, 
  Award, 
  Calendar, 
  MessageSquare, 
  CheckCircle, 
  Star, 
  ShieldCheck, 
  TrendingUp, 
  Search,
  Sparkles,
  ArrowRight
} from 'lucide-react';
import { skillsApi, matchesApi, gamificationApi, usersApi } from './services/api';

export default function App() {
  const [activeTab, setActiveTab] = useState('overview');
  const [user, setUser] = useState({ fullName: 'Koushik Chava', role: 'STUDENT', email: 'koushik@example.com' });
  const [skills, setSkills] = useState([]);
  const [matches, setMatches] = useState([]);
  const [gamification, setGamification] = useState({ points: 150, level: 'Level 2: Skill Apprentice', badges: [], goals: [] });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [skillsRes, matchesRes, gamifyRes] = await Promise.allSettled([
          skillsApi.getAll(),
          matchesApi.getMatches(1),
          gamificationApi.getStatus(1)
        ]);

        if (skillsRes.status === 'fulfilled') setSkills(skillsRes.value.data || []);
        if (matchesRes.status === 'fulfilled') setMatches(matchesRes.value.data || []);
        if (gamifyRes.status === 'fulfilled') setGamification(gamifyRes.value.data || {});
      } catch (err) {
        console.error('API load error:', err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  return (
    <div className="app-container">
      {/* 1. Left Navigation Sidebar */}
      <aside className="sidebar">
        <div className="brand-section">
          <div className="brand-logo">S</div>
          <div className="brand-text">
            <h1>SkillBridge</h1>
            <p>Peer Learning Platform</p>
          </div>
        </div>

        <nav className="nav-menu">
          <button 
            className={`nav-item ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            <Compass size={18} /> Overview
          </button>
          <button 
            className={`nav-item ${activeTab === 'discover' ? 'active' : ''}`}
            onClick={() => setActiveTab('discover')}
          >
            <BookOpen size={18} /> Discover Skills
          </button>
          <button 
            className={`nav-item ${activeTab === 'matches' ? 'active' : ''}`}
            onClick={() => setActiveTab('matches')}
          >
            <Sparkles size={18} /> Smart Matches
          </button>
          <button 
            className={`nav-item ${activeTab === 'achievements' ? 'active' : ''}`}
            onClick={() => setActiveTab('achievements')}
          >
            <Award size={18} /> Achievements
          </button>
        </nav>

        <div className="sidebar-footer">
          <div className="user-card">
            <div className="user-avatar">{user.fullName[0]}</div>
            <div className="user-info">
              <h4>{user.fullName}</h4>
              <p>{user.role}</p>
            </div>
          </div>
        </div>
      </aside>

      {/* 2. Main Content Area */}
      <main className="main-wrapper">
        <header className="top-navbar">
          <div className="search-bar">
            <Search size={16} color="#64748B" />
            <input type="text" placeholder="Search skills, topics, or mentors..." />
          </div>

          <div className="top-actions">
            <div className="status-badge">
              <span className="pulse-dot"></span>
              <span>TiDB Cloud Connected</span>
            </div>
          </div>
        </header>

        <div className="content-body">
          {/* TAB 1: OVERVIEW */}
          {activeTab === 'overview' && (
            <>
              {/* Udemy Hero Welcome */}
              <div className="welcome-hero">
                <div className="welcome-text">
                  <h2>Welcome back, {user.fullName}! 👋</h2>
                  <p>
                    Ready for your next skill exchange? Connect with university peers, share technical knowledge, and track your milestone progress.
                  </p>
                  <div className="hero-btns">
                    <button className="btn-white" onClick={() => setActiveTab('discover')}>
                      Explore Catalog →
                    </button>
                    <button className="btn-glass" onClick={() => setActiveTab('matches')}>
                      View Smart Matches
                    </button>
                  </div>
                </div>
              </div>

              {/* 4 Stats Cards */}
              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-icon" style={{ background: '#F3E8FF', color: '#6D28D9' }}>
                    <Star size={22} />
                  </div>
                  <div className="stat-info">
                    <h3>{gamification.points || 150}</h3>
                    <p>Skill Points</p>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon" style={{ background: '#ECFDF5', color: '#10B981' }}>
                    <CheckCircle size={22} />
                  </div>
                  <div className="stat-info">
                    <h3>5</h3>
                    <p>Sessions Completed</p>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon" style={{ background: '#EFF6FF', color: '#2563EB' }}>
                    <ShieldCheck size={22} />
                  </div>
                  <div className="stat-info">
                    <h3>98%</h3>
                    <p>Trust Reliability</p>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon" style={{ background: '#FFFBEB', color: '#D97706' }}>
                    <TrendingUp size={22} />
                  </div>
                  <div className="stat-info">
                    <h3>{matches.length || 3}</h3>
                    <p>Mutual Matches</p>
                  </div>
                </div>
              </div>

              {/* 2-Column Split */}
              <div className="dashboard-grid">
                {/* Left: Recommended Partners */}
                <div className="section-card">
                  <div className="section-head">
                    <h3>Recommended Exchange Partners</h3>
                    <button className="view-all-btn" onClick={() => setActiveTab('matches')}>
                      View All →
                    </button>
                  </div>

                  {matches.length > 0 ? (
                    matches.slice(0, 3).map((m, idx) => (
                      <div key={idx} className="partner-card">
                        <div className="partner-meta">
                          <div className="partner-avatar">
                            {m.partnerName ? m.partnerName[0] : 'P'}
                          </div>
                          <div className="partner-details">
                            <h4>{m.partnerName || 'Peer Learner'}</h4>
                            <p>Teaches: {m.offeredSkillName} • Wants: {m.wantedSkillName}</p>
                          </div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <span className="match-badge">{Math.round(m.compatibilityScore)}% Match</span>
                          <button className="request-btn">Request</button>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div style={{ textAlign: 'center', padding: '30px', color: '#64748B' }}>
                      <p>Finding peers compatible with your skill profile...</p>
                    </div>
                  )}
                </div>

                {/* Right: Learning Goals & Badges */}
                <div className="section-card">
                  <div className="section-head">
                    <h3>Current Learning Milestones</h3>
                    <button className="view-all-btn" onClick={() => setActiveTab('achievements')}>
                      Milestones →
                    </button>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                    <div style={{ border: '1px solid #E2E8F0', padding: '14px', borderRadius: '8px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                        <span style={{ fontWeight: '700', fontSize: '13px' }}>Master React & UI Design</span>
                        <span style={{ color: '#6D28D9', fontWeight: '700', fontSize: '12px' }}>75%</span>
                      </div>
                      <div style={{ height: '6px', background: '#F1F5F9', borderRadius: '4px', overflow: 'hidden' }}>
                        <div style={{ width: '75%', height: '100%', background: '#6D28D9' }}></div>
                      </div>
                    </div>

                    <div style={{ border: '1px solid #E2E8F0', padding: '14px', borderRadius: '8px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                        <span style={{ fontWeight: '700', fontSize: '13px' }}>Complete 5 Java Peer Sessions</span>
                        <span style={{ color: '#10B981', fontWeight: '700', fontSize: '12px' }}>100%</span>
                      </div>
                      <div style={{ height: '6px', background: '#F1F5F9', borderRadius: '4px', overflow: 'hidden' }}>
                        <div style={{ width: '100%', height: '100%', background: '#10B981' }}></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}

          {/* TAB 2: DISCOVER SKILLS */}
          {activeTab === 'discover' && (
            <div>
              <div style={{ marginBottom: '24px' }}>
                <h2 style={{ fontSize: '22px', fontWeight: '800' }}>Explore Skills Catalog</h2>
                <p style={{ color: '#64748B', fontSize: '14px' }}>Find university peers teaching programming, creative design, and science.</p>
              </div>

              <div className="skills-grid">
                {skills.length > 0 ? (
                  skills.map((s) => (
                    <div key={s.skillId} className="skill-card">
                      <span className="skill-tag">Category #{s.categoryId}</span>
                      <h4 style={{ marginTop: '12px' }}>{s.skillName}</h4>
                      <p>{s.description || 'Comprehensive peer-to-peer interactive topic.'}</p>
                      <button className="request-btn" style={{ width: '100%' }}>
                        Find Mentors →
                      </button>
                    </div>
                  ))
                ) : (
                  <p>Loading catalog skills from TiDB Cloud...</p>
                )}
              </div>
            </div>
          )}

          {/* TAB 3: SMART MATCHES */}
          {activeTab === 'matches' && (
            <div>
              <div style={{ marginBottom: '24px' }}>
                <h2 style={{ fontSize: '22px', fontWeight: '800' }}>Intelligent Peer Matches</h2>
                <p style={{ color: '#64748B', fontSize: '14px' }}>Calculated by compatibility between what you offer to teach and what you want to learn.</p>
              </div>

              {matches.map((m, idx) => (
                <div key={idx} className="partner-card" style={{ padding: '20px' }}>
                  <div className="partner-meta">
                    <div className="partner-avatar" style={{ width: '48px', height: '48px', fontSize: '18px' }}>
                      {m.partnerName ? m.partnerName[0] : 'P'}
                    </div>
                    <div className="partner-details">
                      <h4>{m.partnerName || 'Peer Student'}</h4>
                      <p style={{ marginTop: '4px' }}>
                        Can teach you: <strong>{m.offeredSkillName}</strong> | Wants to learn: <strong>{m.wantedSkillName}</strong>
                      </p>
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                    <div style={{ textAlign: 'right' }}>
                      <span className="match-badge" style={{ fontSize: '13px' }}>
                        {Math.round(m.compatibilityScore)}% Compatibility
                      </span>
                      <p style={{ fontSize: '11px', color: '#64748B', marginTop: '4px' }}>
                        {m.reciprocal ? '⚡ Mutual Reciprocal Match' : 'One-way Match'}
                      </p>
                    </div>
                    <button className="request-btn" style={{ padding: '10px 18px' }}>
                      Send Exchange Request
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* TAB 4: ACHIEVEMENTS */}
          {activeTab === 'achievements' && (
            <div>
              <div style={{ marginBottom: '24px' }}>
                <h2 style={{ fontSize: '22px', fontWeight: '800' }}>Achievements & Platform Badges</h2>
                <p style={{ color: '#64748B', fontSize: '14px' }}>Earn badges and climb tier ranks by completing verified learning exchanges.</p>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: '16px' }}>
                <div className="section-card" style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '36px', marginBottom: '8px' }}>🚀</div>
                  <h4>First Step</h4>
                  <p style={{ fontSize: '12px', color: '#64748B', marginTop: '4px' }}>Registered profile on SkillBridge</p>
                  <span style={{ color: '#10B981', fontWeight: '700', fontSize: '12px', display: 'block', marginTop: '10px' }}>✓ Unlocked</span>
                </div>

                <div className="section-card" style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '36px', marginBottom: '8px' }}>🎓</div>
                  <h4>Skill Contributor</h4>
                  <p style={{ fontSize: '12px', color: '#64748B', marginTop: '4px' }}>Offered first skill to university peers</p>
                  <span style={{ color: '#10B981', fontWeight: '700', fontSize: '12px', display: 'block', marginTop: '10px' }}>✓ Unlocked</span>
                </div>

                <div className="section-card" style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '36px', marginBottom: '8px' }}>⭐</div>
                  <h4>Master Mentor</h4>
                  <p style={{ fontSize: '12px', color: '#64748B', marginTop: '4px' }}>Maintain 95%+ reliability over 5 sessions</p>
                  <span style={{ color: '#6D28D9', fontWeight: '700', fontSize: '12px', display: 'block', marginTop: '10px' }}>In Progress (4/5)</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
