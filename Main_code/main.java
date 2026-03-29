// package Main_code;     // using these libraries to take the input directly from the terminal no need press enter
import java.util.Scanner;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class main {
    public static void main(String[] args)throws Exception {

        Terminal terminal = TerminalBuilder.builder().system(true).build();   // using for direct input from terminal 

        DFS dfs = new DFS();
        BFS bfs = new BFS();
        multi_path m = new multi_path();
        print p =new print();
 

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the maze size : ");
        
        int row = sc.nextInt();
        if(row%2==0)
            row-=1;
        int col = row;

        String[][] maze = new String[row][col];

        int i, j;

        for (i = 0; i < row; i++) {
            for (j = 0; j < col; j++) {
                maze[i][j] = " X ";
            }
        }
        
        dfs.DFS(maze, 1, 1, row, col);

        maze[1][1]=" S ";   
        maze[row-2][col-2]=" E ";
        maze[row-2][col-1]="-->";

        m.Multi_path(maze, row, col, 0.3);

        boolean found = false;
        int a = 1, b = 1;

        maze[a][b] = " P ";
        p.printing(maze,row,col);
        while (found == false) {
            System.out.println("8. UP 5. DOWN , 4. LEFT 6. RIGHT");
            System.out.print("Enter the direction to move : ");
            
            // int move = sc.nextInt();   // used for taking input with enter 


            int move = terminal.reader().read();  // take input without enter (it returns the ASCII value )
            move=move-'0';  

            System.out.println(move);
            System.out.println();

            switch (move) {
                case 8: // up
                    if (1 > a - 1) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a - 1][b].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        a -= 1;
                        maze[a][b] = " P ";
                    }
                    break;
                case 5: // down
                    if (row - 2 < a + 1) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a + 1][b].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        a += 1;
                        maze[a][b] = " P ";
                    }
                    break;
                case 4: // left
                    if (1 > b - 1) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a][b - 1].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        b -= 1;
                        maze[a][b] = " P ";
                    }
                    break;
                case 6: // right
                    if (b + 1 > col - 2) {
                        System.out.println("Boundary hit.");
                        continue;
                    }
                    if (maze[a][b + 1].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        b += 1;
                        maze[a][b] = " P ";
                    }
                    break;
                default:
                    System.out.println("Enter valid move.");

            }

            for (i = 0; i < row; i++) {
                for (j = 0; j < col; j++) {
                    System.out.print(maze[i][j]);
                }
                System.out.println();
            }
          if (a == row - 2 && b == col - 2)
            {
            System.out.println("Exiting.......");

            bfs.tograph(maze,row,col);   // convert maze to graph
            bfs.BFS(maze,row,col);       // run BFS

            System.out.println("\nShortest Path :");
            p.printing(maze, row, col);

            found = true;
            }

        }
             
    }
}