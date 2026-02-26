import java.util.*;

public class GameAPI {
    
    public static String gameStateToJson(GameState state) {
        if (state == null) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder("{");
        
        // Maze
        json.append("\"maze\":").append(arrayToJson(state.maze)).append(",");
        
        // Weights
        json.append("\"weights\":").append(arrayToJson(state.weights)).append(",");
        
        // Player position
        json.append("\"playerPos\":").append(intArrayToJson(state.playerPos)).append(",");
        
        // Player path
        json.append("\"playerPath\":").append(pathToJson(state.playerPath)).append(",");
        
        // Start and exit
        json.append("\"start\":").append(intArrayToJson(state.start)).append(",");
        json.append("\"exit\":").append(intArrayToJson(state.exit)).append(",");
        
        // Game state
        json.append("\"gameOver\":").append(state.gameOver).append(",");
        json.append("\"playerSteps\":").append(state.playerSteps).append(",");
        json.append("\"playerScore\":").append(state.playerScore).append(",");
        
        // Solver paths
        json.append("\"bfsPath\":").append(pathToJson(state.bfsPath)).append(",");
        json.append("\"dfsPath\":").append(pathToJson(state.dfsPath)).append(",");
        json.append("\"dijkstraPath\":").append(pathToJson(state.dijkstraPath)).append(",");
        json.append("\"dijkstraCost\":").append(state.dijkstraCost).append(",");
        
        // Ranking
        if (state.gameOver) {
            json.append("\"ranking\":").append(mapToJson(state.getRanking()));
        } else {
            json.append("\"ranking\":null");
        }
        
        json.append("}");
        return json.toString();
    }
    
    private static String arrayToJson(int[][] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append("[");
            for (int j = 0; j < array[i].length; j++) {
                sb.append(array[i][j]);
                if (j < array[i].length - 1) sb.append(",");
            }
            sb.append("]");
            if (i < array.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private static String intArrayToJson(int[] array) {
        if (array == null) return "null";
        return "[" + array[0] + "," + array[1] + "]";
    }
    
    private static String pathToJson(List<int[]> path) {
        if (path == null) return "null";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < path.size(); i++) {
            sb.append(intArrayToJson(path.get(i)));
            if (i < path.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int count = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (count > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            count++;
        }
        sb.append("}");
        return sb.toString();
    }
}
