const STORAGE_KEY = 'mazeGameLeaderboard';

export function getLeaderboard() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const scores = JSON.parse(raw);
      // Sort highest points first
      return scores.sort((a, b) => b.score - a.score);
    }
  } catch (e) {
    console.error('Failed to parse leaderboard', e);
  }
  return [];
}

export function saveScore(name, playerMoves, bfsMoves, size) {
  const safePlayerMoves = Math.max(1, playerMoves);
  const safeBfsMoves = Math.max(0, bfsMoves);
  const ratio = safeBfsMoves / safePlayerMoves;
  
  // Example max points: 100 base * size factor * efficiency
  // Someone who does perfect path on size 21 = ~2100 points
  const calculatedScore = Math.round(100 * ratio * (size / 10));

  const entry = {
    id: Date.now().toString(),
    name,
    playerMoves: safePlayerMoves,
    bfsMoves: safeBfsMoves,
    size,
    score: calculatedScore,
    date: new Date().toLocaleDateString()
  };

  const scores = getLeaderboard();
  scores.push(entry);
  
  // Sort and keep top 50
  scores.sort((a, b) => b.score - a.score);
  const topScores = scores.slice(0, 50);

  localStorage.setItem(STORAGE_KEY, JSON.stringify(topScores));
  return entry;
}
