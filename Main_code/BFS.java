// package Main_code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BFS {

    static ArrayList<Integer>[] graph;
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
    static void BFS(String[][] maze, int row, int col)
    {
    int start = 1 * col + 1;
    int end = (row - 2) * col + (col - 2);

    Queue<Integer> q = new LinkedList<>();
    boolean visited[] = new boolean[row * col];
    int parent[] = new int[row * col];

    Arrays.fill(parent,-1);

    q.add(start);
    visited[start] = true;

    while(!q.isEmpty())
    {
        int curr = q.poll();

        if(curr == end)
            break;

        for(int nei : graph[curr])
        {
            if(!visited[nei])
            {
                visited[nei] = true;
                parent[nei] = curr;
                q.add(nei);
            }
        }
    }

    // Reconstruct shortest path
    int curr = end;

    while(curr != -1)
    {
        int r = curr / col;
        int c = curr % col;

      if(maze[r][c].equals(" . "))
    {
    maze[r][c] = " * ";
    }
        curr = parent[curr];
    }
}
}
