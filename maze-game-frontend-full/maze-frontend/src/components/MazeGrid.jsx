export default function MazeGrid({ maze, player, goal, visited, solutionSet, showSolution }) {
  if (!maze.length) return <div className="panel">Loading maze...</div>;

  const largestSide = Math.max(maze.length, maze[0].length);
  const cellSize = largestSide <= 21 ? 24 : largestSide <= 31 ? 18 : 14;

  return (
    <div className="maze-wrap">
      <div
        className="maze-grid"
        style={{ gridTemplateColumns: `repeat(${maze[0].length}, ${cellSize}px)` }}
      >
        {maze.flatMap((row, rowIndex) =>
          row.map((cell, colIndex) => {
            const key = `${rowIndex}-${colIndex}`;
            const isWall = cell === 1;
            const isPlayer = rowIndex === player.row && colIndex === player.col;
            const isGoal = rowIndex === goal.row && colIndex === goal.col;
            const isVisited = visited.has(key);
            const isSolution = showSolution && solutionSet.has(key);
            let className = 'maze-cell';
            if (isWall) className += ' wall';
            else className += ' path';
            if (isVisited && !isWall) className += ' visited';
            if (isSolution && !isWall) className += ' solution';
            if (isGoal) className += ' goal';
            if (isPlayer) className += ' player';
            return <div key={key} className={className} style={{ width: cellSize, height: cellSize }} />;
          })
        )}
      </div>
    </div>
  );
}
