export const WALL = 1;
export const PATH = 0;

function createEmptyMaze(rows, cols) {
  return Array.from({ length: rows }, () => Array(cols).fill(WALL));
}

export function generateMaze(rows, cols) {
  const safeRows = Math.max(11, rows % 2 === 0 ? rows - 1 : rows);
  const safeCols = Math.max(11, cols % 2 === 0 ? cols - 1 : cols);
  const maze = createEmptyMaze(safeRows, safeCols);
  const visited = Array.from({ length: safeRows }, () => Array(safeCols).fill(false));
  const stack = [[1, 1]];
  const dirs = [[0, 2], [0, -2], [2, 0], [-2, 0]];

  maze[1][1] = PATH;
  visited[1][1] = true;

  while (stack.length > 0) {
    const [r, c] = stack[stack.length - 1];
    const neighbors = dirs
      .map(([dr, dc]) => [r + dr, c + dc, dr, dc])
      .filter(([nr, nc]) => nr > 0 && nc > 0 && nr < safeRows - 1 && nc < safeCols - 1 && !visited[nr][nc]);

    if (neighbors.length === 0) {
      stack.pop();
      continue;
    }

    const [nr, nc, dr, dc] = neighbors[Math.floor(Math.random() * neighbors.length)];
    maze[r + dr / 2][c + dc / 2] = PATH;
    maze[nr][nc] = PATH;
    visited[nr][nc] = true;
    stack.push([nr, nc]);
  }

  for (let r = 1; r < safeRows - 1; r += 1) {
    for (let c = 1; c < safeCols - 1; c += 1) {
      if (maze[r][c] === WALL && Math.random() < 0.12) {
        const openAround = [maze[r - 1]?.[c], maze[r + 1]?.[c], maze[r]?.[c - 1], maze[r]?.[c + 1]].filter(
          (value) => value === PATH
        ).length;
        if (openAround >= 2) maze[r][c] = PATH;
      }
    }
  }

  maze[1][0] = PATH;
  maze[safeRows - 2][safeCols - 1] = PATH;

  return {
    maze,
    start: { row: 1, col: 0 },
    end: { row: safeRows - 2, col: safeCols - 1 }
  };
}

export function movePlayer(maze, player, direction) {
  const moves = {
    UP: [-1, 0],
    DOWN: [1, 0],
    LEFT: [0, -1],
    RIGHT: [0, 1]
  };

  const [dr, dc] = moves[direction] ?? [0, 0];
  const nr = player.row + dr;
  const nc = player.col + dc;

  if (nr < 0 || nc < 0 || nr >= maze.length || nc >= maze[0].length) {
    return { player, moved: false, reason: 'Boundary hit' };
  }

  if (maze[nr][nc] === WALL) {
    return { player, moved: false, reason: 'Hit wall' };
  }

  return {
    player: { row: nr, col: nc },
    moved: true,
    reason: null
  };
}

export function solveMaze(maze, start, end) {
  const rows = maze.length;
  const cols = maze[0].length;
  const queue = [[start.row, start.col]];
  const visited = Array.from({ length: rows }, () => Array(cols).fill(false));
  const parent = Array.from({ length: rows }, () => Array(cols).fill(null));
  const dirs = [[-1, 0], [1, 0], [0, -1], [0, 1]];

  visited[start.row][start.col] = true;

  while (queue.length > 0) {
    const [r, c] = queue.shift();
    if (r === end.row && c === end.col) break;

    for (const [dr, dc] of dirs) {
      const nr = r + dr;
      const nc = c + dc;
      if (nr < 0 || nc < 0 || nr >= rows || nc >= cols) continue;
      if (visited[nr][nc] || maze[nr][nc] === WALL) continue;
      visited[nr][nc] = true;
      parent[nr][nc] = [r, c];
      queue.push([nr, nc]);
    }
  }

  if (!visited[end.row][end.col]) return [];

  const path = [];
  let current = [end.row, end.col];
  while (current) {
    path.push({ row: current[0], col: current[1] });
    current = parent[current[0]][current[1]];
  }
  return path.reverse();
}
