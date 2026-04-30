import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;public class Leaderboard {

    static String filename = "leaderboard.txt";
    static String[][] currentMaze;
    static int[][] currentWeights;
    static int[][] currentKeys;
    static int currentRow, currentCol, currentMode;

    static class Entry {
        String id;
        String name;
        String mode;
        int mazeSize;
        int levels;
        int playerSteps;
        int bfsSteps;
        double ratio;
        String date;
        int score;

        Entry(String i, String n, String m, int s, int l, int p, int b, double r, String d, int sc) {
            id = i;
            name = n;
            mode = m;
            mazeSize = s;
            levels = l;
            playerSteps = p;
            bfsSteps = b;
            ratio = r;
            date = d;
            score = sc;
        }

        Entry(String i, String n, String m, int s, int l, int p, int b, double r, String d) {
            this(i, n, m, s, l, p, b, r, d, 0);
        }

        String toLine() {
            return id + "," + name + "," + mode + "," + mazeSize + "," + levels + "," + playerSteps + "," + bfsSteps + "," + ratio + "," + date + "," + score;
        }
    }

    // merge sort function 
    private static List<Entry> mergeSort(List<Entry> list) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        List<Entry> left = new ArrayList<>();
        List<Entry> right = new ArrayList<>();

        for (int i = 0; i < mid; i++) left.add(list.get(i));
        for (int i = mid; i < list.size(); i++) right.add(list.get(i));

        left = mergeSort(left);
        right = mergeSort(right);

        List<Entry> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            boolean pickLeft = false;
            // compare scores then ratios
            if (left.get(i).score != right.get(j).score) {
                if (left.get(i).score > right.get(j).score) pickLeft = true;
            } else if (Math.abs(left.get(i).ratio - right.get(j).ratio) > 0.0001) {
                if (left.get(i).ratio > right.get(j).ratio) pickLeft = true;
            } else {
                if (left.get(i).playerSteps < right.get(j).playerSteps) pickLeft = true;
            }

            if (pickLeft) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));

        return result;
    }

    // comparator for priority queue
    static class MyComp implements Comparator<Entry> {
        public int compare(Entry a, Entry b) {
            if (a.score != b.score) {
                return Integer.compare(a.score, b.score);
            }
            if (Math.abs(a.ratio - b.ratio) > 0.0001) {
                if (a.ratio < b.ratio) return -1;
                return 1;
            }
            if (a.playerSteps > b.playerSteps) return -1;
            if (a.playerSteps < b.playerSteps) return 1;
            return 0;
        }
    }

    // hashmap for names
    public static HashMap<String, String> getNameMap(List<Entry> all) {
        HashMap<String, String> map = new HashMap<>();
        for(int i=0; i<all.size(); i++) {
            map.put(all.get(i).id, all.get(i).name);
        }
        return map;
    }

    // load file
    public static List<Entry> loadFile() {
        List<Entry> list = new ArrayList<>();
        try {
            File f = new File(filename);
            if (!f.exists()) return list;

            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] p = line.split(",");
                if (p.length >= 9) {
                    Entry e = new Entry(
                        p[0], p[1], p[2], 
                        Integer.parseInt(p[3]), 
                        Integer.parseInt(p[4]), 
                        Integer.parseInt(p[5]), 
                        Integer.parseInt(p[6]), 
                        Double.parseDouble(p[7]), 
                        p[8],
                        p.length >= 10 ? Integer.parseInt(p[9]) : 0
                    );
                    list.add(e);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading file");
        }
        return list;
    }

    public static void save(String mode, int size, int levels, int pSteps, int bSteps) {
        saveScore(mode, size, levels, pSteps, bSteps);
    }

    public static void saveScore(String mode, int size, int levels, int pSteps, int bSteps) {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("--- SAVE SCORE ---");

        List<Entry> all = loadFile();
        HashMap<String, String> nameMap = getNameMap(all);
        
        System.out.print("Enter your ID: ");
        String id = sc.nextLine();

        String name = nameMap.get(id);

        if (name != null) {
            System.out.println("Welcome back " + name + "!");
        } else {
            System.out.print("New player! Enter name: ");
            name = sc.nextLine();
        }

        double ratio = 0;
        if (pSteps > 0) ratio = (double)bSteps / pSteps;
        
        String date = LocalDate.now().toString();

        Entry newEntry = new Entry(id, name, mode, size, levels, pSteps, bSteps, ratio, date);
        all.add(newEntry);

        all = mergeSort(all);

        // write back to file
        try {
            FileWriter fw = new FileWriter(filename);
            for (int i=0; i<all.size(); i++) {
                fw.write(all.get(i).toLine() + "\n");
            }
            fw.close();
            System.out.println("Saved!");
        } catch(Exception e) {
            System.out.println("Error saving");
        }

        displayAll();
        userMenu(id, name, sc);
    }

    public static void userMenu(String id, String name, Scanner sc) {
        while (true) {
            System.out.println("\nMenu for " + name);
            System.out.println("1. Personal best");
            System.out.println("2. Full history");
            System.out.println("3. Compare rank");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            int ch = -1;
            try {
                ch = Integer.parseInt(sc.nextLine());
            } catch(Exception e) {}

            if (ch == 1) {
                // personal best using priority queue
                List<Entry> all = loadFile();
                PriorityQueue<Entry> pq = new PriorityQueue<>(new MyComp());
                
                for(int i=0; i<all.size(); i++) {
                    if(all.get(i).id.equals(id)) {
                        pq.add(all.get(i));
                        if(pq.size() > 1) pq.poll();
                    }
                }
                
                if(pq.isEmpty()) {
                    System.out.println("No records found.");
                } else {
                    Entry best = pq.poll();
                    System.out.println("Best Ratio: " + best.ratio);
                    System.out.println("Steps: " + best.playerSteps);
                    System.out.println("Date: " + best.date);
                }
            } 
            else if (ch == 2) {
                // history
                List<Entry> all = loadFile();
                List<Entry> mine = new ArrayList<>();
                for(int i=0; i<all.size(); i++) {
                    if(all.get(i).id.equals(id)) {
                        mine.add(all.get(i));
                    }
                }
                mine = mergeSort(mine);
                System.out.println("History:");
                System.out.println("MODE\tSIZE\tSTEPS\tRATIO\tDATE");
                for(int i=0; i<mine.size(); i++) {
                    Entry e = mine.get(i);
                    System.out.println(e.mode + "\t" + e.mazeSize + "x" + e.mazeSize + "\t" + e.playerSteps + "\t" + e.ratio + "\t" + e.date);
                }
            }
            else if (ch == 3) {
                // rank using binary search
                List<Entry> all = loadFile();
                List<Entry> sorted = mergeSort(all);
                
                double bestRatio = -1;
                for(int i=0; i<sorted.size(); i++) {
                    if(sorted.get(i).id.equals(id)) {
                        if(sorted.get(i).ratio > bestRatio) {
                            bestRatio = sorted.get(i).ratio;
                        }
                    }
                }

                int rank = sorted.size();
                int low = 0;
                int high = sorted.size() - 1;
                while(low <= high) {
                    int mid = (low + high) / 2;
                    if(sorted.get(mid).ratio <= bestRatio) {
                        rank = mid;
                        high = mid - 1;
                    } else {
                        low = mid + 1;
                    }
                }
                
                System.out.println("Your rank is " + (rank + 1));
            }
            else if (ch == 0) {
                break;
            }
        }
    }

    public static void displayAll() {
        List<Entry> all = loadFile();
        
        PriorityQueue<Entry> pq = new PriorityQueue<>(new MyComp());
        for (int i=0; i<all.size(); i++) {
            pq.add(all.get(i));
            if(pq.size() > 10) {
                pq.poll();
            }
        }

        List<Entry> top = new ArrayList<>();
        while(!pq.isEmpty()) {
            top.add(pq.poll());
        }
        top = mergeSort(top);

        System.out.println("\n--- TOP 10 LEADERBOARD ---");
        System.out.println("RANK\tNAME\tMODE\tRATIO");
        
        for(int i=0; i<top.size(); i++) {
            Entry e = top.get(i);
            System.out.println((i+1) + "\t" + e.name + "\t" + e.mode + "\t" + e.ratio);
        }
        System.out.println();
    }

    public static void viewFromMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("1. View Leaderboard");
        System.out.println("2. Profile");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        int choice = -1;
        try{
            choice = Integer.parseInt(sc.nextLine());
        } catch(Exception e){}

        if (choice == 1) {
            displayAll();
        } else if (choice == 2) {
            System.out.print("Enter ID: ");
            String id = sc.nextLine();
            List<Entry> all = loadFile();
            HashMap<String, String> map = getNameMap(all);
            
            String name = map.get(id);
            if(name == null) {
                System.out.println("Not found");
            } else {
                userMenu(id, name, sc);
            }
        }
    }

    // ── HTTP SERVER INTEGRATION ─────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        startServer(8080);
    }

    public static HttpServer startServer(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        setupRoutes(server);
        server.start();
        System.out.println("Leaderboard Server started on port " + port);
        return server;
    }

    public static void setupRoutes(HttpServer server) {
        server.createContext("/login", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                addCors(exchange);
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                String name = "unknown";
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("name=")) {
                    name = query.split("name=")[1].split("&")[0];
                }
                try { name = java.net.URLDecoder.decode(name, "UTF-8"); } catch(Exception ex) {}
                String response = "{\"status\":\"ok\", \"name\":\"" + name + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/leaderboard", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                addCors(exchange);
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                List<Entry> all = loadFile();
                PriorityQueue<Entry> pq = new PriorityQueue<>(new MyComp());
                for (int i=0; i<all.size(); i++) {
                    pq.add(all.get(i));
                    if (pq.size() > 10) pq.poll();
                }
                List<Entry> top = new ArrayList<>();
                while (!pq.isEmpty()) top.add(pq.poll());
                top = mergeSort(top);

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (int i = 0; i < top.size(); i++) {
                    Entry e = top.get(i);
                    sb.append("{\"name\":\"").append(e.name)
                      .append("\",\"score\":").append(e.score)
                      .append(",\"level\":").append(e.levels).append("}");
                    if (i < top.size() - 1) sb.append(",");
                }
                sb.append("]");
                String response = sb.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/submit-score", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                addCors(exchange);
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                String query = exchange.getRequestURI().getQuery();
                String name = "unknown";
                int score = 0;
                int level = 1;
                if (query != null) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("name=")) name = param.substring(5);
                        else if (param.startsWith("score=")) score = Integer.parseInt(param.substring(6));
                        else if (param.startsWith("level=")) level = Integer.parseInt(param.substring(6));
                    }
                }
                try { name = java.net.URLDecoder.decode(name, "UTF-8"); } catch(Exception ex) {}
                String id = UUID.randomUUID().toString().substring(0, 8);
                String date = LocalDate.now().toString();
                Entry newEntry = new Entry(id, name, "Frontend", 0, level, 0, 0, 0.0, date, score);
                
                List<Entry> all = loadFile();
                all.add(newEntry);
                all = mergeSort(all);
                
                try {
                    FileWriter fw = new FileWriter(filename);
                    for (int i=0; i<all.size(); i++) {
                        fw.write(all.get(i).toLine() + "\n");
                    }
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                String response = "{\"status\":\"ok\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/generate", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                addCors(exchange);
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                String query = exchange.getRequestURI().getQuery();
                int size = 21;
                int mode = 1;
                double prob = 0.3;
                if (query != null) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("size=")) size = Integer.parseInt(param.substring(5));
                        else if (param.startsWith("mode=")) mode = Integer.parseInt(param.substring(5));
                        else if (param.startsWith("prob=")) prob = Double.parseDouble(param.substring(5));
                    }
                }
                if (size % 2 == 0) size++;
                currentRow = size; currentCol = size; currentMode = mode;

                currentMaze = new String[currentRow][currentCol];
                for (int i = 0; i < currentRow; i++)
                    for (int j = 0; j < currentCol; j++)
                        currentMaze[i][j] = " X ";

                DFS dfs = new DFS();
                dfs.DFS(currentMaze, 1, 1, currentRow, currentCol);

                currentMaze[1][1] = " S ";
                if (mode == 1) {
                    currentMaze[currentRow - 2][currentCol - 2] = " E ";
                } else {
                    currentMaze[currentRow - 2][currentCol - 2] = " D ";
                }

                multi_path mp = new multi_path();
                mp.Multi_path(currentMaze, currentRow, currentCol, prob);

                if (mode == 2) {
                    int numKeys = size / 7;
                    if (numKeys < 1) numKeys = 1;
                    currentKeys = new int[numKeys][2];
                    Random rand = new Random();
                    int count = 0;
                    while (count < numKeys) {
                        int x = rand.nextInt(currentRow - 2) + 1;
                        int y = rand.nextInt(currentCol - 2) + 1;
                        if (currentMaze[x][y].equals(" . ")) {
                            currentMaze[x][y] = " K ";
                            currentKeys[count][0] = x;
                            currentKeys[count][1] = y;
                            count++;
                        }
                    }
                }

                currentWeights = Weights.weights(currentMaze, currentRow, currentCol);

                StringBuilder sb = new StringBuilder();
                sb.append("{\"grid\":[");
                for (int i = 0; i < currentRow; i++) {
                    sb.append("[");
                    for (int j = 0; j < currentCol; j++) {
                        sb.append("\"").append(currentMaze[i][j].trim()).append("\"");
                        if (j < currentCol - 1) sb.append(",");
                    }
                    sb.append("]");
                    if (i < currentRow - 1) sb.append(",");
                }
                sb.append("], \"weights\":[");
                for (int i = 0; i < currentRow; i++) {
                    sb.append("[");
                    for (int j = 0; j < currentCol; j++) {
                        sb.append(currentWeights[i][j]);
                        if (j < currentCol - 1) sb.append(",");
                    }
                    sb.append("]");
                    if (i < currentRow - 1) sb.append(",");
                }
                sb.append("], \"numKeys\":").append(mode == 2 ? currentKeys.length : 0).append("}");

                String response = sb.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/solve-bfs", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                addCors(exchange);
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                List<int[]> path;
                if (currentMode == 1) {
                    path = BFS.getBFSPath(currentMaze, currentRow, currentCol);
                } else {
                    path = BFS.getBFS2Path(currentMaze, currentRow, currentCol, currentKeys, currentKeys.length);
                }
                String response = pathToJson(path);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/solve-dijkstra", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                addCors(exchange);
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                List<int[]> path;
                if (currentMode == 1) {
                    path = Dijkstra.getDijkstraPath(currentMaze, currentWeights, currentRow, currentCol, 1, 1, currentRow - 2, currentCol - 2);
                } else {
                    path = Dijkstra.getDijkstra2Path(currentMaze, currentWeights, currentRow, currentCol, 1, 1, currentRow - 2, currentCol - 2, currentKeys, currentKeys.length);
                }
                String response = pathToJson(path);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
    }

    private static String pathToJson(List<int[]> path) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < path.size(); i++) {
            sb.append("[").append(path.get(i)[0]).append(",").append(path.get(i)[1]).append("]");
            if (i < path.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }
}