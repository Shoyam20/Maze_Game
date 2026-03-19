package Main_code;     // using these libraries to take the input directly from the terminal no need press enter
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


import java.util.Random;
import java.util.Scanner;


public class test1 {

    static ArrayList<Integer>[] graph;

    static void printing(String [][]maze , int row,int col)
    {
        int i ,j ;
        for (i = 0; i < row; i++) {
            System.out.print(i);
            for (j = 0; j < col; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }
    static void BFS(String[][] maze, int row, int col) {

        int start = 1 * col + 1;
        int end = (row - 2) * col + (col - 2);

        Queue<Integer> q = new LinkedList<>();
        boolean[] visited = new boolean[row * col];
        int[] parent = new int[row * col];

        Arrays.fill(parent, -1);

        q.add(start);
        visited[start] = true;

        while (!q.isEmpty()) {
            int curr = q.poll();

            if (curr == end)
                break;

            for (int nei : graph[curr]) {
                if (!visited[nei]) {
                    visited[nei] = true;
                    parent[nei] = curr;
                    q.add(nei);
                }
            }
        }
    }
    static void Multi_path(String [][]maze,int row,int col , Double prob)
    {
        Random rand = new Random();
        int i,j;

        for(i=1;i<row-1;i++)
        {
            for(j=1;j<col-1;j++)
            {
                if(maze[i][j].equals(" X "))
                {
                    boolean hori = maze[i+1][j].equals(" . ") && maze[i-1][j].equals(" . ");
                    boolean vert = maze[i][j+1].equals(" . ") && maze[i][j-1].equals(" . ");

                    if(hori || vert)
                    {
                        if(rand.nextDouble(1)<prob)
                            maze[i][j]=" . ";
                    }
                }
            }
        }
    }
    static void DFS(String[][] maze, int i, int j, int row, int col) {
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

    static void tograph(String [][]maze,int row,int col)
    {
        int totalnodes=row*col;

        graph=new ArrayList[totalnodes];

        for(int i =0;i<totalnodes;i++)
        {
            graph[i]=new ArrayList<>();
        }

        for(int i =0;i<row;i++)
        {
            for(int j=0;j<col;j++)
            {
                if(!maze[i][j].equals(" X "))
                {
                    int curr=i*col+j;

                    if(i>0 && !maze[i-1][j].equals(" X "))
                    {
                        graph[curr].add((i-1)*col+j);
                    }
                    if(i<row-1 && !maze[i+1][j].equals(" X "))
                    {
                        graph[curr].add((i+1)*col+j);
                    }
                    if(j>0 && !maze[i][j-1].equals(" X "))
                    {
                        graph[curr].add(i*col+(j-1));
                    }
                    if(j<col-1 && !maze[i][j+1].equals(" X "))
                    {
                        graph[curr].add(i*col+(j+1));
                    }
                }
            }
        }
    }
    public static void main(String[] args)throws Exception {

        Terminal terminal = TerminalBuilder.builder().system(true).build();   // using for direct input from terminal 



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
        
        DFS(maze, 1, 1, row, col);

        maze[1][1]=" S ";   
        maze[row-2][col-2]=" E ";
        maze[row-2][col-1]="-->";

        Multi_path(maze, row, col, 0.3);

        boolean found = false;
        int a = 1, b = 1;

        maze[a][b] = " P ";
        printing(maze,row,col);
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
                        maze[a][b] = " . ";
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
                        maze[a][b] = " . ";
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
                        maze[a][b] = " . ";
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
                        maze[a][b] = " . ";
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

                System.out.println("\nShortest Path :");
                printing(maze, row, col);

                found = true;
            }

        }
             
    }
}
