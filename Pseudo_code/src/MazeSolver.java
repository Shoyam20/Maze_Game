import java.util.*;

public class MazeSolver {
    
    // BFS - Shortest path (unweighted)
    public static List<int[]> solveBFS(int[][] maze, int[] start, int[] exit) {
        int rows = maze.length;
        int cols = maze[0].length;
        
        Queue<int[]> queue = new LinkedList<>();
        Map<String, int[]> parent = new HashMap<>();
        boolean[][] visited = new boolean[rows][cols];
        
        queue.offer(start);
        visited[start[0]][start[1]] = true;
        parent.put(key(start), null);
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            
            if (Arrays.equals(current, exit)) {
                return reconstructPath(parent, start, exit);
            }
            
            for (int[] dir : directions) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];
                
                if (isValid(maze, newRow, newCol) && !visited[newRow][newCol]) {
                    int[] neighbor = {newRow, newCol};
                    queue.offer(neighbor);
                    visited[newRow][newCol] = true;
                    parent.put(key(neighbor), current);
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    // DFS - Longest path (deep exploration)
    public static List<int[]> solveDFS(int[][] maze, int[] start, int[] exit) {
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        List<int[]> currentPath = new ArrayList<>();
        List<int[]> longestPath = new ArrayList<>();
        
        dfsHelper(maze, start, exit, visited, currentPath, longestPath);
        
        return longestPath;
    }
    
    private static void dfsHelper(int[][] maze, int[] current, int[] exit, 
                                   boolean[][] visited, List<int[]> currentPath, 
                                   List<int[]> longestPath) {
        visited[current[0]][current[1]] = true;
        currentPath.add(new int[]{current[0], current[1]});
        
        if (Arrays.equals(current, exit)) {
            if (currentPath.size() > longestPath.size()) {
                longestPath.clear();
                for (int[] pos : currentPath) {
                    longestPath.add(new int[]{pos[0], pos[1]});
                }
            }
        } else {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            
            for (int[] dir : directions) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];
                
                if (isValid(maze, newRow, newCol) && !visited[newRow][newCol]) {
                    dfsHelper(maze, new int[]{newRow, newCol}, exit, visited, currentPath, longestPath);
                }
            }
        }
        
        visited[current[0]][current[1]] = false;
        currentPath.remove(currentPath.size() - 1);
    }
    
    // Dijkstra - Minimum cost path (weighted)
    public static List<int[]> solveDijkstra(int[][] maze, int[][] weights, int[] start, int[] exit) {
        int rows = maze.length;
        int cols = maze[0].length;
        
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dist[start[0]][start[1]] = weights[start[0]][start[1]];
        
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
        Map<String, int[]> parent = new HashMap<>();
        
        pq.offer(new Node(start[0], start[1], weights[start[0]][start[1]]));
        parent.put(key(start), null);
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            if (current.row == exit[0] && current.col == exit[1]) {
                return reconstructPath(parent, start, exit);
            }
            
            if (current.cost > dist[current.row][current.col]) {
                continue;
            }
            
            for (int[] dir : directions) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                
                if (isValid(maze, newRow, newCol)) {
                    int newCost = current.cost + weights[newRow][newCol];
                    
                    if (newCost < dist[newRow][newCol]) {
                        dist[newRow][newCol] = newCost;
                        pq.offer(new Node(newRow, newCol, newCost));
                        parent.put(key(new int[]{newRow, newCol}), new int[]{current.row, current.col});
                    }
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    public static int getDijkstraCost(int[][] weights, List<int[]> path) {
        int cost = 0;
        for (int[] pos : path) {
            cost += weights[pos[0]][pos[1]];
        }
        return cost;
    }
    
    private static boolean isValid(int[][] maze, int row, int col) {
        return row >= 0 && row < maze.length && col >= 0 && col < maze[0].length && maze[row][col] == 0;
    }
    
    private static String key(int[] pos) {
        return pos[0] + "," + pos[1];
    }
    
    private static List<int[]> reconstructPath(Map<String, int[]> parent, int[] start, int[] exit) {
        List<int[]> path = new ArrayList<>();
        int[] current = exit;
        
        while (current != null) {
            path.add(0, current);
            current = parent.get(key(current));
        }
        
        return path;
    }
    
    static class Node {
        int row, col, cost;
        
        Node(int row, int col, int cost) {
            this.row = row;
            this.col = col;
            this.cost = cost;
        }
    }
}
