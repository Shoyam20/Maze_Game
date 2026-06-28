
import java.util.Random;
import java.util.Scanner;

public class Main2 {
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
        Random rand = new Random();
        int i,j;
        System.out.println("Enter the size of maze:");
        int size=sc.nextInt();

        if(size%2==0)
            size++;
        if(size <9)
            size=9;


        int keys=size/7;
        if(keys<1)
            keys=1;

        double prob=0.3;
        int row=size;
        int col=size;

        String [][]maze=new String[row][col];

        //Filling maze with walls (" X ")
        for(i=0;i<row;i++)
        {
            for(j =0 ; j < col ;j++)
            {
                maze[i][j]=" X ";
            }
        }

        dfs.DFS(maze,1,1,row,col); // generating minimum one possible path to exit

        maze[1][1]=" S ";
        maze[row-2][col-2]=" D ";
        maze[row-2][col-1]="-->";

        int sx=1,sy=1 ; // starting index
        int ex=row-2,ey=col-2; // ending index

        m.Multi_path(maze,row,col,prob); // generating multiple paths

        maze[row-2][col-2]=" D ";

        int[][] key = new int[keys][2];
        int count= 0;
        // keys spawns 
        while(count<keys) 
        {
            int x=rand.nextInt(row-2)+1;
            int y=rand.nextInt(col-2)+1;

            if(maze[x][y].equals(" . "))
            {
                maze[x][y]=" K ";
                key[count][0]=x;
                key[count][1]=y;
                count++;
            }
        }

        int [][]weight=w.weights(maze,row,col); 

        String [][]maze1=copymaze(maze,row,col); // used to perform other operations

        int player_moves=mv.play2(maze,row,col,sx,sy,ex,ey,keys);

        String [][]bfs_maze=copymaze(maze1,row,col);
        String [][]dij_maze=copymaze(maze1,row,col);

        bfs_maze[ex][ey] = " E ";
        dij_maze[ex][ey] = " E ";


        bfs.tograph(bfs_maze,row,col);
        bfs.BFS2(bfs_maze,row,col,key,keys);

        System.out.println("\n---BFS path---\n");
        p.printing(bfs_maze, row, col);

        //counting bfs steps (" * ")
        int bfs_count=bfs.pathcount(bfs_maze,row,col)+1;

        d.run2(dij_maze,weight,row,col,sx,sy,ex,ey,key,keys);

        System.out.println("\n---DIJKSTRA path---\n");
        p.printing(dij_maze, row, col);

        //counting dijkstra steps (" $ ")
        int dijkstra_count=0;
        for(i=0;i<row;i++)
        {
            for(j=0;j<col;j++)
            {
                if(dij_maze[i][j].equals(" $ "))
                    dijkstra_count++;
            }
        }


        System.out.println("---PALYER AND ALGORITHM STEPS---");
            
        System.out.println(" Player Steps :"+player_moves);
        System.out.println(" BFS Steps :"+bfs_count);
        System.out.println(" Dijkstra Steps :"+dijkstra_count);

        
        System.out.println("Successfully complete all levels.");
        Leaderboard.save("Hurdle", size, 1, player_moves, bfs_count);
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
