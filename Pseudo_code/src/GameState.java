import java.util.*;

public class GameState {
    public int[][] maze;
    public int[][] weights;
    public int[] playerPos;
    public List<int[]> playerPath;
    public int playerSteps;
    public int playerScore;
    public boolean gameOver;
    public long startTime;
    
    public int[] start;
    public int[] exit;
    
    public List<int[]> bfsPath;
    public List<int[]> dfsPath;
    public List<int[]> dijkstraPath;
    public int dijkstraCost = -1;
    
    public GameState(int[][] maze, int[][] weights) {
        this.maze = maze;
        this.weights = weights;
        this.start = new int[]{1, 1};
        this.exit = new int[]{maze.length - 2, maze[0].length - 2};
        this.playerPos = new int[]{start[0], start[1]};
        this.playerPath = new ArrayList<>();
        this.playerPath.add(new int[]{playerPos[0], playerPos[1]});
        this.playerSteps = 0;
        this.playerScore = 0;
        this.gameOver = false;
        this.startTime = System.currentTimeMillis();
    }
    
    public void movePlayer(String direction) {
        if (gameOver) return;
        
        int newRow = playerPos[0];
        int newCol = playerPos[1];
        
        switch (direction.toUpperCase()) {
            case "UP":
                newRow--;
                break;
            case "DOWN":
                newRow++;
                break;
            case "LEFT":
                newCol--;
                break;
            case "RIGHT":
                newCol++;
                break;
            default:
                return;
        }
        
        // Validate move
        if (isValidMove(newRow, newCol)) {
            playerPos[0] = newRow;
            playerPos[1] = newCol;
            playerPath.add(new int[]{newRow, newCol});
            playerSteps++;
            
            // Check if reached exit
            if (Arrays.equals(playerPos, exit)) {
                gameOver = true;
                calculateScore();
            }
        }
    }
    
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < maze.length && col >= 0 && col < maze[0].length && maze[row][col] == 0;
    }
    
    public void solveAll() {
        bfsPath = MazeSolver.solveBFS(maze, start, exit);
        dfsPath = MazeSolver.solveDFS(maze, start, exit);
        dijkstraPath = MazeSolver.solveDijkstra(maze, weights, start, exit);
        
        if (dijkstraPath != null && !dijkstraPath.isEmpty()) {
            dijkstraCost = MazeSolver.getDijkstraCost(weights, dijkstraPath);
        }
    }
    
    public void calculateScore() {
        if (bfsPath == null || bfsPath.isEmpty()) {
            solveAll();
        }
        
        int bfsSteps = bfsPath.size() - 1;
        int difference = playerSteps - bfsSteps;
        playerScore = Math.max(0, 1000 - difference * 10);
    }
    
    public Map<String, Object> getRanking() {
        Map<String, Object> ranking = new HashMap<>();
        
        if (bfsPath == null || bfsPath.isEmpty()) {
            solveAll();
        }
        
        int bfsSteps = bfsPath.size() - 1;
        ranking.put("playerSteps", playerSteps);
        ranking.put("bfsSteps", bfsSteps);
        
        String rating;
        if (playerSteps == bfsSteps) {
            rating = "OPTIMAL ⭐⭐⭐";
        } else if (playerSteps <= bfsSteps * 1.2) {
            rating = "GREAT ⭐⭐";
        } else if (playerSteps <= bfsSteps * 1.5) {
            rating = "GOOD ⭐";
        } else {
            rating = "NEEDS IMPROVEMENT";
        }
        
        ranking.put("rating", rating);
        return ranking;
    }
}
