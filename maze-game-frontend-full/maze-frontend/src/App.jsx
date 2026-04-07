import { NavLink, Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage';
import PlayPage from './pages/PlayPage';
import LeaderboardPage from './pages/LeaderboardPage';
export default function App() {
  return (
    <div className="website-wrapper">
      <header className="site-header">
        <div className="container header-inner">
          <div className="logo-area">
            <h1>Maze Game <span className="logo-dot"></span></h1>
          </div>
          <nav className="nav-links">
            <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>Home</NavLink>
            <NavLink to="/play" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>Play</NavLink>
            <NavLink to="/leaderboard" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>Leaderboard</NavLink>
          </nav>
        </div>
      </header>

      <main className="site-main">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/play" element={<PlayPage className="container" />} />
          <Route path="/leaderboard" element={<LeaderboardPage className="container" />} />
        </Routes>
      </main>

      <footer className="site-footer">
        <div className="container">
          <p>&copy; {new Date().getFullYear()} Maze Game Project. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
