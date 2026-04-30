public class print{

    public void printing(String [][]maze,int row ,int col)
    {
        int i,j;
        for(i=0;i<row;i++)
        {
            for( j=0;j<col;j++)
            {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }
}