import React, { useState, useEffect, useRef } from 'react';

export default function App() {
  const [matches, setMatches] = useState([]);
  const [activeMatchId, setActiveMatchId] = useState(1);
  const [matchFilter, setMatchFilter] = useState('ALL');
  const [activeTab, setActiveTab] = useState('commentary');
  const [matchData, setMatchData] = useState(null);
  const [commentary, setCommentary] = useState([]);
  const [commFilter, setCommFilter] = useState('ALL');
  const [scorecard, setScorecard] = useState(null);
  const [leaderboard, setLeaderboard] = useState(null);
  const [syncText, setSyncText] = useState('Live Sync Active');

  // Manual Scorer State
  const [selectedRuns, setSelectedRuns] = useState(0);
  const [isWicket, setIsWicket] = useState(false);
  const [wicketType, setWicketType] = useState('CAUGHT');
  const [isExtra, setIsExtra] = useState(false);
  const [extraType, setExtraType] = useState('WIDE');
  const [customComm, setCustomComm] = useState('');
  const [selectedSpeed, setSelectedSpeed] = useState(2000);

  const sseRef = useRef(null);

  // 1. Fetch matches list
  const fetchMatches = async (filter = matchFilter) => {
    try {
      const url = filter === 'ALL' ? '/api/matches' : `/api/matches?status=${filter}`;
      const res = await fetch(url);
      const data = await res.json();
      setMatches(data);
    } catch (err) {
      console.error('Error fetching matches:', err);
    }
  };

  // 2. Fetch match details
  const fetchMatchDetails = async (id = activeMatchId) => {
    try {
      const res = await fetch(`/api/matches/${id}`);
      if (res.ok) {
        const data = await res.json();
        setMatchData(data);
      }
    } catch (err) {
      console.error('Error fetching match details:', err);
    }
  };

  // 3. Fetch commentary
  const fetchCommentary = async (id = activeMatchId, filter = commFilter) => {
    try {
      const res = await fetch(`/api/matches/${id}/commentary?filter=${filter}&limit=25`);
      if (res.ok) {
        const data = await res.json();
        setCommentary(data);
      }
    } catch (err) {
      console.error('Error fetching commentary:', err);
    }
  };

  // 4. Fetch full scorecard
  const fetchScorecard = async (id = activeMatchId) => {
    try {
      const res = await fetch(`/api/matches/${id}/scorecard`);
      if (res.ok) {
        const data = await res.json();
        setScorecard(data);
      }
    } catch (err) {
      console.error('Error fetching scorecard:', err);
    }
  };

  // 5. Fetch leaderboard
  const fetchLeaderboard = async () => {
    try {
      const res = await fetch('/api/leaderboard');
      if (res.ok) {
        const data = await res.json();
        setLeaderboard(data);
      }
    } catch (err) {
      console.error('Error fetching leaderboard:', err);
    }
  };

  // Setup SSE
  useEffect(() => {
    if (sseRef.current) {
      sseRef.current.close();
    }

    try {
      const es = new EventSource(`/api/matches/${activeMatchId}/stream`);
      sseRef.current = es;

      es.addEventListener('connected', () => {
        setSyncText('SSE Live Stream Connected');
      });

      es.addEventListener('score-update', (e) => {
        const updated = JSON.parse(e.data);
        setMatchData(updated);
        fetchCommentary(activeMatchId, commFilter);
      });

      es.onerror = () => {
        setSyncText('Polling Fallback Sync');
      };
    } catch (err) {
      setSyncText('Polling Active');
    }

    return () => {
      if (sseRef.current) sseRef.current.close();
    };
  }, [activeMatchId, commFilter]);

  // Initial & interval sync
  useEffect(() => {
    fetchMatches(matchFilter);
    fetchMatchDetails(activeMatchId);
    fetchCommentary(activeMatchId, commFilter);
    fetchLeaderboard();

    const interval = setInterval(() => {
      fetchMatchDetails(activeMatchId);
      fetchCommentary(activeMatchId, commFilter);
    }, 2500);

    return () => clearInterval(interval);
  }, [activeMatchId, commFilter, matchFilter]);

  // Tab switch effect
  useEffect(() => {
    if (activeTab === 'scorecard') fetchScorecard(activeMatchId);
    if (activeTab === 'leaderboard') fetchLeaderboard();
  }, [activeTab, activeMatchId]);

  // Simulator actions
  const handleStartSim = async () => {
    await fetch(`/api/matches/${activeMatchId}/simulate/start`, { method: 'POST' });
    fetchMatchDetails(activeMatchId);
  };

  const handlePauseSim = async () => {
    await fetch(`/api/matches/${activeMatchId}/simulate/pause`, { method: 'POST' });
    fetchMatchDetails(activeMatchId);
  };

  const handleStepSim = async () => {
    const res = await fetch(`/api/matches/${activeMatchId}/simulate/step`, { method: 'POST' });
    if (res.ok) {
      const data = await res.json();
      setMatchData(data);
      fetchCommentary(activeMatchId, commFilter);
    }
  };

  const handleResetSim = async () => {
    if (!window.confirm('Reset this match to 0.0 overs?')) return;
    const res = await fetch(`/api/matches/${activeMatchId}/reset`, { method: 'POST' });
    if (res.ok) {
      const data = await res.json();
      setMatchData(data);
      fetchCommentary(activeMatchId, commFilter);
      fetchScorecard(activeMatchId);
    }
  };

  const handleSetSpeed = async (speed) => {
    setSelectedSpeed(speed);
    await fetch(`/api/matches/${activeMatchId}/simulate/speed?speedMs=${speed}`, { method: 'POST' });
  };

  // Submit manual ball
  const handleSubmitManualBall = async () => {
    const payload = {
      runs: selectedRuns,
      isWicket,
      wicketType: isWicket ? wicketType : null,
      isExtra,
      extraType: isExtra ? extraType : null,
      extraRuns: isExtra ? 1 : 0,
      customCommentary: customComm.trim()
    };

    try {
      const res = await fetch(`/api/matches/${activeMatchId}/ball`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        const data = await res.json();
        setMatchData(data);
        fetchCommentary(activeMatchId, commFilter);
        setCustomComm('');
        setIsWicket(false);
        setIsExtra(false);
      }
    } catch (err) {
      alert('Error recording delivery: ' + err.message);
    }
  };

  return (
    <div className="app-root">
      {/* Top Navbar */}
      <header className="navbar">
        <div className="nav-container">
          <div className="brand">
            <span className="brand-icon">🏏</span>
            <div className="brand-text">
              <span className="brand-title">CricPulse<span className="brand-pro">PRO</span></span>
              <span className="brand-subtitle">Scalable Real-Time Cricket Score Management Platform</span>
            </div>
          </div>
          <div className="header-right">
            <div className="sync-status">
              <span className="pulse-dot"></span>
              <span>{syncText}</span>
            </div>
            <button className="btn btn-outline-sm" onClick={() => {
              fetchMatchDetails(activeMatchId);
              fetchCommentary(activeMatchId, commFilter);
              fetchMatches(matchFilter);
            }}>🔄 Refresh</button>
          </div>
        </div>
      </header>

      {/* Match Ticker Strip */}
      <section className="ticker-strip">
        <div className="ticker-container">
          <div className="ticker-filter-group">
            {['ALL', 'LIVE', 'UPCOMING', 'COMPLETED'].map(f => (
              <button
                key={f}
                className={`filter-pill ${matchFilter === f ? 'active' : ''}`}
                onClick={() => { setMatchFilter(f); fetchMatches(f); }}
              >
                {f === 'ALL' ? 'All Matches' : f === 'LIVE' ? '🔴 Live' : f === 'UPCOMING' ? '⏳ Upcoming' : '✅ Completed'}
              </button>
            ))}
          </div>
          <div className="ticker-cards">
            {matches.map(m => (
              <div
                key={m.id}
                className={`match-ticker-card ${m.id === activeMatchId ? 'active-match' : ''}`}
                onClick={() => setActiveMatchId(m.id)}
              >
                <div className="card-top">
                  <span>{m.matchType || 'T20'} • {m.seriesName || 'Series'}</span>
                  <span className="card-status-pill">{m.status === 'LIVE' ? '🔴 LIVE' : m.status}</span>
                </div>
                <div className="card-teams">
                  <div className="card-team-row">
                    <span>{m.teamA?.flagEmoji} {m.teamA?.shortCode}</span>
                    <span>{m.id === 1 ? '192/5' : ''}</span>
                  </div>
                  <div className="card-team-row">
                    <span>{m.teamB?.flagEmoji} {m.teamB?.shortCode}</span>
                    <span>{m.id === 1 ? '154/3' : ''}</span>
                  </div>
                </div>
                <div className="card-status-line">
                  {m.resultSummary || (m.status === 'LIVE' ? 'Match In Progress' : 'Scheduled')}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Main Hero Scoreboard */}
      <main className="main-content">
        {matchData && (
          <section className="hero-scoreboard">
            <div className="hero-header">
              <div className="match-meta">
                <span className={matchData.status === 'LIVE' ? 'badge badge-live' : 'badge badge-sub'}>
                  {matchData.status}
                </span>
                <span className="series-name">{matchData.seriesName}</span>
                <span className="venue-info">{matchData.venue}</span>
              </div>
              <div className="match-title-row">
                <h1>{matchData.matchTitle}</h1>
                <span className="match-type-tag">{matchData.matchType} • {matchData.totalOvers} Overs</span>
              </div>
              <div className="toss-info">
                {matchData.tossWinner ? `Toss: ${matchData.tossWinner} opted to ${matchData.tossDecision}` : 'Toss pending'}
              </div>
            </div>

            <div className="score-display-card">
              <div className="score-main">
                <div className="team-block">
                  <div className="team-flag">{matchData.battingTeam?.flagEmoji || '🏏'}</div>
                  <div className="team-info">
                    <span className="team-name">{matchData.battingTeam?.name}</span>
                    <span className="team-sub">Batting (Innings {matchData.currentInnings})</span>
                  </div>
                </div>

                <div className="score-digits">
                  <div className="primary-score">
                    <span className="runs-wickets">{matchData.totalRuns}/{matchData.totalWickets}</span>
                    <span className="overs-indicator">({matchData.oversDisplay || '0.0'} ov)</span>
                  </div>
                  <div className="rates-row">
                    <span className="rate-item">CRR: <strong>{(matchData.currentRunRate || 0).toFixed(2)}</strong></span>
                    {matchData.currentInnings === 2 && matchData.target > 0 && (
                      <>
                        <span className="rate-item highlight">RRR: <strong>{(matchData.requiredRunRate || 0).toFixed(2)}</strong></span>
                        <span className="rate-item">Target: <strong>{matchData.target}</strong></span>
                      </>
                    )}
                  </div>
                </div>

                <div className="team-block bowling">
                  <div className="team-info text-right">
                    <span className="team-name">{matchData.bowlingTeam?.name}</span>
                    <span className="team-sub">Bowling</span>
                  </div>
                  <div className="team-flag">{matchData.bowlingTeam?.flagEmoji || '🏏'}</div>
                </div>
              </div>

              {matchData.targetEquation && (
                <div className="equation-banner">
                  <span>{matchData.targetEquation}</span>
                </div>
              )}

              <div className="innings-ribbon">
                <div className="ribbon-item">
                  <span className="ribbon-label">1st Innings:</span>
                  <span className="ribbon-val">{matchData.firstInningsScore || '--'}</span>
                </div>
                <div className="ribbon-item">
                  <span className="ribbon-label">Current:</span>
                  <span className="ribbon-val">{matchData.secondInningsScore || `${matchData.totalRuns}/${matchData.totalWickets}`}</span>
                </div>
              </div>

              {/* Action Matrix: Batters, Bowler, Over Balls */}
              <div className="action-matrix">
                {/* Batters */}
                <div className="matrix-card batters-card">
                  <div className="matrix-title">BATSMEN ON CREASE</div>
                  <div className="batter-row striker">
                    <div className="player-name-cell">
                      <span className="bat-icon">🏏</span>
                      <span className="p-name">{matchData.striker?.name || 'Batter 1'}</span>
                      <span className="badge-tag">STRIKER</span>
                    </div>
                    <div className="player-stats-cell">
                      <span className="runs-highlight">{matchData.striker?.runs || 0}*</span>
                      <span className="balls-sub">({matchData.striker?.balls || 0})</span>
                      <span className="boundary-sub">4s: {matchData.striker?.fours || 0} | 6s: {matchData.striker?.sixes || 0}</span>
                      <span className="sr-sub">SR: {(matchData.striker?.strikeRate || 0).toFixed(1)}</span>
                    </div>
                  </div>
                  <div className="batter-row non-striker">
                    <div className="player-name-cell">
                      <span className="dot-icon">•</span>
                      <span className="p-name">{matchData.nonStriker?.name || 'Batter 2'}</span>
                    </div>
                    <div className="player-stats-cell">
                      <span className="runs-highlight">{matchData.nonStriker?.runs || 0}*</span>
                      <span className="balls-sub">({matchData.nonStriker?.balls || 0})</span>
                      <span className="boundary-sub">4s: {matchData.nonStriker?.fours || 0} | 6s: {matchData.nonStriker?.sixes || 0}</span>
                      <span className="sr-sub">SR: {(matchData.nonStriker?.strikeRate || 0).toFixed(1)}</span>
                    </div>
                  </div>
                </div>

                {/* Bowler */}
                <div className="matrix-card bowler-card">
                  <div className="matrix-title">CURRENT BOWLER</div>
                  <div className="bowler-profile">
                    <div className="bowler-name-row">
                      <span className="ball-icon">⚾</span>
                      <span className="b-name">{matchData.currentBowler?.name || 'Bowler'}</span>
                      <span className="badge-tag bow-tag">BOWLING</span>
                    </div>
                    <div className="bowler-grid">
                      <div className="b-stat"><span className="lbl">OVERS</span><span className="val">{matchData.currentBowler?.overs || '0.0'}</span></div>
                      <div className="b-stat"><span className="lbl">MAIDENS</span><span className="val">{matchData.currentBowler?.maidens || 0}</span></div>
                      <div className="b-stat"><span className="lbl">RUNS</span><span className="val">{matchData.currentBowler?.runsConceded || 0}</span></div>
                      <div className="b-stat"><span className="lbl">WICKETS</span><span className="val highlight-w">{matchData.currentBowler?.wickets || 0}</span></div>
                      <div className="b-stat"><span className="lbl">ECONOMY</span><span className="val">{(matchData.currentBowler?.economy || 0).toFixed(2)}</span></div>
                    </div>
                  </div>
                </div>

                {/* Over Balls */}
                <div className="matrix-card over-balls-card">
                  <div className="matrix-title">THIS OVER DELIVERIES</div>
                  <div className="pills-container">
                    {matchData.currentOverBalls && matchData.currentOverBalls.length > 0 ? (
                      matchData.currentOverBalls.map((p, idx) => {
                        let cls = 'ball-single';
                        if (p.type === 'DOT') cls = 'ball-dot';
                        else if (p.type === 'FOUR') cls = 'ball-four';
                        else if (p.type === 'SIX') cls = 'ball-six';
                        else if (p.type === 'WICKET') cls = 'ball-wicket';
                        else if (p.type === 'EXTRA') cls = 'ball-extra';
                        return <span key={idx} className={`ball-pill ${cls}`}>{p.text}</span>;
                      })
                    ) : (
                      <span className="text-muted" style={{ fontSize: '0.8rem' }}>No deliveries bowled yet</span>
                    )}
                  </div>
                  <div className="over-summary-hint"><small>Live ball ribbon refreshes automatically</small></div>
                </div>
              </div>
            </div>
          </section>
        )}

        {/* Tabs Section */}
        <section className="details-section">
          <div className="tabs-header">
            <button className={`tab-btn ${activeTab === 'commentary' ? 'active' : ''}`} onClick={() => setActiveTab('commentary')}>
              🎙️ Live Commentary
            </button>
            <button className={`tab-btn ${activeTab === 'scorecard' ? 'active' : ''}`} onClick={() => setActiveTab('scorecard')}>
              📋 Detailed Scorecard
            </button>
            <button className={`tab-btn ${activeTab === 'analytics' ? 'active' : ''}`} onClick={() => setActiveTab('analytics')}>
              📊 Match Analytics
            </button>
            <button className={`tab-btn ${activeTab === 'leaderboard' ? 'active' : ''}`} onClick={() => setActiveTab('leaderboard')}>
              🏆 Tournament Leaderboard
            </button>
            <button className={`tab-btn accent-tab ${activeTab === 'simulation' ? 'active' : ''}`} onClick={() => setActiveTab('simulation')}>
              🎮 Live Simulator &amp; Scorer Console
            </button>
          </div>

          {/* Commentary Tab */}
          {activeTab === 'commentary' && (
            <div className="tab-pane active">
              <div className="pane-subbar">
                <span className="pane-title">Ball-by-Ball Live Broadcast Feed</span>
                <div className="filter-btn-group">
                  {['ALL', 'BOUNDARIES', 'WICKETS'].map(f => (
                    <button
                      key={f}
                      className={`btn-filter ${commFilter === f ? 'active' : ''}`}
                      onClick={() => { setCommFilter(f); fetchCommentary(activeMatchId, f); }}
                    >
                      {f === 'ALL' ? 'All Balls' : f === 'BOUNDARIES' ? '⚡ Boundaries' : '🔴 Wickets'}
                    </button>
                  ))}
                </div>
              </div>
              <div className="commentary-list">
                {commentary.map(c => {
                  let tagClass = 'tag-run';
                  if (c.eventTag === 'FOUR') tagClass = 'tag-four';
                  else if (c.eventTag === 'SIX') tagClass = 'tag-six';
                  else if (c.eventTag === 'WICKET') tagClass = 'tag-wicket';
                  else if (c.eventTag === 'EXTRA') tagClass = 'tag-extra';
                  else if (c.eventTag === 'DOT') tagClass = 'tag-dot';

                  return (
                    <div key={c.id} className="commentary-item">
                      <div className="comm-top-row">
                        <span className="comm-ball-badge">{c.ballDisplay}</span>
                        <span className="comm-players">{c.bowlerName} to {c.batsmanName}</span>
                        <span className={`comm-event-tag ${tagClass}`}>{c.eventTag}</span>
                      </div>
                      <div className="comm-text">{c.commentary}</div>
                      <div className="comm-footer">
                        <span>Score after ball: <strong>{c.scoreAfterBall}</strong></span>
                        <span>{new Date(c.timestamp).toLocaleTimeString()}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Scorecard Tab */}
          {activeTab === 'scorecard' && (
            <div className="tab-pane active">
              {scorecard?.innings?.map(inn => (
                <div key={inn.inningsNumber} className="scorecard-inn-block">
                  <div className="scorecard-inn-header">
                    <div>
                      <h2>{inn.battingTeam?.name} (Innings {inn.inningsNumber})</h2>
                      <span className="text-secondary">
                        Total: <strong>{inn.totalRuns}/{inn.totalWickets}</strong> in {inn.oversBowled} ov (RR: {inn.runRate.toFixed(2)})
                      </span>
                    </div>
                    <div><span className="badge badge-sub">{inn.bowlingTeam?.shortCode} Bowling</span></div>
                  </div>

                  <div className="table-responsive">
                    <table className="cric-table">
                      <thead>
                        <tr>
                          <th>Batter</th>
                          <th>Dismissal</th>
                          <th className="text-right">R</th>
                          <th className="text-right">B</th>
                          <th className="text-right">4s</th>
                          <th className="text-right">6s</th>
                          <th className="text-right">SR</th>
                        </tr>
                      </thead>
                      <tbody>
                        {inn.batting.map(b => (
                          <tr key={b.playerId} className={b.isOnCrease ? 'active-crease' : ''}>
                            <td><strong>{b.playerName}{b.isStriker ? ' 🏏' : ''}</strong></td>
                            <td className="text-muted"><small>{b.dismissal}</small></td>
                            <td className="text-right"><strong>{b.runs}</strong></td>
                            <td className="text-right">{b.balls}</td>
                            <td className="text-right">{b.fours}</td>
                            <td className="text-right">{b.sixes}</td>
                            <td className="text-right">{b.strikeRate.toFixed(1)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  <div style={{ fontSize: '0.82rem', marginBottom: '1.25rem', color: '#94a3b8' }}>
                    <strong>Extras:</strong> {inn.extras?.total || 0} (b {inn.extras?.byes || 0}, lb {inn.extras?.legByes || 0}, w {inn.extras?.wides || 0}, nb {inn.extras?.noBalls || 0})
                  </div>

                  <h4 style={{ margin: '0.75rem 0 0.5rem 0', fontSize: '0.95rem' }}>Bowling Analysis</h4>
                  <div className="table-responsive">
                    <table className="cric-table">
                      <thead>
                        <tr>
                          <th>Bowler</th>
                          <th className="text-right">O</th>
                          <th className="text-right">M</th>
                          <th className="text-right">R</th>
                          <th className="text-right">W</th>
                          <th className="text-right">Econ</th>
                          <th className="text-right">Dots</th>
                        </tr>
                      </thead>
                      <tbody>
                        {inn.bowling.map(bow => (
                          <tr key={bow.playerId} className={bow.isCurrentBowler ? 'active-crease' : ''}>
                            <td><strong>{bow.playerName}{bow.isCurrentBowler ? ' ⚾' : ''}</strong></td>
                            <td className="text-right">{bow.overs}</td>
                            <td className="text-right">{bow.maidens}</td>
                            <td className="text-right">{bow.runsConceded}</td>
                            <td className="text-right"><strong>{bow.wickets}</strong></td>
                            <td className="text-right">{bow.economy.toFixed(2)}</td>
                            <td className="text-right">{bow.dots}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  <div className="fow-ribbon">
                    <strong>Fall of Wickets:</strong> {inn.fallOfWickets?.length > 0 ?
                      inn.fallOfWickets.map(f => `${f.scoreAtDismissal}/${f.wicketNumber} (${f.playerName}, ${f.overAtDismissal} ov)`).join('  •  ') :
                      'No wickets fallen'
                    }
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Analytics Tab */}
          {activeTab === 'analytics' && (
            <div className="tab-pane active">
              <div className="analytics-grid">
                <div className="analytics-card">
                  <h3>Match Comparison &amp; Run Rate Analysis</h3>
                  <div className="stat-compare-table">
                    <div className="compare-row header">
                      <span>Metric</span>
                      <span>{matchData?.teamA?.name}</span>
                      <span>{matchData?.teamB?.name}</span>
                    </div>
                    <div className="compare-row">
                      <span>Total Runs</span>
                      <span>192/5</span>
                      <span>{matchData?.totalRuns}/{matchData?.totalWickets}</span>
                    </div>
                    <div className="compare-row">
                      <span>Overs Bowled</span>
                      <span>20.0</span>
                      <span>{matchData?.oversDisplay}</span>
                    </div>
                    <div className="compare-row">
                      <span>Run Rate</span>
                      <span>9.60</span>
                      <span>{(matchData?.currentRunRate || 0).toFixed(2)}</span>
                    </div>
                    <div className="compare-row">
                      <span>Extras</span>
                      <span>10</span>
                      <span>{matchData?.extras || 0}</span>
                    </div>
                  </div>
                </div>

                <div className="analytics-card">
                  <h3>Partnership &amp; Key Milestones</h3>
                  <div className="partnership-box">
                    <div className="p-pair">
                      <strong>Active Partnership:</strong>
                      <span>{matchData?.striker?.name} &amp; {matchData?.nonStriker?.name}</span>
                    </div>
                    <div className="progress-bar-wrap" style={{ background: '#1e293b', height: '10px', borderRadius: '5px', margin: '0.75rem 0' }}>
                      <div className="progress-fill" style={{ width: '70%', background: '#3b82f6', height: '100%', borderRadius: '5px' }}></div>
                    </div>
                    <small>Crucial stand in the run-chase pressure overs.</small>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Leaderboard Tab */}
          {activeTab === 'leaderboard' && (
            <div className="tab-pane active">
              <div className="leaderboard-grid">
                {/* Orange Cap */}
                <div className="cap-card orange-cap">
                  <div className="cap-header">
                    <span className="cap-icon">🧢</span>
                    <div>
                      <h3>Orange Cap Leaderboard</h3>
                      <small>Top Run Scorers</small>
                    </div>
                  </div>
                  <table className="cap-table">
                    <thead>
                      <tr><th>#</th><th>Player</th><th>Team</th><th>Runs</th><th>Avg</th><th>SR</th></tr>
                    </thead>
                    <tbody>
                      {leaderboard?.topBatsmen?.map((p, idx) => (
                        <tr key={p.id}>
                          <td><strong>{idx + 1}</strong></td>
                          <td>{p.name}</td>
                          <td>{p.team?.shortCode}</td>
                          <td><strong>{p.runsScored}</strong></td>
                          <td>{p.battingAverage.toFixed(1)}</td>
                          <td>{p.battingStrikeRate.toFixed(1)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Purple Cap */}
                <div className="cap-card purple-cap">
                  <div className="cap-header">
                    <span className="cap-icon">🟣</span>
                    <div>
                      <h3>Purple Cap Leaderboard</h3>
                      <small>Top Wicket Takers</small>
                    </div>
                  </div>
                  <table className="cap-table">
                    <thead>
                      <tr><th>#</th><th>Bowler</th><th>Team</th><th>Wickets</th><th>Econ</th><th>BBI</th></tr>
                    </thead>
                    <tbody>
                      {leaderboard?.topBowlers?.map((p, idx) => (
                        <tr key={p.id}>
                          <td><strong>{idx + 1}</strong></td>
                          <td>{p.name}</td>
                          <td>{p.team?.shortCode}</td>
                          <td><strong>{p.wicketsTaken}</strong></td>
                          <td>{p.bowlingEconomy.toFixed(2)}</td>
                          <td>{p.bestBowling}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* Simulator & Scorer Console */}
          {activeTab === 'simulation' && (
            <div className="tab-pane active">
              <div className="console-grid">
                {/* Simulation Panel */}
                <div className="console-panel">
                  <div className="panel-header">
                    <h3>🤖 Automated Match Simulation Engine</h3>
                    <span className={`sim-badge ${matchData?.isSimulationActive ? 'active' : ''}`}>
                      {matchData?.isSimulationActive ? 'ACTIVE (RUNNING)' : 'PAUSED / IDLE'}
                    </span>
                  </div>
                  <p className="panel-desc">
                    Generates realistic ball-by-ball match action automatically using cricket probability distributions.
                  </p>
                  <div className="sim-controls">
                    <button className="btn btn-success" onClick={handleStartSim}>▶ Start Live Sim</button>
                    <button className="btn btn-warning" onClick={handlePauseSim}>⏸ Pause Sim</button>
                    <button className="btn btn-primary" onClick={handleStepSim}>⏭ Step 1 Ball</button>
                    <button className="btn btn-danger" onClick={handleResetSim}>🔄 Reset Match</button>
                  </div>
                  <div className="sim-speed-box">
                    <label>Simulation Delivery Speed:</label>
                    <div className="speed-btns">
                      {[1000, 2000, 3500].map(s => (
                        <button
                          key={s}
                          className={`btn-speed ${selectedSpeed === s ? 'active' : ''}`}
                          onClick={() => handleSetSpeed(s)}
                        >
                          {s === 1000 ? '⚡ 1.0s (Fast)' : s === 2000 ? '⏱ 2.0s (Default)' : '🐢 3.5s (Relaxed)'}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Manual Scorer */}
                <div className="console-panel">
                  <div className="panel-header">
                    <h3>📝 Manual Scorer Mode</h3>
                    <span className="badge badge-sub">OFFICIAL SCORER</span>
                  </div>
                  <p className="panel-desc">
                    Directly inject custom delivery events into the match live stream.
                  </p>

                  <div className="scoring-field">
                    <label>Runs Scored:</label>
                    <div className="runs-btn-grid">
                      {[0, 1, 2, 3, 4, 6].map(r => (
                        <button
                          key={r}
                          className={`btn-run ${selectedRuns === r ? 'selected' : ''}`}
                          onClick={() => setSelectedRuns(r)}
                        >
                          {r} {r === 4 ? '(4s)' : r === 6 ? '(6s)' : ''}
                        </button>
                      ))}
                    </div>
                  </div>

                  <div className="scoring-row">
                    <div className="scoring-col">
                      <label>
                        <input type="checkbox" checked={isWicket} onChange={e => setIsWicket(e.target.checked)} /> 🔴 Wicket
                      </label>
                      <select className="form-select" disabled={!isWicket} value={wicketType} onChange={e => setWicketType(e.target.value)}>
                        <option value="CAUGHT">Caught</option>
                        <option value="BOWLED">Bowled</option>
                        <option value="LBW">LBW</option>
                        <option value="RUN_OUT">Run Out</option>
                        <option value="STUMPED">Stumped</option>
                      </select>
                    </div>
                    <div className="scoring-col">
                      <label>
                        <input type="checkbox" checked={isExtra} onChange={e => setIsExtra(e.target.checked)} /> 🟡 Extra
                      </label>
                      <select className="form-select" disabled={!isExtra} value={extraType} onChange={e => setExtraType(e.target.value)}>
                        <option value="WIDE">Wide</option>
                        <option value="NO_BALL">No Ball</option>
                        <option value="LEG_BYE">Leg Bye</option>
                        <option value="BYE">Bye</option>
                      </select>
                    </div>
                  </div>

                  <div className="scoring-field">
                    <label>Custom Commentary Text (Optional):</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Smashed over long-off into the stands!"
                      value={customComm}
                      onChange={e => setCustomComm(e.target.value)}
                    />
                  </div>

                  <button className="btn btn-record" onClick={handleSubmitManualBall}>
                    ⚡ Record Delivery &amp; Broadcast
                  </button>
                </div>
              </div>
            </div>
          )}
        </section>
      </main>

      {/* Footer */}
      <footer className="footer">
        <div className="footer-container">
          <p>© 2026 <strong>CricPulse Pro</strong> — Scalable Real-Time Cricket Score Management System</p>
          <p>Built with Spring Boot 4.x / Java 27 • Hibernate JPA • MySQL 8 • REST APIs • Server-Sent Events (SSE) • React 19</p>
        </div>
      </footer>
    </div>
  );
}
