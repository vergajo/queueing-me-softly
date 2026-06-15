import { useState, useEffect, useCallback } from 'react'
import './App.css'

const API_BASE = '/api'

function App() {
  const [dashboard, setDashboard] = useState(null)
  const [playerName, setPlayerName] = useState('')
  const [skillLevel, setSkillLevel] = useState('INTERMEDIATE')

  const fetchDashboard = useCallback(async () => {
    const res = await fetch(`${API_BASE}/dashboard`)
    const data = await res.json()
    setDashboard(data)
  }, [])

  useEffect(() => {
    fetchDashboard()
    const interval = setInterval(fetchDashboard, 30000)
    return () => clearInterval(interval)
  }, [fetchDashboard])

  const addPlayer = async (e) => {
    e.preventDefault()
    if (!playerName.trim()) return
    await fetch(`${API_BASE}/players`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: playerName, skillLevel }),
    })
    setPlayerName('')
    fetchDashboard()
  }

  const removePlayer = async (id) => {
    await fetch(`${API_BASE}/players/${id}`, { method: 'DELETE' })
    fetchDashboard()
  }

  const joinQueue = async (id) => {
    await fetch(`${API_BASE}/players/${id}/join-queue`, { method: 'POST' })
    fetchDashboard()
  }

  const leaveQueue = async (id) => {
    await fetch(`${API_BASE}/players/${id}/leave-queue`, { method: 'POST' })
    fetchDashboard()
  }

  const generateMatch = async (courtNumber) => {
    await fetch(`${API_BASE}/courts/${courtNumber}/generate-match`, { method: 'POST' })
    fetchDashboard()
  }

  const startMatch = async (courtNumber) => {
    await fetch(`${API_BASE}/courts/${courtNumber}/start`, { method: 'POST' })
    fetchDashboard()
  }

  const endMatch = async (courtNumber) => {
    await fetch(`${API_BASE}/courts/${courtNumber}/end`, { method: 'POST' })
    fetchDashboard()
  }

  const autoAssign = async () => {
    await fetch(`${API_BASE}/courts/auto-assign`, { method: 'POST' })
    fetchDashboard()
  }

  if (!dashboard) return <div className="loading">Loading...</div>

  return (
    <div className="app">
      <header className="header">
        <h1>🏸 Queueing Me Softly</h1>
        <div className="stat-badges">
          <span className="badge badge-blue">👥 {dashboard.allPlayers.length} Players</span>
          <span className="badge badge-green">⏳ {dashboard.waitingQueue.length} Waiting</span>
          <span className="badge badge-orange">🏸 {dashboard.activeCourtCount} Active</span>
        </div>
      </header>

      <section className="add-player-section">
        <form onSubmit={addPlayer} className="add-player-form">
          <input
            type="text"
            value={playerName}
            onChange={(e) => setPlayerName(e.target.value)}
            placeholder="Player name..."
            required
            className="input-name"
          />
          <select
            value={skillLevel}
            onChange={(e) => setSkillLevel(e.target.value)}
            className="input-skill"
          >
            <option value="BEGINNER">BEGINNER</option>
            <option value="INTERMEDIATE">INTERMEDIATE</option>
            <option value="ADVANCED">ADVANCED</option>
            <option value="EXPERT">EXPERT</option>
          </select>
          <button type="submit" className="btn btn-primary">+ Add Player</button>
        </form>
      </section>

      <div className="dashboard-grid">
        {/* Waiting Queue */}
        <section className="panel panel-queue">
          <div className="panel-header">
            <h2>⏳ Waiting Queue</h2>
            <span className="count-badge">{dashboard.waitingQueue.length}</span>
          </div>
          <div className="player-list">
            {dashboard.waitingQueue.length === 0 ? (
              <div className="empty-state">
                <p>No players waiting</p>
                <p className="empty-hint">Add players above to get started</p>
              </div>
            ) : (
              dashboard.waitingQueue.map((player, idx) => (
                <div
                  key={player.id}
                  className={`player-card ${idx === 0 ? 'player-card-longest' : player.gamesPlayed > 0 ? 'player-card-recent' : ''}`}
                >
                  <div className="player-info">
                    <span className="player-name">{player.name}</span>
                    <div className="player-meta">
                      <span className="meta-item">🎮 {player.gamesPlayed}</span>
                      <span className="meta-item">⏱ {player.waitTime}</span>
                      <span className={`skill-badge skill-${player.skillLevel.toLowerCase()}`}>
                        {player.skillLevel}
                      </span>
                    </div>
                  </div>
                  <div className="player-actions">
                    <button onClick={() => leaveQueue(player.id)} className="btn btn-sm btn-ghost" title="Leave queue">✕</button>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>

        {/* Courts */}
        <section className="panel panel-courts">
          <div className="panel-header">
            <h2>🏸 Courts</h2>
            <button onClick={autoAssign} className="btn btn-secondary">⚡ Auto Assign</button>
          </div>
          <div className="courts-grid">
            {dashboard.courts.map((court) => (
              <div
                key={court.id}
                className={`court-card ${court.status === 'IN_PLAY' ? 'court-active' : 'court-available'}`}
              >
                <div className="court-header">
                  <h3>Court {court.courtNumber}</h3>
                  <span className={`court-status status-${court.status.toLowerCase()}`}>{court.status}</span>
                </div>

                {court.status === 'IN_PLAY' && (
                  <div className="court-timer">
                    <span className="timer-icon">⏱</span>
                    <span className="timer-text">In Progress</span>
                  </div>
                )}

                {(court.teamA.length > 0 || court.teamB.length > 0) ? (
                  <div className="court-teams">
                    <div className="team team-a">
                      <span className="team-label">Team A</span>
                      {court.teamA.map((p) => (
                        <div key={p.id} className="team-player">{p.name}</div>
                      ))}
                    </div>
                    <div className="team-vs">VS</div>
                    <div className="team team-b">
                      <span className="team-label">Team B</span>
                      {court.teamB.map((p) => (
                        <div key={p.id} className="team-player">{p.name}</div>
                      ))}
                    </div>
                  </div>
                ) : (
                  <div className="court-empty"><p>No match assigned</p></div>
                )}

                <div className="court-actions">
                  {court.status === 'AVAILABLE' && court.teamA.length === 0 && (
                    <button onClick={() => generateMatch(court.courtNumber)} className="btn btn-primary btn-large">🎲 Generate Match</button>
                  )}
                  {court.status === 'AVAILABLE' && court.teamA.length > 0 && (
                    <button onClick={() => startMatch(court.courtNumber)} className="btn btn-success btn-large">▶ Start Match</button>
                  )}
                  {court.status === 'IN_PLAY' && (
                    <button onClick={() => endMatch(court.courtNumber)} className="btn btn-danger btn-large">⏹ End Match</button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Stats */}
        <section className="panel panel-stats">
          <div className="panel-header">
            <h2>📊 Statistics</h2>
          </div>
          <div className="stats-grid">
            <div className="stat-card">
              <span className="stat-value">{dashboard.allPlayers.length}</span>
              <span className="stat-label">Total Players</span>
            </div>
            <div className="stat-card">
              <span className="stat-value">{dashboard.gamesCompleted}</span>
              <span className="stat-label">Games Completed</span>
            </div>
            <div className="stat-card">
              <span className="stat-value">{dashboard.averageWaitTime}</span>
              <span className="stat-label">Avg Wait Time</span>
            </div>
          </div>

          {dashboard.leaderboard.length > 0 && (
            <div className="leaderboard">
              <h3>🏆 Leaderboard</h3>
              {dashboard.leaderboard.map((player, idx) => (
                <div key={player.id} className="leaderboard-item">
                  <span className="leaderboard-rank">{idx + 1}</span>
                  <span className="leaderboard-name">{player.name}</span>
                  <span className="leaderboard-games">{player.gamesPlayed} games</span>
                </div>
              ))}
            </div>
          )}

          {dashboard.restingPlayers.length > 0 && (
            <div className="resting-players">
              <h3>😴 Resting</h3>
              {dashboard.restingPlayers.map((player) => (
                <div key={player.id} className="resting-item">
                  <span>{player.name}</span>
                  <button onClick={() => joinQueue(player.id)} className="btn btn-sm btn-primary">+ Queue</button>
                  <button onClick={() => removePlayer(player.id)} className="btn btn-sm btn-ghost">✕</button>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  )
}

export default App

