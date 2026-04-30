import React, { useRef, useEffect } from 'react';

function drawPath(ctx, path, cellSize, color, offsetX, offsetY) {
    if (!path || path.length < 2) return;
    ctx.save();
    ctx.beginPath();
    ctx.strokeStyle = color;
    ctx.lineWidth = Math.max(3, cellSize * 0.2);
    ctx.globalAlpha = 0.82;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.moveTo(
        (path[0][1] + 0.5) * cellSize + offsetX,
        (path[0][0] + 0.5) * cellSize + offsetY
    );
    for (let i = 1; i < path.length; i++) {
        ctx.lineTo(
            (path[i][1] + 0.5) * cellSize + offsetX,
            (path[i][0] + 0.5) * cellSize + offsetY
        );
    }
    ctx.stroke();
    ctx.restore();
}

const MazeCanvas = ({ grid, weights, playerHistory, bfsPath, dijkstraPath, showComparison, doorOpen }) => {
    const canvasRef = useRef(null);

    useEffect(() => {
        if (!grid || grid.length === 0) return;

        const canvas = canvasRef.current;
        const ctx = canvas.getContext('2d');
        const rows = grid.length;
        const cols = grid[0].length;

        // Fit canvas into available space
        const maxSize = Math.min(window.innerWidth * 0.64, window.innerHeight * 0.90);
        const cellSize = Math.max(8, Math.floor(maxSize / Math.max(rows, cols)));

        canvas.width  = cols * cellSize;
        canvas.height = rows * cellSize;

        // ── 1. Background: walls + floors ─────────────────────────────────
        for (let r = 0; r < rows; r++) {
            for (let c = 0; c < cols; c++) {
                ctx.fillStyle = grid[r][c] === 'X' ? '#2d3f55' : '#f8fafc';
                ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }

        // ── 2. Weight labels (only when cells are large enough) ────────────
        if (weights && weights.length > 0 && cellSize >= 18) {
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            for (let r = 0; r < rows; r++) {
                for (let c = 0; c < cols; c++) {
                    const cell = grid[r][c];
                    if (cell === 'X' || cell === 'S' || cell === 'E' || cell === 'D') continue;
                    const w = weights[r]?.[c];
                    if (w == null || w < 0) continue;
                    // Tint floor by weight (heavier = slightly redder)
                    const ratio = w / 9;
                    ctx.fillStyle = `rgba(239,68,68,${ratio * 0.12})`;
                    ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                    ctx.fillStyle = `hsl(${220 - ratio * 60},20%,72%)`;
                    ctx.font = `${Math.floor(cellSize * 0.32)}px Inter, sans-serif`;
                    ctx.fillText(w, (c + 0.5) * cellSize, (r + 0.5) * cellSize);
                }
            }
        }

        // ── 3. Special cells (S, E, D, K) ─────────────────────────────────
        for (let r = 0; r < rows; r++) {
            for (let c = 0; c < cols; c++) {
                const cell = grid[r][c];
                const cx = (c + 0.5) * cellSize;
                const cy = (r + 0.5) * cellSize;

                if (cell === 'S') {
                    ctx.fillStyle = '#10b981';
                    ctx.beginPath(); ctx.arc(cx, cy, cellSize * 0.38, 0, Math.PI * 2); ctx.fill();
                    ctx.fillStyle = '#fff';
                    ctx.font = `bold ${Math.floor(cellSize * 0.38)}px sans-serif`;
                    ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
                    ctx.fillText('S', cx, cy);
                }
                else if (cell === 'E') {
                    ctx.fillStyle = '#ef4444';
                    ctx.fillRect((c + 0.12) * cellSize, (r + 0.12) * cellSize, cellSize * 0.76, cellSize * 0.76);
                    ctx.fillStyle = '#fff';
                    ctx.font = `bold ${Math.floor(cellSize * 0.38)}px sans-serif`;
                    ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
                    ctx.fillText('E', cx, cy);
                }
                else if (cell === 'D') {
                    if (doorOpen) {
                        ctx.fillStyle = '#d1fae5';
                        ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                        if (cellSize >= 18) {
                            ctx.font = `${Math.floor(cellSize * 0.5)}px sans-serif`;
                            ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
                            ctx.fillText('🔓', cx, cy);
                        }
                    } else {
                        ctx.fillStyle = '#1e293b';
                        ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                        if (cellSize >= 18) {
                            ctx.font = `${Math.floor(cellSize * 0.5)}px sans-serif`;
                            ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
                            ctx.fillText('🔒', cx, cy);
                        }
                    }
                }
                else if (cell === 'K') {
                    const picked = playerHistory.some(pos => pos.r === r && pos.c === c);
                    if (!picked) {
                        ctx.fillStyle = '#fef3c7';
                        ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                        if (cellSize >= 18) {
                            ctx.font = `${Math.floor(cellSize * 0.5)}px sans-serif`;
                            ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
                            ctx.fillText('🗝️', cx, cy);
                        } else {
                            // Small cell fallback: amber square
                            ctx.fillStyle = '#d97706';
                            ctx.fillRect(
                                (c + 0.2) * cellSize, (r + 0.2) * cellSize,
                                cellSize * 0.6, cellSize * 0.6
                            );
                        }
                    }
                }
            }
        }

        // ── 4. Algorithm paths ─────────────────────────────────────────────
        if (showComparison) {
            const off = Math.max(1, cellSize * 0.1);
            // Dijkstra (purple) drawn first so BFS (blue) renders on top
            if (dijkstraPath && dijkstraPath.length > 1)
                drawPath(ctx, dijkstraPath, cellSize, '#8b5cf6',  off,  off);
            if (bfsPath && bfsPath.length > 1)
                drawPath(ctx, bfsPath,      cellSize, '#3b82f6', -off, -off);
        }

        // ── 5. Player trail (dashed amber) ─────────────────────────────────
        if (playerHistory && playerHistory.length > 1) {
            ctx.save();
            ctx.beginPath();
            ctx.setLineDash([Math.max(2, cellSize * 0.15), Math.max(2, cellSize * 0.15)]);
            ctx.strokeStyle = '#f59e0b';
            ctx.lineWidth = Math.max(2, cellSize * 0.16);
            ctx.lineJoin = 'round';
            ctx.lineCap  = 'round';
            ctx.moveTo((playerHistory[0].c + 0.5) * cellSize, (playerHistory[0].r + 0.5) * cellSize);
            for (let i = 1; i < playerHistory.length; i++) {
                ctx.lineTo((playerHistory[i].c + 0.5) * cellSize, (playerHistory[i].r + 0.5) * cellSize);
            }
            ctx.stroke();
            ctx.restore();
        }

        // ── 6. Player token (always on top) ───────────────────────────────
        if (playerHistory && playerHistory.length > 0) {
            const last = playerHistory[playerHistory.length - 1];
            const cx = (last.c + 0.5) * cellSize;
            const cy = (last.r + 0.5) * cellSize;
            const r  = cellSize * 0.33;

            // Glow
            ctx.fillStyle = 'rgba(245,158,11,0.25)';
            ctx.beginPath(); ctx.arc(cx, cy, r * 1.5, 0, Math.PI * 2); ctx.fill();
            // Body
            ctx.fillStyle = '#f59e0b';
            ctx.beginPath(); ctx.arc(cx, cy, r, 0, Math.PI * 2); ctx.fill();
            // Border
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = Math.max(1.5, cellSize * 0.05);
            ctx.stroke();
        }

    }, [grid, weights, playerHistory, bfsPath, dijkstraPath, showComparison, doorOpen]);

    return (
        <div className="maze-wrapper">
            <canvas
                ref={canvasRef}
                style={{ display: 'block', borderRadius: '10px', boxShadow: '0 4px 24px rgba(0,0,0,0.10)' }}
            />
        </div>
    );
};

export default MazeCanvas;
