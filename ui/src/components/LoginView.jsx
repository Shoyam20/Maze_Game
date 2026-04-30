import React, { useState } from 'react';

const LoginView = ({ onLogin }) => {
  const [name, setName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (name.trim()) onLogin(name.trim());
  };

  return (
    <div style={{ 
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      justifyContent: 'center', 
      height: '100vh',
      background: 'radial-gradient(circle at 50% 50%, #ffffff 0%, #f1f5f9 100%)'
    }}>
      <div className="stats-card" style={{ width: '400px', padding: '3rem', textAlign: 'center', boxShadow: 'var(--shadow-soft)' }}>
        <h1 style={{ marginBottom: '0.5rem', color: 'var(--accent-primary)' }}>Welcome Runner</h1>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Enter your name to start the challenge</p>
        
        <form onSubmit={handleSubmit} className="control-group">
          <input 
            type="text" 
            placeholder="Player Name" 
            value={name} 
            onChange={(e) => setName(e.target.value)}
            style={{ textAlign: 'center', marginBottom: '1rem' }}
            autoFocus
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
            Enter Maze
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginView;
