# Maze Game Frontend

This is a repo-ready React + Vite frontend for the `Maze_Game` Java project.

## Features

- Route-based frontend structure
- `App.jsx` included
- Playable maze UI
- Java backend API integration layer
- Local fallback mode if backend is offline
- Sample backend controller for integration

## Project structure

```text
maze-frontend/
├── backend-integration/
│   └── MazeControllerExample.java
├── public/
├── src/
│   ├── components/
│   │   ├── ControlPad.jsx
│   │   ├── MazeGrid.jsx
│   │   └── StatCard.jsx
│   ├── hooks/
│   │   └── useMazeGame.js
│   ├── pages/
│   │   ├── ApiDocsPage.jsx
│   │   ├── HomePage.jsx
│   │   └── PlayPage.jsx
│   ├── services/
│   │   ├── localMazeEngine.js
│   │   └── mazeApi.js
│   ├── App.jsx
│   ├── main.jsx
│   └── styles.css
├── index.html
├── package.json
└── vite.config.js
```

## Run frontend

```bash
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`

## Backend integration

The frontend expects these endpoints:

- `GET /api/health`
- `POST /api/maze/generate`
- `POST /api/maze/move`
- `POST /api/maze/solve`

Vite proxy is already configured to forward `/api/*` to `http://localhost:8080`.

## How to connect to your Java project

Your current repository is Java-only and centered around `Main_code/test1.java`. The frontend in this zip is designed to connect once you expose your maze generation, movement validation, and BFS solving logic through REST endpoints.

A starter sample controller is included in `backend-integration/MazeControllerExample.java`.

## Suggested branch commands

```bash
git checkout -b frontend-ui
cp -r maze-frontend/* .
git add .
git commit -m "Add React frontend and Java API integration scaffold"
git push origin frontend-ui
```
