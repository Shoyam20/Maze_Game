import org.jline.terminal.Terminal;

public class moves {

    static void play(String[][] maze, int row, int col,
                     Terminal terminal, BFS bfs, print p) throws Exception {

        boolean found = false;
        int a = 1, b = 1;

        maze[a][b] = " P ";

        while (found == false) {

            System.out.println("8/W. UP 5/S. DOWN , 4/A. LEFT 6/D. RIGHT");
            System.out.print("Enter the direction to move : ");

            int move = terminal.reader().read();

            if (move == 'w' || move == 'W') move = 8;
            else if (move == 's' || move == 'S') move = 5;
            else if (move == 'a' || move == 'A') move = 4;
            else if (move == 'd' || move == 'D') move = 6;
            else move = move - '0';

            System.out.println(move);
            System.out.println();

            switch (move) {
                case 8:
                    if (1 > a - 1) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a - 1][b].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        a--;
                        maze[a][b] = " P ";
                    }
                    break;

                case 5:
                    if (row - 2 < a + 1) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a + 1][b].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        a++;
                        maze[a][b] = " P ";
                    }
                    break;

                case 4:
                    if (1 > b - 1) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a][b - 1].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        b--;
                        maze[a][b] = " P ";
                    }
                    break;

                case 6:
                    if (b + 1 > col - 2) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a][b + 1].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        b++;
                        maze[a][b] = " P ";
                    }
                    break;

                default:
                    System.out.println("Enter valid move.");
            }

            p.printing(maze, row, col);

            if (a == row - 2 && b == col - 2) {
                System.out.println("Exiting.......");
                found = true;
            }
        }
    }
}