import { generateMaze, movePlayer, solveMaze } from './localMazeEngine';

const API_BASE = '/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with ${response.status}`);
  }

  return response.json();
}

export async function checkBackendHealth() {
  try {
    return await request('/health');
  } catch {
    return { status: 'offline', fallback: true };
  }
}

export async function generateMazeWithFallback(rows, cols) {
  try {
    const data = await request('/maze/generate', {
      method: 'POST',
      body: JSON.stringify({ rows, cols })
    });
    return { ...data, source: 'backend' };
  } catch {
    return { ...generateMaze(rows, cols), source: 'local-fallback' };
  }
}

export async function movePlayerWithFallback(maze, player, direction) {
  try {
    const data = await request('/maze/move', {
      method: 'POST',
      body: JSON.stringify({ maze, player, direction })
    });
    return { ...data, source: 'backend' };
  } catch {
    return { ...movePlayer(maze, player, direction), source: 'local-fallback' };
  }
}

export async function solveMazeWithFallback(maze, start, end) {
  try {
    const data = await request('/maze/solve', {
      method: 'POST',
      body: JSON.stringify({ maze, start, end })
    });
    return { ...data, source: 'backend' };
  } catch {
    return { path: solveMaze(maze, start, end), source: 'local-fallback' };
  }
}
