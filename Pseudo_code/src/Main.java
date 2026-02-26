import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    private static GameState gameState;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", Main::handleRoot);
        server.createContext("/api/new-game", Main::handleNewGame);
        server.createContext("/api/move", Main::handleMove);
        server.createContext("/api/solve", Main::handleSolve);
        server.createContext("/api/state", Main::handleState);
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        String html = getIndexHtml();
        sendResponse(exchange, 200, html, "text/html");
    }

    private static void handleNewGame(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int size = 15;
        if (query != null && query.startsWith("size=")) {
            size = Integer.parseInt(query.substring(5));
        }
        
        int[][] maze = MazeGenerator.generate(size);
        int[][] weights = MazeGenerator.generateWeights(maze);
        gameState = new GameState(maze, weights);
        
        String json = GameAPI.gameStateToJson(gameState);
        sendResponse(exchange, 200, json, "application/json");
    }

    private static void handleMove(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
            return;
        }
        
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String direction = body.replaceAll(".*\"direction\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        
        if (gameState != null) {
            gameState.movePlayer(direction);
        }
        
        String json = GameAPI.gameStateToJson(gameState);
        sendResponse(exchange, 200, json, "application/json");
    }

    private static void handleSolve(HttpExchange exchange) throws IOException {
        if (gameState != null) {
            gameState.solveAll();
        }
        String json = GameAPI.gameStateToJson(gameState);
        sendResponse(exchange, 200, json, "application/json");
    }

    private static void handleState(HttpExchange exchange) throws IOException {
        String json = gameState != null ? GameAPI.gameStateToJson(gameState) : "{}";
        sendResponse(exchange, 200, json, "application/json");
    }

    private static void sendResponse(HttpExchange exchange, int code, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String getIndexHtml() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Intelligent Maze Game</title><style>" +
"*{margin:0;padding:0;box-sizing:border-box}body{font-family:'Segoe UI',sans-serif;background:#0f0f1a;color:#fff;display:flex;justify-content:center;align-items:center;min-height:100vh;padding:20px}.container{display:flex;gap:30px;max-width:1200px}.canvas-container{background:#1a1a2e;padding:20px;border-radius:10px;box-shadow:0 8px 32px rgba(0,0,0,0.3)}canvas{border:2px solid #16213e;border-radius:5px}.control-panel{background:#1a1a2e;padding:25px;border-radius:10px;width:350px;box-shadow:0 8px 32px rgba(0,0,0,0.3)}.btn{width:100%;padding:12px;margin:8px 0;border:none;border-radius:5px;font-size:14px;cursor:pointer;transition:all 0.3s;font-weight:600}.btn-primary{background:#e94560;color:#fff}.btn-primary:hover{background:#d63651;transform:translateY(-2px)}.btn-secondary{background:#00b4d8;color:#fff}.btn-secondary:hover{background:#0096b8}.size-selector{display:flex;gap:8px;margin:10px 0}.size-btn{flex:1;padding:8px;background:#16213e;color:#fff;border:2px solid #0f3460;border-radius:5px;cursor:pointer;transition:all 0.3s}.size-btn.active{background:#0f3460;border-color:#00b4d8}.stats{margin:20px 0;padding:15px;background:#16213e;border-radius:5px}.stat-row{display:flex;justify-content:space-between;margin:8px 0;font-size:14px}.legend{margin:15px 0}.legend-item{display:flex;align-items:center;margin:8px 0;font-size:13px}.legend-color{width:20px;height:20px;border-radius:3px;margin-right:10px}.toggle-btns{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin:15px 0}.toggle-btn{padding:8px;background:#16213e;border:2px solid #0f3460;color:#fff;border-radius:5px;cursor:pointer;font-size:12px;transition:all 0.3s}.toggle-btn.active{border-color:#00b4d8;background:#0f3460}.ranking{margin-top:20px;padding:15px;background:#16213e;border-radius:5px}.ranking-row{display:flex;justify-content:space-between;padding:8px;margin:5px 0;background:#1a1a2e;border-radius:3px;font-size:13px}h2{margin:15px 0 10px;font-size:18px;color:#00b4d8}h3{margin:10px 0;font-size:14px;color:#f5a623}.instructions{font-size:12px;color:#aaa;margin-top:15px;line-height:1.6}</style></head><body>" +
"<div class='container'><div class='canvas-container'><canvas id='mazeCanvas' width='600' height='600'></canvas></div>" +
"<div class='control-panel'><h2>🎮 Maze Game</h2><button class='btn btn-primary' onclick='newGame()'>New Game</button>" +
"<div class='size-selector'><button class='size-btn active' onclick='selectSize(11)'>11x11</button>" +
"<button class='size-btn' onclick='selectSize(15)'>15x15</button><button class='size-btn' onclick='selectSize(21)'>21x21</button></div>" +
"<button class='btn btn-secondary' onclick='solveAll()'>Solve All</button><h3>Algorithm Paths</h3>" +
"<div class='toggle-btns'><button class='toggle-btn' id='bfsToggle' onclick='togglePath(\"bfs\")'>BFS (Shortest)</button>" +
"<button class='toggle-btn' id='dfsToggle' onclick='togglePath(\"dfs\")'>DFS (Longest)</button>" +
"<button class='toggle-btn' id='dijkstraToggle' onclick='togglePath(\"dijkstra\")'>Dijkstra (Min Cost)</button></div>" +
"<div class='stats'><h3>📊 Stats</h3><div class='stat-row'><span>Steps:</span><span id='steps'>0</span></div>" +
"<div class='stat-row'><span>Score:</span><span id='score'>0</span></div>" +
"<div class='stat-row'><span>BFS Length:</span><span id='bfsLen'>-</span></div>" +
"<div class='stat-row'><span>Dijkstra Cost:</span><span id='dijkstraCost'>-</span></div></div>" +
"<div class='legend'><h3>🎨 Legend</h3><div class='legend-item'><div class='legend-color' style='background:#e94560'></div>Player</div>" +
"<div class='legend-item'><div class='legend-color' style='background:#0f3460'></div>Start</div>" +
"<div class='legend-item'><div class='legend-color' style='background:#f5a623'></div>Exit</div>" +
"<div class='legend-item'><div class='legend-color' style='background:#00b4d8'></div>BFS Path</div>" +
"<div class='legend-item'><div class='legend-color' style='background:#9b5de5'></div>DFS Path</div>" +
"<div class='legend-item'><div class='legend-color' style='background:#06d6a0'></div>Dijkstra Path</div></div>" +
"<div id='rankingPanel' class='ranking' style='display:none'><h3>🏆 Final Ranking</h3><div id='rankingContent'></div></div>" +
"<div class='instructions'><strong>Controls:</strong><br>Arrow Keys or WASD to move<br>Reach the orange exit to win!</div></div></div>" +
"<script>let gameState=null;let selectedSize=15;let showBfs=false;let showDfs=false;let showDijkstra=false;const canvas=document.getElementById('mazeCanvas');" +
"const ctx=canvas.getContext('2d');function selectSize(size){selectedSize=size;document.querySelectorAll('.size-btn').forEach(b=>b.classList.remove('active'));" +
"event.target.classList.add('active')}async function newGame(){const res=await fetch(`/api/new-game?size=${selectedSize}`);" +
"gameState=await res.json();showBfs=false;showDfs=false;showDijkstra=false;document.getElementById('rankingPanel').style.display='none';" +
"updateToggles();render()}async function solveAll(){const res=await fetch('/api/solve');gameState=await res.json();render()}" +
"async function move(dir){if(!gameState||gameState.gameOver)return;const res=await fetch('/api/move',{method:'POST',headers:{'Content-Type':'application/json'}," +
"body:JSON.stringify({direction:dir})});gameState=await res.json();render();if(gameState.gameOver){showRanking()}}" +
"function togglePath(type){if(type==='bfs'){showBfs=!showBfs;document.getElementById('bfsToggle').classList.toggle('active')}else if(type==='dfs'){showDfs=!showDfs;" +
"document.getElementById('dfsToggle').classList.toggle('active')}else{showDijkstra=!showDijkstra;document.getElementById('dijkstraToggle').classList.toggle('active')}render()}" +
"function updateToggles(){document.getElementById('bfsToggle').classList.remove('active');document.getElementById('dfsToggle').classList.remove('active');" +
"document.getElementById('dijkstraToggle').classList.remove('active')}function render(){if(!gameState)return;const maze=gameState.maze;const rows=maze.length;" +
"const cellSize=canvas.width/rows;ctx.clearRect(0,0,canvas.width,canvas.height);for(let r=0;r<rows;r++){for(let c=0;c<rows;c++){ctx.fillStyle=maze[r][c]===1?'#1a1a2e':'#16213e';" +
"ctx.fillRect(c*cellSize,r*cellSize,cellSize,cellSize)}}" +
"if(showDijkstra&&gameState.dijkstraPath){drawPath(gameState.dijkstraPath,'#06d6a0',cellSize)}if(showDfs&&gameState.dfsPath){drawPath(gameState.dfsPath,'#9b5de5',cellSize)}" +
"if(showBfs&&gameState.bfsPath){drawPath(gameState.bfsPath,'#00b4d8',cellSize)}if(gameState.playerPath&&gameState.playerPath.length>1){drawPath(gameState.playerPath,'rgba(233,69,96,0.3)',cellSize)}" +
"const start=gameState.start;ctx.fillStyle='#0f3460';ctx.fillRect(start[1]*cellSize+2,start[0]*cellSize+2,cellSize-4,cellSize-4);" +
"const exit=gameState.exit;ctx.fillStyle='#f5a623';ctx.fillRect(exit[1]*cellSize+2,exit[0]*cellSize+2,cellSize-4,cellSize-4);" +
"const pos=gameState.playerPos;ctx.fillStyle='#e94560';ctx.beginPath();ctx.arc(pos[1]*cellSize+cellSize/2,pos[0]*cellSize+cellSize/2,cellSize/3,0,Math.PI*2);" +
"ctx.fill();document.getElementById('steps').textContent=gameState.playerSteps;document.getElementById('score').textContent=gameState.playerScore;" +
"document.getElementById('bfsLen').textContent=gameState.bfsPath?gameState.bfsPath.length:'-';" +
"document.getElementById('dijkstraCost').textContent=gameState.dijkstraCost>=0?gameState.dijkstraCost:'-'}" +
"function drawPath(path,color,cellSize){ctx.fillStyle=color;ctx.globalAlpha=0.4;path.forEach(p=>{ctx.fillRect(p[1]*cellSize+4,p[0]*cellSize+4,cellSize-8,cellSize-8)});ctx.globalAlpha=1}" +
"function showRanking(){const panel=document.getElementById('rankingPanel');const content=document.getElementById('rankingContent');" +
"const r=gameState.ranking;content.innerHTML=`<div class='ranking-row'><span>Your Steps:</span><span>${r.playerSteps}</span></div>" +
"<div class='ranking-row'><span>BFS Steps:</span><span>${r.bfsSteps}</span></div><div class='ranking-row'><span>Rating:</span><span>${r.rating}</span></div>`;" +
"panel.style.display='block'}document.addEventListener('keydown',e=>{const key=e.key.toUpperCase();const map={'ARROWUP':'UP','ARROWDOWN':'DOWN','ARROWLEFT':'LEFT'," +
"'ARROWRIGHT':'RIGHT','W':'UP','S':'DOWN','A':'LEFT','D':'RIGHT'};if(map[key]){e.preventDefault();move(map[key])}});newGame()</script></body></html>";
    }
}
