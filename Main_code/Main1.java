

// public class Main1 {

//     static void func() throws Exception {

//         Terminal terminal = TerminalBuilder.builder().system(true).build();

//         DFS dfs = new DFS();
//         BFS bfs = new BFS();
//         multi_path m = new multi_path();
//         print p = new print();

//         Scanner sc = new Scanner(System.in);

//         //User inputs
//         System.out.print("Enter the maze size : ");
//         int baseSize = sc.nextInt();
//         if (baseSize % 2 == 0) baseSize -= 1;
//         if (baseSize < 7) baseSize = 7;

//         System.out.print("Enter number of levels: ");
//         int totalLevels = sc.nextInt();
//         if (totalLevels < 1) totalLevels = 1;


//         double prob = 0.3;


//         for (int level = 1; level <= totalLevels; level++) {


//             int size = baseSize + (level - 1) * 4;
//             int row = size;
//             int col = size;

            
//             String[][] maze = new String[row][col];

//             //Fill the maze with walls
//             for (int i = 0; i < row; i++)
//                 for (int j = 0; j < col; j++)
//                     maze[i][j] = " X ";

//             dfs.DFS(maze, 1, 1, row, col);

//             maze[1][1]= " S ";
//             maze[row-2][col-2] = " E ";
//             maze[row-2][col-1] = "-->";

//             int sx = 1, sy = 1;
//             int ex = row - 2, ey = col - 2;

//             m.Multi_path(maze, row, col, prob);

//             int[][] weight = Weights.generateWeights(maze,row,col);

//             // Save clean maze 
//             String[][] cleanMaze = copyMaze(maze);

//             // --- Show maze with level header ---
//             // p.printWithHeader(maze, row, col, level, totalLevels);

//             // Movements
//             int playerSteps = moves.play(maze, row, col,terminal, p,sx, sy, ex, ey);

//             // BFS path
//             String[][] mazeForBFS = copyMaze(cleanMaze);

//             bfs.tograph(mazeForBFS, row, col);

//             bfs.BFS(mazeForBFS, row, col);

//             System.out.println("\nBFS Shortest Path (Level " + level + ") ");

//             // p.printWithHeader(mazeForBFS, row, col, level, totalLevels);

//             //  Count BFS path length 
//             int bfsSteps = 0;
//             for (int i = 0; i < row; i++)
//                 for (int j = 0; j < col; j++)
//                     if (mazeForBFS[i][j].equals(" * "))
//                         bfsSteps++;
//             bfsSteps++;

//             //  Dijkstra 
//             String[][] mazeForDijkstra = copyMaze(cleanMaze);

//             Dijkstra.run(mazeForDijkstra, weight, sx, sy, ex, ey);

//             System.out.println("\nDijkstra Minimum Weight Path (Level " + level + ")");

//             // p.printWithHeader(mazeForDijkstra, row, col, level, totalLevels);

//             // // --- Performance stats ---
//             // System.out.println("\n==========================================");
//             // System.out.println("         LEVEL " + level + " PERFORMANCE");
//             // System.out.println("==========================================");
//             // System.out.println("  Your steps        : " + playerSteps);
//             // System.out.println("  BFS optimal steps : " + bfsSteps);

//             double ratio = 0.5; // default if bfsSteps is 0
//             if (bfsSteps > 0) {
//                 ratio = (double) bfsSteps / playerSteps;
//             }

//             System.out.printf("  Efficiency ratio  : %.2f%n", ratio);

//             if (ratio >= 0.8) {
//                 System.out.println("  Performance       : EXCELLENT! Near optimal path.");
//             } else if (ratio >= 0.5) {
//                 System.out.println("  Performance       : GOOD. Some wrong turns.");
//             } else {
//                 System.out.println("  Performance       : NEEDS WORK. Lots of backtracking.");
//             }
//             System.out.println("==========================================\n");

//             // --- Adapt difficulty for next level ---
//             if (level < totalLevels) {
//                 prob = prob - (ratio - 0.5) * 0.2;
//                 prob = Math.max(0.1, Math.min(0.5, prob)); // clamp 0.1 to 0.5

//                 System.out.println("  Next level difficulty adjusted.");
//                 System.out.printf("  New multi-path probability: %.2f%n", prob);
//                 System.out.println();

//                 System.out.println("Press ENTER to continue to Level " + (level + 1) + "...");
//                 sc.nextLine();
//                 sc.nextLine();
//             }
//         }
//         System.out.println("\nCompleted\n");
//         // // --- Game complete ---
//         // System.out.println("\n==========================================");
//         // System.out.println("       CONGRATULATIONS! GAME COMPLETE!    ");
//         // System.out.println("   You completed all " + totalLevels + " levels!");
//         // System.out.println("==========================================\n");
//     }

//     public static String[][] copyMaze(String[][] maze) {
//         int rows = maze.length;
//         int cols = maze[0].length;
//         String[][] newMaze = new String[rows][cols];
//         for (int i = 0; i < rows; i++)
//             for (int j = 0; j < cols; j++)
//                 newMaze[i][j] = maze[i][j];
//         return newMaze;
//     }

//     public static void main(String[] args) throws Exception {
//         func();
//     }
// }


import java.util.Scanner;



public class Main1 {
    public void func() throws Exception
    {
        

        Scanner sc = new Scanner(System.in);
        DFS dfs =new DFS();
        BFS bfs =new BFS();
        multi_path m=new multi_path();
        print p=new print();
        moves mv=new moves();
        Dijkstra d= new Dijkstra();
        Weights w=new Weights();

        System.out.println("Enter the size of maze:");
        int size=sc.nextInt();

        if(size%2==0)
            size++;
        if(size <9)
            size=9;

        System.out.println("Enter the levels of maze:");
        int level=sc.nextInt();

        if(level<1)
            level=1;

        double prob=0.3;

        int totalPlayerSteps = 0;
        int totalBFSSteps= 0;
        int totalDijSteps= 0;

        int i ,j,k;
        for(i =0;i<level;i++)
        {

            int update_size=size+(i)*4;  // increase the size of maze after each iteration

            int row=update_size;
            int col=update_size;

            String [][]maze=new String[row][col];

            //Filling maze with walls (" X ")
            for(k=0;k<row;k++)
            {
                for(j =0 ; j < col ;j++)
                {
                    maze[k][j]=" X ";
                }
            }

            dfs.DFS(maze,1,1,row,col); // generating minimum one possible path to exit

            maze[1][1]=" S ";
            maze[row-2][col-2]=" E ";
            maze[row-2][col-1]="-->";

            int sx=1,sy=1 ; // starting index
            int ex=row-2,ey=col-2; // ending index

            m.Multi_path(maze,row,col,prob); // generating multiple paths

            maze[row-2][col-2]=" E ";

            int [][]weight=w.weights(maze,row,col);

            String [][]maze1=copymaze(maze,row,col); // used to perform other operations

            int player_moves=mv.play(maze,row,col,sx,sy,ex,ey);

            String [][]bfs_maze=copymaze(maze1,row,col);
            String [][]dij_maze=copymaze(maze1,row,col);

            bfs.tograph(bfs_maze,row,col);
            bfs.BFS(bfs_maze,row,col);

            //counting bfs steps (" * ")
            int bfs_count=0;
            for(k=0;k<row;k++)
            {
                for(j=0;j<col;j++)
                {
                    if(bfs_maze[k][j].equals(" * "))
                        bfs_count++;
                }
            }


            d.run(dij_maze,weight,row,col,sx,sy,ex,ey);

            //counting dij steps (" @ ")
            int dijkstra_count=0;
            for(k=0;k<row;k++)
            {
                for(j=0;j<col;j++)
                {
                    if(dij_maze[k][j].equals(" @ "));
                        dijkstra_count++;
                }
            }
            System.out.println("\n---BFS path---\n");
            p.printing(bfs_maze, row, col);

            System.out.println("\n---DIJKSTRA path---\n");
            p.printing(dij_maze, row, col);

            System.out.println("--=PALYER AND ALGORITHM STEPS---");
            
            System.out.println(" Player Steps :"+player_moves);
            System.out.println(" BFS Steps :"+bfs_count);
            System.out.println(" Dijkstra Steps :"+dijkstra_count);

            totalPlayerSteps += player_moves;
            totalBFSSteps+= bfs_count;
            totalDijSteps+= dijkstra_count;

            double ratio =(double)bfs_count/player_moves;
            if (i < level )
            {
                prob = prob - (ratio - 0.5) * 0.2;
                prob = Math.max(0.1, Math.min(0.5, prob));
            }

            System.out.println("Press ENTER to continue....");
            sc.nextLine();
            sc.nextLine();
        }
        System.out.println("Successfully complete all levels.");
        Leaderboard.save("Level", size, level, totalPlayerSteps, totalBFSSteps);
    }

    public static String[][] copymaze(String [][]maze,int row,int col)
    {
        String [][]copy=new String[row][col];
        int i,j;
        for(i=0;i<row;i++)
        {
            for(j=0;j<col;j++)
            {
                copy[i][j]=maze[i][j];
            }
        }
        return copy;
    }
}
