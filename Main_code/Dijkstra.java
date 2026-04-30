
import java.util.*;

public class Dijkstra
{
    static class Node {
        int x, y, cost;
        Node(int x, int y, int cost) {
            this.x = x; this.y = y; this.cost = cost;
        }
    }
    
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

    private static boolean isValid2(int x,int y ,String[][] maze,int row,int col)
    {
        if(x<0 || y<0 || x>=row || y>=col)
        {
            return false;
        }
        String cell=maze[x][y].trim();

        if(cell.equals(".") || cell.equals("S") || cell.equals("E") ||cell.equals("K") || cell.equals("$") || cell.equals("P") || cell.equals("#"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private static boolean isValid(int x,int y ,String[][] maze,int row,int col)
    {
        if(x<0 || y<0 || x>=row || y>=col)
        {
            return false;
        }
        String cell=maze[x][y].trim();

        if(cell.equals(".") || cell.equals("P") ||cell.equals("S") || cell.equals("E") || cell.equals("#"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    
    public static void run(String [][]maze,int [][]weight,int row,int col,int sx,int sy,int ex,int ey)
    {
        int [][]dist=new int [row][col];
        int [][]parentX=new int [row][col];
        int [][]parentY=new int [row][col];

        for (int i = 0; i < row; i++) {
            Arrays.fill(parentX[i], -1);
            Arrays.fill(parentY[i], -1);
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);

        dist[sx][sy] = 0;
        pq.add(new Node(sx, sy, 0));

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            int x = cur.x, y = cur.y, cost = cur.cost;

            if (x == ex && y == ey) 
                break;

            if (cost > dist[x][y]) 
                continue;

            for (int d = 0; d < 4; d++) {
                int nx = x + dx[d], ny = y + dy[d];
                if (isValid(nx, ny, maze,row,col)) 
                {
                    int newCost = cost + weight[nx][ny];
                    if (newCost < dist[nx][ny]) 
                    {
                        dist[nx][ny] = newCost;
                        parentX[nx][ny] = x;
                        parentY[nx][ny] = y;
                        pq.add(new Node(nx, ny, newCost));
                    }
                }
            }
        }

        int x=ex,y=ey;

        while(!(x==sx  && y==sy))
        {
            if(!maze[x][y].trim().equals("E"))
                maze[x][y]=" @ ";
            int px=parentX[x][y],py=parentY[x][y];
            if(px==-1 && py==-1)
            {
                System.out.println("No path found\n");
            }
            x=px;
            y=py;
        }
    }


    public static void run2(String[][]maze ,int [][]weight,int row,int col,int sx,int sy,int ex,int ey,int [][]key,int keys) {
        key=sorting(key,keys,sx,sy);

        int [][]keynode=new int [keys+2][2];

        keynode[0][0]=sx;
        keynode[0][1]=sy;

        for(int i=0;i<keys;i++)
        {
            keynode[i+1][0]=key[i][0];
            keynode[i+1][1]=key[i][1];
        }
        keynode[keys+1][0]=ex;
        keynode[keys+1][1]=ey;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};


        for(int k=0;k<keynode.length-1;k++)
        {
            int fromX=keynode[k][0];
            int fromY=keynode[k][1];

            int toX=keynode[k+1][0];
            int toY=keynode[k+1][1];

            int[][] dist    = new int[row][col];
            int[][] parentX = new int[row][col];
            int[][] parentY = new int[row][col];
            boolean [][]visited=new boolean[row][col];

            for(int i=0;i<row;i++)
            {
                Arrays.fill(dist[i], Integer.MAX_VALUE);
                Arrays.fill(parentX[i], -1);
                Arrays.fill(parentY[i], -1);
            }
            PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
            dist[fromX][fromY] = 0;

            pq.add(new Node(fromX, fromY, 0));

            while (!pq.isEmpty()) {
                Node cur = pq.poll();
                int x = cur.x, y = cur.y, cost = cur.cost;

                if (visited[x][y]) 
                    continue;
                visited[x][y] = true;

                if (x == toX && y == toY) 
                    break;

                for (int d = 0; d < 4; d++) {
                    int nx = x + dx[d], ny = y + dy[d];
                    if (isValid2(nx, ny, maze,row,col) && !visited[nx][ny]) 
                    {
                        int w = weight[nx][ny];
                        if (w < 0)
                             w = 0; 
                        int newCost = cost + w;
                        if (newCost < dist[nx][ny]) 
                        {
                            dist[nx][ny] = newCost;
                            parentX[nx][ny] = x;
                            parentY[nx][ny] = y;
                            pq.add(new Node(nx, ny, newCost));
                        }
                    }
                }
            }


            int x = toX, y = toY;
            while (!(x == fromX && y == fromY)) 
            {
                String cell = maze[x][y].trim();
                if (!cell.equals("S") && !cell.equals("E") && !cell.equals("K"))
                    maze[x][y] = " $ ";
                int px = parentX[x][y], py = parentY[x][y];
                if (px == -1 && py == -1) {
                    System.out.println("No path found in leg " + k);
                    return;
                }
                x = px; y = py;
            }

        }
    }

    public static List<int[]> getDijkstraPath(String[][] maze, int[][] weight, int row, int col, int sx, int sy, int ex, int ey) {
        int[][] dist = new int[row][col];
        int[][] parentX = new int[row][col];
        int[][] parentY = new int[row][col];

        for (int i = 0; i < row; i++) {
            Arrays.fill(parentX[i], -1);
            Arrays.fill(parentY[i], -1);
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
        dist[sx][sy] = 0;
        pq.add(new Node(sx, sy, 0));

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            int x = cur.x, y = cur.y, cost = cur.cost;
            if (x == ex && y == ey) break;
            if (cost > dist[x][y]) continue;

            for (int d = 0; d < 4; d++) {
                int nx = x + dx[d], ny = y + dy[d];
                if (isValid(nx, ny, maze, row, col)) {
                    int newCost = cost + weight[nx][ny];
                    if (newCost < dist[nx][ny]) {
                        dist[nx][ny] = newCost;
                        parentX[nx][ny] = x;
                        parentY[nx][ny] = y;
                        pq.add(new Node(nx, ny, newCost));
                    }
                }
            }
        }

        List<int[]> path = new ArrayList<>();
        int x = ex, y = ey;
        while (x != -1 && y != -1) {
            path.add(0, new int[]{x, y});
            if (x == sx && y == sy) break;
            int px = parentX[x][y];
            int py = parentY[x][y];
            x = px; y = py;
        }
        return path;
    }

    public static List<int[]> getDijkstra2Path(String[][] maze, int[][] weight, int row, int col, int sx, int sy, int ex, int ey, int[][] key, int keys) {
        int[][] sortedKeys = sorting(key, keys, sx, sy);
        int[][] keynode = new int[keys + 2][2];
        keynode[0][0] = sx; keynode[0][1] = sy;
        for (int i = 0; i < keys; i++) {
            keynode[i + 1][0] = sortedKeys[i][0];
            keynode[i + 1][1] = sortedKeys[i][1];
        }
        keynode[keys + 1][0] = ex; keynode[keys + 1][1] = ey;

        List<int[]> fullPath = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int k = 0; k < keynode.length - 1; k++) {
            int fromX = keynode[k][0], fromY = keynode[k][1];
            int toX = keynode[k + 1][0], toY = keynode[k + 1][1];

            int[][] dist = new int[row][col];
            int[][] parentX = new int[row][col];
            int[][] parentY = new int[row][col];
            boolean[][] visited = new boolean[row][col];

            for (int i = 0; i < row; i++) {
                Arrays.fill(dist[i], Integer.MAX_VALUE);
                Arrays.fill(parentX[i], -1);
                Arrays.fill(parentY[i], -1);
            }
            PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
            dist[fromX][fromY] = 0;
            pq.add(new Node(fromX, fromY, 0));

            while (!pq.isEmpty()) {
                Node cur = pq.poll();
                if (visited[cur.x][cur.y]) continue;
                visited[cur.x][cur.y] = true;
                if (cur.x == toX && cur.y == toY) break;

                for (int d = 0; d < 4; d++) {
                    int nx = cur.x + dx[d], ny = cur.y + dy[d];
                    if (isValid2(nx, ny, maze, row, col) && !visited[nx][ny]) {
                        int w = weight[nx][ny];
                        if (w < 0) w = 0;
                        int newCost = cur.cost + w;
                        if (newCost < dist[nx][ny]) {
                            dist[nx][ny] = newCost;
                            parentX[nx][ny] = cur.x;
                            parentY[nx][ny] = cur.y;
                            pq.add(new Node(nx, ny, newCost));
                        }
                    }
                }
            }

            List<int[]> segment = new ArrayList<>();
            int x = toX, y = toY;
            while (x != -1 && y != -1) {
                segment.add(0, new int[]{x, y});
                if (x == fromX && y == fromY) break;
                int px = parentX[x][y];
                int py = parentY[x][y];
                x = px; y = py;
            }
            if (k > 0 && !segment.isEmpty()) segment.remove(0);
            fullPath.addAll(segment);
        }
        return fullPath;
    }
}

