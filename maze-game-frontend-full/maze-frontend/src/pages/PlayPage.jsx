import { useState, useEffect } from 'react';
import ControlPad from '../components/ControlPad';
import MazeGrid from '../components/MazeGrid';
import StatCard from '../components/StatCard';
import { useMazeGame } from '../hooks/useMazeGame';

export default function PlayPage() {
  const [playerNameInput, setPlayerNameInput] = useState('');
  const [playerName, setPlayerName] = useState('');

  const {
    rows,
    cols,
    maze,
    player,
    goal,
    moves,
    visited,
    solutionSet,
    showSolution,
    won,
    message,
    setRows,
    setCols,
    regenerateMaze,
    move
  } = useMazeGame(playerName);

  useEffect(() => {
    const onKeyDown = (event) => {
      const mapping = {
        w: 'UP',
        W: 'UP',
        s: 'DOWN',
        S: 'DOWN',
        a: 'LEFT',
        A: 'LEFT',
        d: 'RIGHT',
        D: 'RIGHT'
      };
      const direction = mapping[event.key];
      if (direction) move(direction);
    };

    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [move]);

  if (!playerName) {
    return (
      <section className="hero-grid">
        <div className="panel" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '4rem 2rem' }}>
          <h2>Who is exploring the maze today?</h2>
          <form onSubmit={(e) => { e.preventDefault(); if (playerNameInput.trim()) setPlayerName(playerNameInput.trim()); }} style={{ marginTop: '2rem' }}>
            <input 
              type="text" 
              placeholder="Enter your name..." 
              value={playerNameInput}
              onChange={(e) => setPlayerNameInput(e.target.value)}
              style={{ fontSize: '1.2rem', padding: '0.8rem', marginRight: '1rem', borderRadius: '4px', border: '1px solid #ccc' }}
              required
            />
            <button type="submit" className="primary-btn">Start Game</button>
          </form>
        </div>
      </section>
    );
  }

  return (
    <section className="game-layout">
      <aside className="sidebar stack">
        <div className="panel">
          <h2>Maze controls</h2>
          <div className="form-grid">
            <label>
              Maze Size
              <input type="number" min="11" step="2" value={rows} onChange={(e) => { setRows(Number(e.target.value)); setCols(Number(e.target.value)); }} />
            </label>
          </div>
          <div className="cta-row wrap">
            <button className="primary-btn" onClick={regenerateMaze}>Generate</button>
          </div>
        </div>

        <div className="stats-grid">
          <StatCard label="Moves" value={moves} accent="blue" />
          <StatCard label="State" value={won ? 'Won' : 'Running'} accent={won ? 'green' : 'blue'} />
          {won && <StatCard label="BFS Moves" value={solutionSet.size > 0 ? solutionSet.size - 1 : 0} accent="purple" />}
        </div>

        <div className="panel">
          <h3>Controls</h3>
          <ControlPad onMove={move} />
        </div>
      </aside>

      <div className="panel maze-panel">
        <div className="maze-header">
          <div>
            <h2>Playable maze</h2>
            <p>Blue is player, green is goal, purple is shortest path.</p>
          </div>
        </div>
        <MazeGrid
          maze={maze}
          player={player}
          goal={goal}
          visited={visited}
          solutionSet={solutionSet}
          showSolution={showSolution}
        />
      </div>
    </section>
  );
}
