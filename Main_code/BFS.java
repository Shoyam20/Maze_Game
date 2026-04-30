
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS{
    static ArrayList<Integer>[] graph;

    private static int [][]sorting(int [][] key,int keys,int sx,int sy)
    {
        boolean []visited =new boolean[keys];
        int [][]sorted =  new int[keys][2];

        int cx=sx,cy=sy;
        int i,j;
        for(i=0;i<keys;i++)
        {
            int ind=-1;
            int min_dist=Integer.MAX_VALUE;

            for(j=0;j<keys;j++)
            {
                if(!visited[j])
                {
                    int dist=Math.abs(key[j][0] - cx)+ Math.abs(key[j][1] - cy);
                    if(dist<min_dist)
                    {
                        min_dist=dist;
                        ind=j;
                    }
                }
                
            }
            sorted[i]=key[ind];
            visited[ind]=true;
            cx=sorted[i][0];
            cy=sorted[i][1];
        }
        return sorted;
    }


    static void tograph(String [][]maze,int row,int col)
    {
        int totalnodes=row*col;

        graph=new ArrayList[totalnodes];

        int i,j;
        for(i=0;i<totalnodes;i++)
        {
            graph[i]=new ArrayList<>();
        }

        for(i=0;i<row;i++)
        {
            for(j=0;j<col;j++)
            {
                if(!maze[i][j].equals(" X "))
                {
                    int curr=i*col+j;
                    if(i>0 && !maze[i-1][j].equals(" X "))
                        graph[curr].add((i-1)*col+j);

                    if(i<row-1 && !maze[i+1][j].equals(" X "))
                        graph[curr].add((i+1)*col+j);

                    if(j>0 && !maze[i][j-1].equals(" X "))
                        graph[curr].add(i*col+(j-1));

                    if(j<col-1 && !maze[i][j+1].equals(" X "))
                        graph[curr].add(i*col+(j+1));
                }
            }
        }
    }

    static void BFS(String [][]maze,int row,int col)
    {
        int start=1*col+1;
        int end=(row-2)*col+(col-2);

        Queue<Integer> q=new LinkedList<>();
        boolean[] visited = new boolean[row*col];
        int []parent=new int[row*col];
        Arrays.fill(parent,-1);

        q.add(start);
        visited[start]=true;

        while(!q.isEmpty())
        {
            int curr=q.poll();
            if(curr==end)
                break;
            for(int w:graph[curr])
            {
                if(!visited[w])
                {
                    visited[w]=true;
                    q.add(w);
                    parent[w]=curr;
                }
            }
        }
        int curr =end;
        while(curr!=-1)
        {
            int r=curr/col;
            int c=curr%col;

            if(maze[r][c].equals(" . "))
            {
                maze[r][c]=" * ";
            }
            curr=parent[curr];
        }

    }

    static void BFS2(String [][]maze,int row,int col,int [][]key,int keys)
    {
        key=sorting(key,keys,1,1);
        
        int start=1*col+1;
        int end=(row-2)*col+(col-2);

        int []keynode=new int [keys+2];

        keynode[0]=start;
        for(int i=0;i<keys;i++)
        {
            keynode[i+1]=key[i][0]*col+key[i][1];
        }
        keynode[keys+1]=end;

        for(int k=0 ;k<keynode.length-1;k++)
        {
            int from =keynode[k];
            int to =keynode[k+1];

            tograph(maze, row, col);

            Queue<Integer> q=new LinkedList<>();
            boolean[] visited = new boolean[row*col];
            int []parent=new int[row*col];
            Arrays.fill(parent,-1);

            q.add(from);
            visited[from]=true;

            while(!q.isEmpty())
            {
                int curr=q.poll();
                if(curr==to)
                    break;
                for(int w:graph[curr])
                {
                    if(!visited[w])
                    {
                        visited[w]=true;
                        q.add(w);
                        parent[w]=curr;
                    }
                }
            }
            int curr =to;
            while(curr!=-1)
            {
                int r=curr/col;
                int c=curr%col;

                String cell = maze[r][c].trim();

                if (cell.equals(".") || cell.equals("^"))
                {
                    maze[r][c] = " ^ ";
                }
                curr=parent[curr];
            }
        }
    }

    static List<int[]> getBFSPath(String[][] maze, int row, int col) {
        tograph(maze, row, col);
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
            if (curr == end) break;
            for (int w : graph[curr]) {
                if (!visited[w]) {
                    visited[w] = true;
                    q.add(w);
                    parent[w] = curr;
                }
            }
        }

        List<int[]> path = new ArrayList<>();
        int curr = end;
        while (curr != -1) {
            path.add(0, new int[]{curr / col, curr % col});
            curr = parent[curr];
        }
        return path;
    }

    static List<int[]> getBFS2Path(String[][] maze, int row, int col, int[][] key, int keys) {
        int[][] sortedKeys = sorting(key, keys, 1, 1);
        int[] keynode = new int[keys + 2];
        keynode[0] = 1 * col + 1;
        for (int i = 0; i < keys; i++) {
            keynode[i + 1] = sortedKeys[i][0] * col + sortedKeys[i][1];
        }
        keynode[keys + 1] = (row - 2) * col + (col - 2);

        List<int[]> fullPath = new ArrayList<>();
        for (int k = 0; k < keynode.length - 1; k++) {
            int from = keynode[k];
            int to = keynode[k + 1];
            tograph(maze, row, col);

            Queue<Integer> q = new LinkedList<>();
            boolean[] visited = new boolean[row * col];
            int[] parent = new int[row * col];
            Arrays.fill(parent, -1);

            q.add(from);
            visited[from] = true;

            while (!q.isEmpty()) {
                int curr = q.poll();
                if (curr == to) break;
                for (int w : graph[curr]) {
                    if (!visited[w]) {
                        visited[w] = true;
                        q.add(w);
                        parent[w] = curr;
                    }
                }
            }


            List<int[]> segment = new ArrayList<>();
            int curr = to;
            while (curr != -1) {
                segment.add(0, new int[]{curr / col, curr % col});
                curr = parent[curr];
            }
            if (k > 0 && !segment.isEmpty()) segment.remove(0);
            fullPath.addAll(segment);
        }
        return fullPath;
    }


    static int pathcount(String [][]maze , int row ,int col)
    {
        int count=0;
        int i,j;
        for(i=0;i<row;i++)
        {
            for(j=0;j<col;j++)
            {
                if(maze[i][j].equals(" ^ "))
                    count++;
            }
        }
        return count+1;
    }
}
