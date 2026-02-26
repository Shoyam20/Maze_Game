import java.util.*;

public class MazeGenerator {
    private static Random rand = new Random();
    
    // Generate maze using Recursive Backtracking DFS
    public static int[][] generate(int size) {
        if (size % 2 == 0) size++; // Ensure odd size
        int[][] maze = new int[size][size];
        
        // Fill with walls
        for (int i = 0; i < size; i++) {
            Arrays.fill(maze[i], 1);
        }
        
        // Start DFS from (1,1)
        carvePath(maze, 1, 1);
        
        // Create additional paths by removing random walls (20% of walls)
        createMultiplePaths(maze);
        
        return maze;
    }
    
    // Remove random walls to create loops and multiple paths
    private static void createMultiplePaths(int[][] maze) {
        int size = maze.length;
        int wallsToRemove = (size * size) / 20; // Remove ~5% of cells as walls
        
        for (int i = 0; i < wallsToRemove; i++) {
            int row = rand.nextInt(size - 2) + 1;
            int col = rand.nextInt(size - 2) + 1;
            
            // Only remove walls that are between two path cells
            if (maze[row][col] == 1) {
                int pathNeighbors = 0;
                if (row > 0 && maze[row-1][col] == 0) pathNeighbors++;
                if (row < size-1 && maze[row+1][col] == 0) pathNeighbors++;
                if (col > 0 && maze[row][col-1] == 0) pathNeighbors++;
                if (col < size-1 && maze[row][col+1] == 0) pathNeighbors++;
                
                // Remove wall if it connects at least 2 paths
                if (pathNeighbors >= 2) {
                    maze[row][col] = 0;
                }
            }
        }
    }
    
    private static void carvePath(int[][] maze, int row, int col) {
        // Mark current cell as path
        maze[row][col] = 0;
        
        // Four directions: UP, DOWN, LEFT, RIGHT
        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        
        // Shuffle directions for randomness
        shuffleArray(directions);
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            // Check if neighbor is within bounds and unvisited (wall)
            if (isValid(maze, newRow, newCol) && maze[newRow][newCol] == 1) {
                // Remove wall between current and neighbor
                maze[row + dir[0]/2][col + dir[1]/2] = 0;
                
                // Recurse into neighbor
                carvePath(maze, newRow, newCol);
            }
        }
    }
    
    private static boolean isValid(int[][] maze, int row, int col) {
        return row > 0 && row < maze.length - 1 && col > 0 && col < maze[0].length - 1;
    }
    
    private static void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    // Generate random weights for Dijkstra (1-5 for path cells)
    public static int[][] generateWeights(int[][] maze) {
        int size = maze.length;
        int[][] weights = new int[size][size];
        
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (maze[r][c] == 0) {
                    weights[r][c] = rand.nextInt(5) + 1; // Random weight 1-5
                } else {
                    weights[r][c] = 0;
                }
            }
        }
        
        return weights;
    }
}
