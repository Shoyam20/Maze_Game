import { useState, useEffect } from 'react';
import { getLeaderboard } from '../services/leaderboard';
import { Link } from 'react-router-dom';

export default function LeaderboardPage() {
  const [scores, setScores] = useState([]);

  useEffect(() => {
    setScores(getLeaderboard());
  }, []);

  return (
    <section className="hero-grid">
      <div className="panel" style={{ gridColumn: '1 / -1' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>Top Maze Explorers</h2>
          <Link className="primary-btn" to="/play">Play Maze</Link>
        </div>
        
        <div style={{ marginTop: '2rem', overflowX: 'auto' }}>
          {scores.length === 0 ? (
            <p>No scores yet! Be the first to play and get on the leaderboard.</p>
          ) : (
            <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid rgba(255,255,255,0.1)' }}>
                  <th style={{ padding: '1rem' }}>Rank</th>
                  <th style={{ padding: '1rem' }}>Name</th>
                  <th style={{ padding: '1rem' }}>Score</th>
                  <th style={{ padding: '1rem' }}>Maze Size</th>
                  <th style={{ padding: '1rem' }}>Efficiency</th>
                  <th style={{ padding: '1rem' }}>Date</th>
                </tr>
              </thead>
              <tbody>
                {scores.map((entry, index) => (
                  <tr key={entry.id}>
                    <td style={{ padding: '1rem' }}>#{index + 1}</td>
                    <td style={{ padding: '1rem', fontWeight: 'bold' }}>{entry.name}</td>
                    <td style={{ padding: '1rem', color: '#b45309', fontWeight: 'bold' }}>{entry.score} pts</td>
                    <td style={{ padding: '1rem' }}>{entry.size}x{entry.size}</td>
                    <td style={{ padding: '1rem' }}>{Math.round((entry.bfsMoves / entry.playerMoves) * 100)}%</td>
                    <td style={{ padding: '1rem', color: '#666' }}>{entry.date}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </section>
  );
}
