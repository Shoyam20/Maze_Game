import { useEffect, useMemo, useState } from 'react';
import { checkBackendHealth, generateMazeWithFallback, movePlayerWithFallback, solveMazeWithFallback } from '../services/mazeApi';
import { saveScore } from '../services/leaderboard';

export function useMazeGame(playerName) {
  const [rows, setRows] = useState(21);
  const [cols, setCols] = useState(21);
  const [maze, setMaze] = useState([]);
  const [player, setPlayer] = useState({ row: 1, col: 0 });
  const [startPosition, setStartPosition] = useState({ row: 1, col: 0 });
  const [goal, setGoal] = useState({ row: 19, col: 20 });
  const [moves, setMoves] = useState(0);
  const [visited, setVisited] = useState(new Set());
  const [solution, setSolution] = useState([]);
  const [showSolution, setShowSolution] = useState(false);
  const [won, setWon] = useState(false);
  const [engine, setEngine] = useState('checking');
  const [message, setMessage] = useState('Ready');

  useEffect(() => {
    checkBackendHealth().then((health) => {
      setEngine(health.fallback ? 'local-fallback' : 'backend');
    });
  }, []);

  async function regenerateMaze() {
    const data = await generateMazeWithFallback(Number(rows) || 21, Number(cols) || 21);
    setMaze(data.maze);
    setPlayer(data.start);
    setStartPosition(data.start);
    setGoal(data.end);
    setMoves(0);
    setVisited(new Set([`${data.start.row}-${data.start.col}`]));
    setSolution([]);
    setShowSolution(false);
    setWon(false);
    setEngine(data.source);
    setMessage('Maze generated and ready to play!');
  }

  useEffect(() => {
    regenerateMaze();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function move(direction) {
    if (!maze.length || won) return;
    const result = await movePlayerWithFallback(maze, player, direction);
    setEngine(result.source);
    if (!result.moved) {
      setMessage(result.reason || 'Move blocked');
      return;
    }
    setPlayer(result.player);
    setMoves((current) => current + 1);
    setVisited((current) => {
      const next = new Set(current);
      next.add(`${result.player.row}-${result.player.col}`);
      return next;
    });
    setMessage('Move accepted');
    if (result.player.row === goal.row && result.player.col === goal.col) {
      setWon(true);
      setMessage('You escaped the maze! Shortest path shown.');
      solveMazeWithFallback(maze, startPosition, goal).then((res) => {
        setSolution(res.path || []);
        setShowSolution(true);
        if (playerName) {
          saveScore(playerName, moves + 1, (res.path ? Math.max(0, res.path.length - 1) : 0), rows);
        }
      });
    }
  }

  async function solve() {
    if (!maze.length) return;
    const result = await solveMazeWithFallback(maze, startPosition, goal);
    setSolution(result.path || []);
    setShowSolution(true);
    setEngine(result.source);
    setMessage(result.path?.length ? 'Solution path generated' : 'No path found');
  }

  const exploredPercent = useMemo(() => {
    if (!maze.length) return 0;
    const walkable = maze.flat().filter((cell) => cell === 0).length;
    return Math.min(100, Math.round((visited.size / walkable) * 100));
  }, [maze, visited]);

  const solutionSet = useMemo(
    () => new Set(solution.map((cell) => `${cell.row}-${cell.col}`)),
    [solution]
  );

  return {
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
    engine,
    message,
    exploredPercent,
    setRows,
    setCols,
    regenerateMaze,
    move,
    solve,
    setShowSolution
  };
}
