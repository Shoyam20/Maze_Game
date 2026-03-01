// this code is based on maze generation for k path only which makes it more easy to implement k-shortest algorithm for finding short,long and normal paths in the maze.

// We will modify maze generation so that:

// Loops are added in controlled manner

// Branching factor is limited

// Graph density is moderate

// Path explosion is avoided

//Introduced the weighted graph 

import java.util.*;

public class mazegame6 {

    static Random rand = new Random();
    static int rows = 11;
    static int cols = 11;

    static ArrayList<Edge>[] graph;
    static int[][] weight;

    static class Edge {
        int node, cost;
        Edge(int node, int cost) {
            this.node = node;
            this.cost = cost;
        }
    }

    public static void main(String[] args) {

        Scanner user = new Scanner(System.in);
        String[][] maze = new String[rows][cols];
        weight = new int[rows][cols];

        // Fill walls
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                maze[i][j] = "X";

        // Step 1: Perfect Maze
        generateMaze(maze, 1, 1);

        // Step 2: Force extra structural corridors
        createGuaranteedAlternatePaths(maze);

        // Step 3: Assign Weights
        assignWeights(maze);

        // Step 4: Convert to Weighted Graph
        convertToWeightedGraph(maze);

        System.out.println("Maze Ready with Multiple Complete Paths.");

        // Player setup
        int px = 1, py = 1;
        maze[px][py] = "P";
        maze[rows - 2][cols - 2] = "T";

        boolean gotTreasure = false;

        while (!gotTreasure) {

            printMaze(maze);

            System.out.println("1-Up 2-Down 3-Left 4-Right 0-Quit");
            int choice = user.nextInt();

            int nx = px, ny = py;

            if (choice == 1) nx--;
            else if (choice == 2) nx++;
            else if (choice == 3) ny--;
            else if (choice == 4) ny++;
            else if (choice == 0) System.exit(0);

            if (nx >= 0 && nx < rows && ny >= 0 && ny < cols
                    && !maze[nx][ny].equals("X")) {

                if (maze[nx][ny].equals("T"))
                    gotTreasure = true;

                maze[px][py] = ".";
                px = nx;
                py = ny;
                maze[px][py] = "P";
            }
        }

        printMaze(maze);
        System.out.println("🎉 Treasure Found!");
        user.close();
    }

    // DFS Perfect Maze
    static void generateMaze(String[][] maze, int x, int y) {

        maze[x][y] = ".";
        int[][] dirs = {{-2,0},{2,0},{0,-2},{0,2}};

        Collections.shuffle(Arrays.asList(dirs));

        for (int[] d : dirs) {

            int nx = x + d[0];
            int ny = y + d[1];

            if (nx > 0 && nx < rows-1 &&
                ny > 0 && ny < cols-1 &&
                maze[nx][ny].equals("X")) {

                maze[x + d[0]/2][y + d[1]/2] = ".";
                generateMaze(maze, nx, ny);
            }
        }
    }

    // FORCE Multiple Complete Paths
    static void createGuaranteedAlternatePaths(String[][] maze) {

        // Vertical corridor near middle
        for (int i = 1; i < rows-1; i++)
            maze[i][cols/2] = ".";

        // Horizontal corridor near middle
        for (int j = 1; j < cols-1; j++)
            maze[rows/2][j] = ".";

        // Ensure connection near exit
        maze[rows-3][cols-3] = ".";
        maze[rows-3][cols-4] = ".";
        maze[rows-4][cols-3] = ".";
    }

    // Assign Intelligent Weights
    static void assignWeights(String[][] maze) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (!maze[i][j].equals("X")) {

                    int distExit = Math.abs(i-(rows-2)) +
                                   Math.abs(j-(cols-2));

                    if (distExit < 3)
                        weight[i][j] = 8;
                    else
                        weight[i][j] = 2;

                    if (rand.nextDouble() < 0.05)
                        weight[i][j] = 15;
                }
            }
        }
    }

    // Convert to Weighted Graph
    static void convertToWeightedGraph(String[][] maze) {

        graph = new ArrayList[rows*cols];

        for (int i = 0; i < graph.length; i++)
            graph[i] = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (!maze[i][j].equals("X")) {

                    int current = i*cols+j;

                    if (i>0 && !maze[i-1][j].equals("X"))
                        graph[current].add(
                            new Edge((i-1)*cols+j, weight[i-1][j]));

                    if (i<rows-1 && !maze[i+1][j].equals("X"))
                        graph[current].add(
                            new Edge((i+1)*cols+j, weight[i+1][j]));

                    if (j>0 && !maze[i][j-1].equals("X"))
                        graph[current].add(
                            new Edge(i*cols+(j-1), weight[i][j-1]));

                    if (j<cols-1 && !maze[i][j+1].equals("X"))
                        graph[current].add(
                            new Edge(i*cols+(j+1), weight[i][j+1]));
                }
            }
        }
    }

    static void printMaze(String[][] maze) {

        for (int i = 0; i < cols; i++)
            System.out.print("+---");
        System.out.println("+");

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                System.out.print("| ");
                System.out.print(maze[i][j].equals(".") ? " " : maze[i][j]);
                System.out.print(" ");
            }
            System.out.println("|");

            for (int k = 0; k < cols; k++)
                System.out.print("+---");
            System.out.println("+");
        }
    }
}