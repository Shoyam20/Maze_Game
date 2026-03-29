// package Main_code;

import java.util.Random;

public class multi_path {
    public void Multi_path(String [][]maze,int row,int col , Double prob)
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
}
