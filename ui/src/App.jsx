import React, { useState, useEffect, useCallback, useRef } from 'react';
import MazeCanvas from './components/MazeCanvas';
import LoginView from './components/LoginView';

const API_BASE = 'http://localhost:8080';

function App() {
  // ── UI state ──────────────────────────────────────────────────────────────
  const [playerName,     setPlayerName]     = useState(null);
  const [grid,           setGrid]           = useState([]);
  const [weights,        setWeights]        = useState([]);
  const [playerHistory,  setPlayerHistory]  = useState([{ r: 1, c: 1 }]);
  const [bfsPath,        setBfsPath]        = useState(null);
  const [dijkstraPath,   setDijkstraPath]   = useState(null);
  const [size,           setSize]           = useState(21);
  const [mode,           setMode]           = useState(1);
  const [totalLevels,    setTotalLevels]    = useState(1);
  const [currentLevel,   setCurrentLevel]   = useState(1);
  const [keysCollected,  setKeysCollected]  = useState(0);
  const [totalKeys,      setTotalKeys]      = useState(0);
  const [doorOpen,       setDoorOpen]       = useState(false);
  const [showComparison, setShowComparison] = useState(false);
  const [loading,        setLoading]        = useState(false);
  const [leaderboard,    setLeaderboard]    = useState([]);
  const [lastScore,      setLastScore]      = useState(null); // show score feedback

  // ── Refs hold always-fresh values for closures ────────────────────────────
  const playerHistoryRef  = useRef([{ r: 1, c: 1 }]);
  const gridRef           = useRef([]);
  const weightsRef        = useRef([]);
  const doorOpenRef       = useRef(false);
  const keysCollectedRef  = useRef(0);
  const totalKeysRef      = useRef(0);
  const showComparisonRef = useRef(false);
  const playerNameRef     = useRef(null);
  const modeRef           = useRef(1);
  const sizeRef           = useRef(21);
  const currentLevelRef   = useRef(1);
  const finishedRef       = useRef(false);   // guard: reveal only once per maze

  // Sync refs
  useEffect(() => { playerHistoryRef.current  = playerHistory;  }, [playerHistory]);
  useEffect(() => { gridRef.current           = grid;           }, [grid]);
  useEffect(() => { weightsRef.current        = weights;        }, [weights]);
  useEffect(() => { doorOpenRef.current       = doorOpen;       }, [doorOpen]);
  useEffect(() => { keysCollectedRef.current  = keysCollected;  }, [keysCollected]);
  useEffect(() => { totalKeysRef.current      = totalKeys;      }, [totalKeys]);
  useEffect(() => { showComparisonRef.current = showComparison; }, [showComparison]);
  useEffect(() => { playerNameRef.current     = playerName;     }, [playerName]);
  useEffect(() => { modeRef.current           = mode;           }, [mode]);
  useEffect(() => { sizeRef.current           = size;           }, [size]);
  useEffect(() => { currentLevelRef.current   = currentLevel;   }, [currentLevel]);

  // ── Fetch / generate maze ─────────────────────────────────────────────────
  const fetchMaze = useCallback(async (targetSize, targetMode, targetProb) => {
    setLoading(true);
    setBfsPath(null);
    setDijkstraPath(null);
    setShowComparison(false); showComparisonRef.current = false;
    setKeysCollected(0);      keysCollectedRef.current = 0;
    setDoorOpen(false);       doorOpenRef.current = false;
    setLastScore(null);
    finishedRef.current = false;

    const startPos = [{ r: 1, c: 1 }];
    setPlayerHistory(startPos);
    playerHistoryRef.current = startPos;

    try {
      const resp = await fetch(
        `${API_BASE}/generate?size=${targetSize}&mode=${targetMode}&prob=${targetProb}`
      );
      if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
      const data = await resp.json();

      setGrid(data.grid);         gridRef.current = data.grid;
      setWeights(data.weights);   weightsRef.current = data.weights;

      const k = data.numKeys ?? 0;
      setTotalKeys(k);            totalKeysRef.current = k;
    } catch (err) {
      console.error('fetchMaze error:', err);
    }
    setLoading(false);
  }, []);

  // ── Login ─────────────────────────────────────────────────────────────────
  const handleLogin = async (name) => {
    await fetch(`${API_BASE}/login?name=${encodeURIComponent(name)}`).catch(() => {});
    setPlayerName(name);
    playerNameRef.current = name;
    fetchMaze(21, 1, 0.3);
    fetchLeaderboard();
  };

  // ── Leaderboard ───────────────────────────────────────────────────────────
  const fetchLeaderboard = async () => {
    try {
      const resp = await fetch(`${API_BASE}/leaderboard`);
      if (resp.ok) setLeaderboard(await resp.json());
    } catch (_) {}
  };

  // ── Score computation ─────────────────────────────────────────────────────
  //  Score formula (per level):
  //    efficiency   = bfsSteps / max(playerSteps, 1)    [1.0 = perfect, <1 = worse]
  //    costBonus    = dijkCost / max(playerCost, 1)     [1.0 = matched Dijkstra cost]
  //    levelBonus   = currentLevel * 100
  //    sizeBonus    = mazeSize * 5
  //    rawScore     = round((efficiency * 700 + costBonus * 300 + levelBonus + sizeBonus))
  //    finalScore   = max(10, rawScore)
  const computeScore = (bfsSteps, playerSteps, dijkCost, playerCost, mazeSize, level) => {
    const efficiency = bfsSteps > 0 ? Math.min(1, bfsSteps / Math.max(playerSteps, 1)) : 0;
    const costEff    = dijkCost > 0 ? Math.min(1, dijkCost  / Math.max(playerCost, 1))  : 0;
    const levelBonus = (level - 1) * 100;
    const sizeBonus  = mazeSize * 5;
    return Math.max(10, Math.round(efficiency * 700 + costEff * 300 + levelBonus + sizeBonus));
  };

  // ── Reveal paths after player finishes ───────────────────────────────────
  const revealPaths = useCallback(async (historyLength) => {
    if (finishedRef.current) return;
    finishedRef.current = true;

    try {
      const [bRes, dRes] = await Promise.all([
        fetch(`${API_BASE}/solve-bfs`),
        fetch(`${API_BASE}/solve-dijkstra`),
      ]);
      if (!bRes.ok || !dRes.ok) throw new Error('Solver error');

      const [bPath, dPath] = await Promise.all([bRes.json(), dRes.json()]);
      setBfsPath(bPath);
      setDijkstraPath(dPath);
      setShowComparison(true);
      showComparisonRef.current = true;

      // Compute score — use ref weights for accuracy
      const w          = weightsRef.current;
      const history    = playerHistoryRef.current;
      // Skip start cell (index 0) for cost: player didn't "move into" it
      const playerCost = history.slice(1).reduce((s, pos) => s + (w[pos.r]?.[pos.c] ?? 0), 0);
      const dijkCost   = dPath.slice(1).reduce((s, p)   => s + (w[p[0]]?.[p[1]]    ?? 0), 0);
      const mazeSize   = gridRef.current.length;

      const score = computeScore(
        bPath.length, historyLength,
        dijkCost, playerCost,
        mazeSize, currentLevelRef.current
      );
      setLastScore(score);

      await fetch(
        `${API_BASE}/submit-score?name=${encodeURIComponent(playerNameRef.current)}&score=${score}&level=${currentLevelRef.current}`,
        { method: 'POST' }
      ).catch(() => {});
      fetchLeaderboard();

    } catch (err) {
      console.error('revealPaths error:', err);
    }
  }, []);

  // ── Next level ────────────────────────────────────────────────────────────
  const goToNextLevel = useCallback(() => {
    const nextLevel = currentLevelRef.current + 1;
    const nextSize  = sizeRef.current + 4;  // grow maze by 4 each level
    // Reduce prob slightly as levels increase (mazes get harder = fewer shortcuts)
    const nextProb  = Math.max(0.1, 0.3 - (nextLevel - 1) * 0.02);

    setCurrentLevel(nextLevel);   currentLevelRef.current = nextLevel;
    setSize(nextSize);            sizeRef.current = nextSize;
    fetchMaze(nextSize, modeRef.current, nextProb);
  }, [fetchMaze]);

  // ── Keyboard movement ─────────────────────────────────────────────────────
  const handleKeyDown = useCallback((e) => {
    if (e.target.tagName === 'INPUT' || e.target.tagName === 'SELECT' || e.target.tagName === 'TEXTAREA') return;
    if (!playerNameRef.current || showComparisonRef.current || gridRef.current.length === 0) return;

    const history = playerHistoryRef.current;
    const last    = history[history.length - 1];
    let { r, c }  = last;

    switch (e.key) {
      case 'ArrowUp':    case 'w': case 'W': e.preventDefault(); r--; break;
      case 'ArrowDown':  case 's': case 'S': e.preventDefault(); r++; break;
      case 'ArrowLeft':  case 'a': case 'A': e.preventDefault(); c--; break;
      case 'ArrowRight': case 'd': case 'D': e.preventDefault(); c++; break;
      default: return;
    }

    const g = gridRef.current;
    if (r < 0 || r >= g.length || c < 0 || c >= g[0].length) return;

    const cell = g[r][c];
    if (cell === 'X') return;
    if (cell === 'D' && !doorOpenRef.current) {
      // Show a non-blocking toast instead of blocking alert
      return; // silently block; UI shows key count
    }

    const newPos     = { r, c };
    const newHistory = [...history, newPos];
    playerHistoryRef.current = newHistory;
    setPlayerHistory(newHistory);

    // ── Mode 2: key collection ────────────────────────────────────────────
    if (modeRef.current === 2) {
      const uniqueKeys = new Set();
      newHistory.forEach(pos => {
        if (g[pos.r][pos.c] === 'K') uniqueKeys.add(`${pos.r},${pos.c}`);
      });
      const newCount = uniqueKeys.size;
      if (newCount !== keysCollectedRef.current) {
        setKeysCollected(newCount);
        keysCollectedRef.current = newCount;
      }
      if (!doorOpenRef.current && newCount >= totalKeysRef.current && totalKeysRef.current > 0) {
        setDoorOpen(true);
        doorOpenRef.current = true;
      }
    }

    // ── Check goal ────────────────────────────────────────────────────────
    const reachedGoal =
      (modeRef.current === 1 && cell === 'E') ||
      (modeRef.current === 2 && cell === 'D' && doorOpenRef.current);

    if (reachedGoal) revealPaths(newHistory.length);
  }, [revealPaths]);

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [handleKeyDown]);

  // ── Render ────────────────────────────────────────────────────────────────
  if (!playerName) return <LoginView onLogin={handleLogin} />;

  const w           = weights;
  const playerSteps = playerHistory.length;
  // Cost: sum weight of cells moved INTO (skip start cell index 0)
  const playerCost  = playerHistory.slice(1).reduce((s, pos) => s + (w[pos.r]?.[pos.c] ?? 0), 0);
  const bfsSteps    = bfsPath?.length ?? 0;
  const bfsCost     = bfsPath?.slice(1).reduce((s, p) => s + (w[p[0]]?.[p[1]] ?? 0), 0) ?? 0;
  const dijkSteps   = dijkstraPath?.length ?? 0;
  const dijkCost    = dijkstraPath?.slice(1).reduce((s, p) => s + (w[p[0]]?.[p[1]] ?? 0), 0) ?? 0;

  return (
    <div className="app-container">
      <aside className="sidebar">

        {/* ── Header ── */}
        <div>
          <h1 style={{ color: 'var(--accent-primary)', fontSize: '1.25rem', margin: 0 }}>
            Maze Master
          </h1>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', margin: '2px 0 0' }}>
            Welcome, {playerName}
            {mode === 1 && ` | Level ${currentLevel}/${totalLevels}`}
          </p>
        </div>

        {/* ── Settings ── */}
        <div className="control-group">
          <label>Settings</label>
          <select
            value={mode}
            onChange={(e) => {
              const m = parseInt(e.target.value);
              setMode(m); modeRef.current = m;
              setCurrentLevel(1); currentLevelRef.current = 1;
              // Reset size to 21 when switching modes
              setSize(21); sizeRef.current = 21;
              fetchMaze(21, m, 0.3);
            }}
          >
            <option value="1">Mode 1 — BFS vs Dijkstra</option>
            <option value="2">Mode 2 — Door &amp; Key</option>
          </select>

          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem', alignItems: 'center' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Size</span>
            <input
              type="number" min="7" max="99" step="2" value={size}
              onChange={(e) => {
                let v = parseInt(e.target.value) || 21;
                if (v % 2 === 0) v++;
                if (v < 7)  v = 7;
                if (v > 99) v = 99;
                setSize(v); sizeRef.current = v;
              }}
              style={{ width: '52px', borderRadius: '8px', border: '1px solid #e2e8f0', padding: '0 8px', fontSize: '0.875rem' }}
            />
            {mode === 1 && <>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Levels</span>
              <input
                type="number" min="1" max="20" value={totalLevels}
                onChange={(e) => {
                  const v = Math.max(1, Math.min(20, parseInt(e.target.value) || 1));
                  setTotalLevels(v);
                }}
                style={{ width: '52px', borderRadius: '8px', border: '1px solid #e2e8f0', padding: '0 8px', fontSize: '0.875rem' }}
              />
            </>}
          </div>

          <button
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem', fontSize: '0.8rem' }}
            onClick={() => {
              setCurrentLevel(1); currentLevelRef.current = 1;
              setSize(size);      sizeRef.current = size;
              fetchMaze(size, mode, 0.3);
            }}
            disabled={loading}
          >
            {loading ? 'Building…' : '🛠️ New Game'}
          </button>
        </div>

        {/* ── Legend ── */}
        <div className="legend stats-card" style={{ padding: '0.75rem' }}>
          <label style={{ gridColumn: 'span 2', marginBottom: '0.35rem' }}>Legend</label>
          <div className="legend-item"><div className="dot" style={{ background: '#10b981' }}></div><span>Start</span></div>
          <div className="legend-item"><div className="dot" style={{ background: '#ef4444' }}></div><span>Exit</span></div>
          {mode === 2 && <>
            <div className="legend-item"><div className="dot" style={{ background: '#fbbf24', border: '1px solid #d97706' }}></div><span>Key 🗝️</span></div>
            <div className="legend-item"><div className="dot" style={{ background: '#1e293b' }}></div><span>Door 🔒</span></div>
          </>}
          <div className="legend-item"><div className="line" style={{ background: '#f59e0b', borderTop: '3px dashed #f59e0b', height: 0 }}></div><span>Your Path</span></div>
          <div className="legend-item"><div className="line" style={{ background: '#3b82f6' }}></div><span>BFS Path</span></div>
          <div className="legend-item"><div className="line" style={{ background: '#8b5cf6' }}></div><span>Dijkstra</span></div>
        </div>

        {/* ── Performance Metrics ── */}
        <div className="stats-card">
          <label>Performance</label>
          <div style={{ marginTop: '0.5rem', fontSize: '0.84rem' }}>

            {/* Player */}
            <div style={{ paddingBottom: '0.4rem', borderBottom: '1px solid #f1f5f9', marginBottom: '0.4rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Your Steps:</span> <b>{playerSteps}</b>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Your Cost:</span>  <b>{playerCost}</b>
              </div>
              {mode === 2 && (
                <div style={{
                  display: 'flex', justifyContent: 'space-between', marginTop: '0.2rem',
                  color: doorOpen ? '#10b981' : (keysCollected > 0 ? '#f59e0b' : '#94a3b8'),
                  fontWeight: 700
                }}>
                  <span>Keys:</span>
                  <span>{keysCollected} / {totalKeys} {doorOpen ? '🔓' : '🔒'}</span>
                </div>
              )}
              {mode === 2 && !doorOpen && totalKeys > 0 && (
                <div style={{ fontSize: '0.7rem', color: '#94a3b8', marginTop: '0.2rem' }}>
                  {totalKeys - keysCollected} key{totalKeys - keysCollected !== 1 ? 's' : ''} remaining
                </div>
              )}
            </div>

            {/* Algorithm results */}
            {showComparison ? (
              <>
                {lastScore !== null && (
                  <div style={{
                    background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
                    color: '#fff', borderRadius: '10px', padding: '0.5rem 0.75rem',
                    textAlign: 'center', marginBottom: '0.5rem'
                  }}>
                    <div style={{ fontSize: '0.7rem', opacity: 0.85 }}>SCORE</div>
                    <div style={{ fontSize: '1.4rem', fontWeight: 800 }}>{lastScore} pts</div>
                  </div>
                )}

                <div style={{ color: '#3b82f6', fontWeight: 600, marginBottom: '0.2rem' }}>
                  BFS <span style={{ fontWeight: 400, fontSize: '0.75rem' }}>(fewest steps)</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}><span>Steps:</span> <b>{bfsSteps}</b></div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}><span>Cost:</span>  <b>{bfsCost}</b></div>

                <div style={{ color: '#8b5cf6', fontWeight: 600, marginTop: '0.5rem', marginBottom: '0.2rem' }}>
                  Dijkstra <span style={{ fontWeight: 400, fontSize: '0.75rem' }}>(min cost)</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}><span>Steps:</span> <b>{dijkSteps}</b></div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}><span>Cost:</span>  <b>{dijkCost}</b></div>

                {/* Efficiency badge */}
                {bfsSteps > 0 && (
                  <div style={{ marginTop: '0.5rem', fontSize: '0.75rem', color: '#64748b' }}>
                    Efficiency: {Math.round((Math.min(bfsSteps, playerSteps) / Math.max(playerSteps, 1)) * 100)}%
                  </div>
                )}

                {/* Level controls */}
                {mode === 1 && currentLevel < totalLevels && (
                  <button
                    className="btn btn-primary"
                    style={{ width: '100%', marginTop: '0.75rem', background: '#10b981' }}
                    onClick={goToNextLevel}
                  >
                    Next Level →
                  </button>
                )}
                {mode === 1 && currentLevel >= totalLevels && (
                  <div style={{ marginTop: '0.75rem', textAlign: 'center', color: '#10b981', fontWeight: 'bold' }}>
                    🎉 Game Complete!
                  </div>
                )}
              </>
            ) : (
              <div style={{ color: '#94a3b8', fontSize: '0.75rem' }}>
                {mode === 2 && !doorOpen && totalKeys > 0
                  ? '🗝️ Collect all keys, then reach the door.'
                  : 'Finish the maze to see algorithm results.'}
              </div>
            )}
          </div>
        </div>

        {/* ── Hall of Fame ── */}
        <div className="control-group">
          <label>Hall of Fame</label>
          <div style={{ maxHeight: '160px', overflowY: 'auto', fontSize: '0.8rem' }}>
            {leaderboard.length === 0
              ? <span style={{ color: '#94a3b8' }}>No scores yet.</span>
              : leaderboard.map((entry, i) => (
                  <div key={i} style={{
                    display: 'flex', justifyContent: 'space-between',
                    padding: '0.3rem 0', borderBottom: '1px solid #f1f5f9',
                    alignItems: 'center'
                  }}>
                    <span>
                      {i === 0 ? '🥇' : i === 1 ? '🥈' : i === 2 ? '🥉' : `${i + 1}.`} {entry.name}
                      {entry.level > 1 && <span style={{ fontSize: '0.65rem', color: '#94a3b8', marginLeft: '4px' }}>L{entry.level}</span>}
                    </span>
                    <span style={{
                      background: '#ede9fe', color: '#7c3aed',
                      fontSize: '0.72rem', borderRadius: '9999px', padding: '1px 8px'
                    }}>
                      {entry.score} pts
                    </span>
                  </div>
                ))
            }
          </div>
        </div>

        {/* ── Footer ── */}
        <div style={{ fontSize: '0.7rem', color: '#94a3b8', textAlign: 'center' }}>
          Arrow keys or W / A / S / D to move
        </div>
        <button
          className="btn btn-secondary"
          style={{ fontSize: '0.75rem' }}
          onClick={() => window.location.reload()}
        >
          Logout
        </button>

      </aside>

      <main className="main-content">
        {loading
          ? <div className="loader">Building Maze…</div>
          : <MazeCanvas
              grid={grid} weights={weights}
              playerHistory={playerHistory}
              bfsPath={bfsPath} dijkstraPath={dijkstraPath}
              showComparison={showComparison}
              doorOpen={doorOpen}
            />
        }
      </main>
    </div>
  );
}

export default App;
