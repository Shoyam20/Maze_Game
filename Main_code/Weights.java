import java.util.*;

public class Weights {

    public static int[][] weights(String[][] maze,int rows , int cols) {

        int[][] weight = new int[rows][cols];

        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isPath(maze[i][j])) {
                    weight[i][j] = rand.nextInt(10); 
                } else {
                    weight[i][j] = -1;
                }
            }
        }

        return weight;
    }

    private static boolean isPath(String cell) {
        if(cell.equals(" . ") || cell.equals(" S ") || cell.equals(" E ") ||cell.equals(" K ") || cell.equals(" D "))
        {
            return true;
        }
        else{
            return false;
        }
    }

}