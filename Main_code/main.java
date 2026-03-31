import java.util.Scanner;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class main {
    public static void main(String[] args) throws Exception {

        Terminal terminal = TerminalBuilder.builder().system(true).build();

        DFS dfs = new DFS();
        BFS bfs = new BFS();
        multi_path m = new multi_path();
        print p = new print();

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the maze size : ");

        int row = sc.nextInt();
        if (row % 2 == 0)
            row -= 1;
        int col = row;

        String[][] maze = new String[row][col];

        int i, j;

        for (i = 0; i < row; i++) {
            for (j = 0; j < col; j++) {
                maze[i][j] = " X ";
            }
        }

        dfs.DFS(maze, 1, 1, row, col);

        maze[1][1] = " S ";
        maze[row - 2][col - 2] = " E ";
        maze[row - 2][col - 1] = "-->";

        m.Multi_path(maze, row, col, 0.3);

        boolean found = false;
        int a = 1, b = 1;

        maze[a][b] = " P ";
        p.printing(maze, row, col);

        moves.play(maze, row, col, terminal, bfs, p);

        if (a == row - 2 && b == col - 2) {
            System.out.println("Exiting.......");
            bfs.tograph(maze, row, col);
            bfs.BFS(maze, row, col);
            System.out.println("\nShortest Path :");
            p.printing(maze, row, col);
            found = true;
        }
    }
}