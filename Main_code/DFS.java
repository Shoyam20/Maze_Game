// package Main_code;

import java.util.Random;

public class DFS {
    public void DFS(String[][] maze, int i, int j, int row, int col) {
        Random rand = new Random();
        maze[i][j] = " . ";

        int[][] directions = {
                { -2, 0 },
                { 2, 0 },
                { 0, -2 },
                { 0, 2 }
        };
        int x;
        for (x = 0; x < directions.length; x++) {
            int y = rand.nextInt(directions.length);
            int[] temp = directions[x];
            directions[x] = directions[y];
            directions[y] = temp;
        }

        for (int[] dir : directions) {
            int in = i + dir[0];
            int jn = j + dir[1];

            if (in > 0 && in < row - 1
                    && jn > 0 && jn < col - 1
                    && maze[in][jn].equals(" X ")) {
                maze[i + dir[0] / 2][j + dir[1] / 2] = " . ";

                DFS(maze, in, jn, row, col);
            }
        }
    }

}
