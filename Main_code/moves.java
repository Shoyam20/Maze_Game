import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class moves {


    public static int play(String[][] maze, int row, int col,int sx, int sy, int ex, int ey) throws Exception {

        Terminal terminal =TerminalBuilder.builder().system(true).build();
        print p =new print();

        boolean found = false;
        int a = sx, b = sy;
        int steps = 0;

        maze[a][b] = " P ";
        p.printing(maze, row, col);

        while (!found) {

            System.out.println("8/W. UP  5/S. DOWN  4/A. LEFT  6/D. RIGHT");
            System.out.println("Enter the direction to move : ");

            int move = terminal.reader().read();

            if (move == 'w' || move == 'W') 
                move = 8;
            else if (move == 's' || move == 'S') 
                move = 5;
            else if (move == 'a' || move == 'A') 
                move = 4;
            else if (move == 'd' || move == 'D') 
                move = 6;
            else move = move - '0';

            switch (move) {

                case 8:
                    if (1 > a - 1) 
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                    if (maze[a - 1][b].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        a--;
                        steps++;
                        maze[a][b] = " P ";
                    }
                    break;

                case 5:
                    if (row - 2 < a + 1) 
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                    if (maze[a + 1][b].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        a++;
                        steps++;
                        maze[a][b] = " P ";
                    }
                    break;

                case 4:
                    if (1 > b - 1)
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                    if (maze[a][b - 1].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        b--;
                        steps++;
                        maze[a][b] = " P ";
                    }
                    break;

                case 6:
                    if (b + 1 > col - 2)
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                    if (maze[a][b + 1].equals(" X ")) {
                        System.out.println("Hit wall.");
                    } else {
                        maze[a][b] = " # ";
                        b++;
                        steps++;
                        maze[a][b] = " P ";
                    }
                    break;

                default:
                    System.out.println("Enter valid move.");
            }

            p.printing(maze, row, col);

            if (a == ex && b == ey) {
                System.out.println("\nReached Exit with "+steps+"steps\n");
                found = true;
            }
        }

        return steps;
    }
    


    public static int play2(String[][] maze, int row, int col,int sx, int sy, int ex, int ey,int keys) throws Exception {

        Terminal terminal =TerminalBuilder.builder().system(true).build();
        print p =new print();

        boolean found     = false;
        int a = sx, b = sy;
        int steps   = 0;
        int count = 0;
        boolean door= false;

        maze[a][b] = " P ";

        p.printing(maze, row, col);

        while (!found) {

            System.out.println("8/W. UP  5/S. DOWN  4/A. LEFT  6/D. RIGHT");
            System.out.print("Enter direction : ");

            int move = terminal.reader().read();

            if (move == 'w' || move == 'W') 
                move = 8;
            else if (move == 's' || move == 'S') 
                move = 5;
            else if (move == 'a' || move == 'A') 
                move = 4;
            else if (move == 'd' || move == 'D') 
                move = 6;
            else move = move - '0';

            System.out.println(move);
            System.out.println();

            int na = a, nb = b;
            boolean validMove = false;

            switch (move) {

                case 8:
                    if (1 > a - 1) 
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                        na = a - 1; nb = b;
                        validMove = true;
                        break;

                case 5:
                    if (row - 2 < a + 1) 
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                    na = a + 1; nb = b;
                    validMove = true;
                    break;

                case 4:
                    if (1 > b - 1) 
                    { 
                        System.out.println("Boundary hit."); 
                        continue; 
                    }
                    na = a; nb = b - 1;
                    validMove = true;
                    break;

                case 6:
                    if (b + 1 > col - 2) 
                    { 
                        System.out.println("Boundary hit."); 
                    continue; 
                        
                    }
                    na = a; nb = b + 1;
                    validMove = true;
                    break;

                default:
                    System.out.println("Enter valid move.");
            }

            if (!validMove)
                continue;

            String nextCell = maze[na][nb];

            if (nextCell.equals(" X ")) {
                System.out.println("Hit wall.");
                p.printing(maze, row, col);
                continue;
            }

            if (nextCell.equals(" D ")) {
                if (!door) {
                    System.out.println("Door is locked,"+keys+"keys requeired.\n");
                    p.printing(maze, row, col);
                    continue;
                }
            }
            maze[a][b] = " # ";
            a = na;
            b = nb;
            steps++;

            if (maze[a][b].equals(" K ")) {
                count++;
                System.out.println("Key found , needs"+count+"/"+ keys +"keys");

                if (count == keys) {
                    door = true;
                    for(int i=0;i<row;i++)
                    {
                        for(int j=0;j<col;j++)
                        {
                            if(maze[i][j].equals(" D "))
                            {
                                maze[i][j] = " . ";
                            }
                        }
                    }
                    
                    System.out.println("DOOR IS NOW OPEN!");
                }
            }

            maze[a][b] = " P ";
            p.printing(maze, row, col);

            // --- Exit check ---
            if (a == ex && b == ey) {
                System.out.println("\nReached Exit with "+steps+"steps\n");
                found = true;
            }
        }
        return steps;
    }
}
