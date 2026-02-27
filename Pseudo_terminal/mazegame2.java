import java.util.*;

public class mazegame2 {

    static Random rand = new Random();

    public static void main(String[] args) {

        Scanner user = new Scanner(System.in);

        int rows = 15;   // MUST be odd
        int cols = 15;   // MUST be odd

        String[][] maze = new String[rows][cols];

        // Fill entire grid with walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = "X";
            }
        }

        // Generate maze using DFS
        generateMaze(maze, 1, 1, rows, cols);

        // Player and Treasure
        int px = 1;
        int py = 1;
        maze[px][py] = "P";

        maze[rows - 2][cols - 2] = "T";

        boolean gotTreasure = false;

        while (!gotTreasure) {

            printMaze(maze, rows, cols);

            System.out.println("Move:");
            System.out.println("1) Up  2) Down  3) Left  4) Right  0) Quit");

            int choice = user.nextInt();

            int newX = px;
            int newY = py;

            if (choice == 1) newX--;
            else if (choice == 2) newX++;
            else if (choice == 3) newY--;
            else if (choice == 4) newY++;
            else if (choice == 0) System.exit(0);

            if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {

                if (maze[newX][newY].equals("X")) {
                    System.out.println("Hit a wall! Try again.");
                }
                else {

                    if (maze[newX][newY].equals("T")) {
                        gotTreasure = true;
                    }

                    maze[px][py] = ".";
                    px = newX;
                    py = newY;
                    maze[px][py] = "P";
                }
            }
            else {
                System.out.println("Out of bounds!");
            }
        }

        printMaze(maze, rows, cols);
        System.out.println("🎉 You found the treasure!");

        user.close();
    }

    // DFS Maze Generator
    public static void generateMaze(String[][] maze, int x, int y, int rows, int cols) {

        maze[x][y] = ".";

        int[][] directions = {
                {-2, 0},   // Up
                {2, 0},    // Down
                {0, -2},   // Left
                {0, 2}     // Right
        };

        // Shuffle directions randomly
        for (int i = 0; i < directions.length; i++) {
            int r = rand.nextInt(directions.length);
            int[] temp = directions[i];
            directions[i] = directions[r];
            directions[r] = temp;
        }

        for (int[] dir : directions) {

            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && nx < rows - 1 &&
                ny > 0 && ny < cols - 1 &&
                maze[nx][ny].equals("X")) {

                // Remove wall between
                maze[x + dir[0]/2][y + dir[1]/2] = ".";

                generateMaze(maze, nx, ny, rows, cols);
            }
        }
    }

    // Terminal Box Display
    public static void printMaze(String[][] maze, int rows, int cols) {

        for (int i = 0; i < cols; i++) {
            System.out.print("+---");
        }
        System.out.println("+");

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                System.out.print("| ");

                if (maze[i][j].equals(".")) {
                    System.out.print(" ");
                } else {
                    System.out.print(maze[i][j]);
                }

                System.out.print(" ");
            }

            System.out.println("|");

            for (int k = 0; k < cols; k++) {
                System.out.print("+---");
            }
            System.out.println("+");
        }
    }
}