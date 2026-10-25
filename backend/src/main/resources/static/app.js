/**
 * CricPulse Pro - Real-Time Cricket Score Management Platform
 * Client Application Logic (REST APIs + Server-Sent Events SSE)
 */

document.addEventListener('DOMContentLoaded', () => {
    // State
    let activeMatchId = 1;
    let activeFilter = 'ALL';
    let commentaryFilter = 'ALL';
    let selectedRuns = 0;
    let sseSource = null;
    let pollInterval = null;

    // Elements
    const matchesTicker = document.getElementById('matchesTicker');
    const heroTitle = document.getElementById('heroTitle');
    const heroSeries = document.getElementById('heroSeries');
    const heroVenue = document.getElementById('heroVenue');
    const heroType = document.getElementById('heroType');
    const heroToss = document.getElementById('heroToss');
    const matchStatusBadge = document.getElementById('matchStatusBadge');

    const battingFlag = document.getElementById('battingFlag');
    const battingName = document.getElementById('battingName');
    const bowlingFlag = document.getElementById('bowlingFlag');
    const bowlingName = document.getElementById('bowlingName');

    const mainScoreDigits = document.getElementById('mainScoreDigits');
    const mainOvers = document.getElementById('mainOvers');
    const crrValue = document.getElementById('crrValue');
    const rrrValue = document.getElementById('rrrValue');
    const rrrBlock = document.getElementById('rrrBlock');
    const targetValue = document.getElementById('targetValue');
    const targetBlock = document.getElementById('targetBlock');
    const equationText = document.getElementById('equationText');
    const equationBanner = document.getElementById('equationBanner');

    const firstInnSummary = document.getElementById('firstInnSummary');
    const secondInnSummary = document.getElementById('secondInnSummary');

    // Batsmen
    const strikerName = document.getElementById('strikerName');
    const strikerRuns = document.getElementById('strikerRuns');
    const strikerBalls = document.getElementById('strikerBalls');
    const strikerBoundaries = document.getElementById('strikerBoundaries');
    const strikerSR = document.getElementById('strikerSR');

    const nonStrikerName = document.getElementById('nonStrikerName');
    const nonStrikerRuns = document.getElementById('nonStrikerRuns');
    const nonStrikerBalls = document.getElementById('nonStrikerBalls');
    const nonStrikerBoundaries = document.getElementById('nonStrikerBoundaries');
    const nonStrikerSR = document.getElementById('nonStrikerSR');

    // Bowler
    const bowlerName = document.getElementById('bowlerName');
    const bowlerOvers = document.getElementById('bowlerOvers');
    const bowlerMaidens = document.getElementById('bowlerMaidens');
    const bowlerRuns = document.getElementById('bowlerRuns');
    const bowlerWickets = document.getElementById('bowlerWickets');
    const bowlerEcon = document.getElementById('bowlerEcon');

    const currentOverPills = document.getElementById('currentOverPills');
    const commentaryFeed = document.getElementById('commentaryFeed');
    const scorecardContainer = document.getElementById('scorecardContainer');
    const simActiveBadge = document.getElementById('simActiveBadge');
    const syncStatus = document.getElementById('syncStatus');
    const syncText = document.getElementById('syncText');
    const serverClock = document.getElementById('serverClock');

    // Setup Clock
    function updateClock() {
        const now = new Date();
        serverClock.textContent = now.toLocaleTimeString();
    }
    setInterval(updateClock, 1000);
    updateClock();

    // Setup Tabs
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
            btn.classList.add('active');
            const target = btn.dataset.tab;
            const pane = document.getElementById(target);
            if (pane) pane.classList.add('active');

            if (target === 'tabScorecard') {
                loadScorecard(activeMatchId);
            } else if (target === 'tabLeaderboard') {
                loadLeaderboard();
            }
        });
    });

    // Setup Ticker Filters
    document.querySelectorAll('.filter-pill').forEach(pill => {
        pill.addEventListener('click', () => {
            document.querySelectorAll('.filter-pill').forEach(p => p.classList.remove('active'));
            pill.classList.add('active');
            activeFilter = pill.dataset.filter;
            loadMatches(activeFilter);
        });
    });

    // Setup Commentary Filters
    document.querySelectorAll('.btn-filter').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.btn-filter').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            commentaryFilter = btn.dataset.filter;
            loadCommentary(activeMatchId, commentaryFilter);
        });
    });

    // Setup Manual Run Buttons
    document.querySelectorAll('.btn-run').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.btn-run').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
            selectedRuns = parseInt(btn.dataset.runs, 10);
        });
    });

    // Checkbox toggles for Wicket & Extra
    const chkIsWicket = document.getElementById('chkIsWicket');
    const selWicketType = document.getElementById('selWicketType');
    chkIsWicket.addEventListener('change', () => {
        selWicketType.disabled = !chkIsWicket.checked;
    });

    const chkIsExtra = document.getElementById('chkIsExtra');
    const selExtraType = document.getElementById('selExtraType');
    chkIsExtra.addEventListener('change', () => {
        selExtraType.disabled = !chkIsExtra.checked;
    });

    // Speed Buttons
    document.querySelectorAll('.btn-speed').forEach(btn => {
        btn.addEventListener('click', async () => {
            document.querySelectorAll('.btn-speed').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const speed = parseInt(btn.dataset.speed, 10);
            try {
                await fetch(`/api/matches/${activeMatchId}/simulate/speed?speedMs=${speed}`, { method: 'POST' });
            } catch (err) {
                console.error(err);
            }
        });
    });

    // Simulator Buttons
    document.getElementById('btnStartSim').addEventListener('click', async () => {
        try {
            await fetch(`/api/matches/${activeMatchId}/simulate/start`, { method: 'POST' });
            loadMatchDetails(activeMatchId);
        } catch (err) {
            console.error('Error starting simulation:', err);
        }
    });

    document.getElementById('btnPauseSim').addEventListener('click', async () => {
        try {
            await fetch(`/api/matches/${activeMatchId}/simulate/pause`, { method: 'POST' });
            loadMatchDetails(activeMatchId);
        } catch (err) {
            console.error('Error pausing simulation:', err);
        }
    });

    document.getElementById('btnStepSim').addEventListener('click', async () => {
        try {
            const res = await fetch(`/api/matches/${activeMatchId}/simulate/step`, { method: 'POST' });
            const data = await res.json();
            renderMatchDetails(data);
            loadCommentary(activeMatchId, commentaryFilter);
        } catch (err) {
            console.error('Error stepping simulation:', err);
        }
    });

    document.getElementById('btnResetSim').addEventListener('click', async () => {
        if (!confirm('Are you sure you want to reset this match to 0.0 overs?')) return;
        try {
            const res = await fetch(`/api/matches/${activeMatchId}/reset`, { method: 'POST' });
            const data = await res.json();
            renderMatchDetails(data);
            loadCommentary(activeMatchId, commentaryFilter);
            loadScorecard(activeMatchId);
        } catch (err) {
            console.error('Error resetting match:', err);
        }
    });

    // Submit Manual Score
    document.getElementById('btnSubmitScore').addEventListener('click', async () => {
        const payload = {
            runs: selectedRuns,
            isWicket: chkIsWicket.checked,
            wicketType: chkIsWicket.checked ? selWicketType.value : null,
            isExtra: chkIsExtra.checked,
            extraType: chkIsExtra.checked ? selExtraType.value : null,
            extraRuns: chkIsExtra.checked ? 1 : 0,
            customCommentary: document.getElementById('txtCustomCommentary').value.trim()
        };

        try {
            const res = await fetch(`/api/matches/${activeMatchId}/ball`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            renderMatchDetails(data);
            loadCommentary(activeMatchId, commentaryFilter);
            // Reset fields
            document.getElementById('txtCustomCommentary').value = '';
            chkIsWicket.checked = false;
            selWicketType.disabled = true;
            chkIsExtra.checked = false;
            selExtraType.disabled = true;
        } catch (err) {
            alert('Failed to record delivery: ' + err.message);
        }
    });

    document.getElementById('btnRefresh').addEventListener('click', () => {
        loadMatchDetails(activeMatchId);
        loadCommentary(activeMatchId, commentaryFilter);
        loadMatches(activeFilter);
    });

    // -------------------------------------------------------------
    // API Fetch Functions
    // -------------------------------------------------------------

    async function loadMatches(filter) {
        try {
            const url = filter === 'ALL' ? '/api/matches' : `/api/matches?status=${filter}`;
            const res = await fetch(url);
            const matches = await res.json();

            if (!matches || matches.length === 0) {
                matchesTicker.innerHTML = `<div class="card-skeleton">No matches found for filter: ${filter}</div>`;
                return;
            }

            matchesTicker.innerHTML = matches.map(m => {
                const isActive = m.id === activeMatchId ? 'active-match' : '';
                const flagA = m.teamA ? m.teamA.flagEmoji : '🏏';
                const flagB = m.teamB ? m.teamB.flagEmoji : '🏏';
                const codeA = m.teamA ? m.teamA.shortCode : 'T1';
                const codeB = m.teamB ? m.teamB.shortCode : 'T2';
                const statusTag = m.status === 'LIVE' ? '🔴 LIVE' : (m.status === 'UPCOMING' ? '⏳ UPCOMING' : '✅ FINISHED');

                return `
                    <div class="match-ticker-card ${isActive}" onclick="window.selectMatch(${m.id})">
                        <div class="card-top">
                            <span>${m.matchType || 'T20'} • ${m.seriesName || 'Series'}</span>
                            <span class="card-status-pill">${statusTag}</span>
                        </div>
                        <div class="card-teams">
                            <div class="card-team-row">
                                <span>${flagA} ${codeA}</span>
                                <span>${m.id === 1 ? '192/5' : ''}</span>
                            </div>
                            <div class="card-team-row">
                                <span>${flagB} ${codeB}</span>
                                <span>${m.id === 1 ? '154/3' : ''}</span>
                            </div>
                        </div>
                        <div class="card-status-line">
                            ${m.resultSummary || (m.status === 'LIVE' ? 'Innings in progress' : 'Scheduled')}
                        </div>
                    </div>
                `;
            }).join('');
        } catch (err) {
            console.error('Error loading matches:', err);
        }
    }

    async function loadMatchDetails(matchId) {
        try {
            const res = await fetch(`/api/matches/${matchId}`);
            if (!res.ok) throw new Error('Match fetch failed');
            const data = await res.json();
            renderMatchDetails(data);
        } catch (err) {
            console.error('Error fetching match details:', err);
        }
    }

    function renderMatchDetails(data) {
        if (!data) return;

        heroTitle.textContent = data.matchTitle || 'Live Match';
        heroSeries.textContent = data.seriesName || 'ICC Tournament';
        heroVenue.textContent = data.venue || 'Stadium';
        heroType.textContent = (data.matchType || 'T20') + ' • ' + (data.totalOvers || 20) + ' Overs';
        heroToss.textContent = data.tossWinner ? `Toss: ${data.tossWinner} opted to ${data.tossDecision}` : 'Toss yet to happen';

        matchStatusBadge.textContent = data.status;
        if (data.status === 'COMPLETED') {
            matchStatusBadge.className = 'badge badge-sub';
            matchStatusBadge.style.color = '#10b981';
            matchStatusBadge.style.borderColor = '#10b981';
        } else if (data.status === 'UPCOMING') {
            matchStatusBadge.className = 'badge badge-sub';
            matchStatusBadge.style.color = '#f59e0b';
            matchStatusBadge.style.borderColor = '#f59e0b';
        } else {
            matchStatusBadge.className = 'badge badge-live';
        }

        // Teams & Flags
        if (data.battingTeam) {
            battingName.textContent = data.battingTeam.name;
            battingFlag.textContent = data.battingTeam.flagEmoji || '🏏';
        }
        if (data.bowlingTeam) {
            bowlingName.textContent = data.bowlingTeam.name;
            bowlingFlag.textContent = data.bowlingTeam.flagEmoji || '🏏';
        }

        // Main Scores
        mainScoreDigits.textContent = `${data.totalRuns}/${data.totalWickets}`;
        mainOvers.textContent = `(${data.oversDisplay || '0.0'} ov)`;
        crrValue.textContent = (data.currentRunRate || 0).toFixed(2);

        if (data.currentInnings === 2 && data.target > 0) {
            rrrBlock.style.display = 'inline-block';
            rrrValue.textContent = (data.requiredRunRate || 0).toFixed(2);
            targetBlock.style.display = 'inline-block';
            targetValue.textContent = data.target;
        } else {
            rrrBlock.style.display = 'none';
            targetBlock.style.display = 'none';
        }

        // Equation Banner
        if (data.targetEquation) {
            equationBanner.style.display = 'block';
            equationText.textContent = data.targetEquation;
        } else if (data.resultSummary) {
            equationBanner.style.display = 'block';
            equationText.textContent = data.resultSummary;
        } else {
            equationBanner.style.display = 'none';
        }

        // Innings ribbons
        firstInnSummary.textContent = data.firstInningsScore || '--';
        secondInnSummary.textContent = data.secondInningsScore || `${data.totalRuns}/${data.totalWickets} (${data.oversDisplay || '0.0'} ov)`;

        // Striker
        if (data.striker) {
            strikerName.textContent = data.striker.name;
            strikerRuns.textContent = data.striker.runs + '*';
            strikerBalls.textContent = `(${data.striker.balls})`;
            strikerBoundaries.textContent = `4s: ${data.striker.fours} | 6s: ${data.striker.sixes}`;
            strikerSR.textContent = `SR: ${data.striker.strikeRate.toFixed(1)}`;
        } else {
            strikerName.textContent = '--';
            strikerRuns.textContent = '0';
            strikerBalls.textContent = '(0)';
            strikerBoundaries.textContent = '4s: 0 | 6s: 0';
            strikerSR.textContent = 'SR: 0.0';
        }

        // Non-Striker
        if (data.nonStriker) {
            nonStrikerName.textContent = data.nonStriker.name;
            nonStrikerRuns.textContent = data.nonStriker.runs + '*';
            nonStrikerBalls.textContent = `(${data.nonStriker.balls})`;
            nonStrikerBoundaries.textContent = `4s: ${data.nonStriker.fours} | 6s: ${data.nonStriker.sixes}`;
            nonStrikerSR.textContent = `SR: ${data.nonStriker.strikeRate.toFixed(1)}`;
        } else {
            nonStrikerName.textContent = '--';
            nonStrikerRuns.textContent = '0';
            nonStrikerBalls.textContent = '(0)';
            nonStrikerBoundaries.textContent = '4s: 0 | 6s: 0';
            nonStrikerSR.textContent = 'SR: 0.0';
        }

        // Bowler
        if (data.currentBowler) {
            bowlerName.textContent = data.currentBowler.name;
            bowlerOvers.textContent = data.currentBowler.overs;
            bowlerMaidens.textContent = data.currentBowler.maidens;
            bowlerRuns.textContent = data.currentBowler.runsConceded;
            bowlerWickets.textContent = data.currentBowler.wickets;
            bowlerEcon.textContent = data.currentBowler.economy.toFixed(2);
        } else {
            bowlerName.textContent = '--';
            bowlerOvers.textContent = '0.0';
            bowlerMaidens.textContent = '0';
            bowlerRuns.textContent = '0';
            bowlerWickets.textContent = '0';
            bowlerEcon.textContent = '0.00';
        }

        // Current Over Delivery Pills
        if (data.currentOverBalls && data.currentOverBalls.length > 0) {
            currentOverPills.innerHTML = data.currentOverBalls.map(pill => {
                let cls = 'ball-single';
                if (pill.type === 'DOT') cls = 'ball-dot';
                else if (pill.type === 'FOUR') cls = 'ball-four';
                else if (pill.type === 'SIX') cls = 'ball-six';
                else if (pill.type === 'WICKET') cls = 'ball-wicket';
                else if (pill.type === 'EXTRA') cls = 'ball-extra';
                return `<span class="ball-pill ${cls}">${pill.text}</span>`;
            }).join('');
        } else {
            currentOverPills.innerHTML = `<span class="text-muted" style="font-size:0.8rem;">No balls bowled yet in this over</span>`;
        }

        // Simulation badge
        if (data.isSimulationActive) {
            simActiveBadge.textContent = 'ACTIVE (RUNNING)';
            simActiveBadge.className = 'sim-badge active';
        } else {
            simActiveBadge.textContent = 'PAUSED / IDLE';
            simActiveBadge.className = 'sim-badge';
        }

        // Analytics table sync
        if (data.teamA) document.getElementById('anaTeamA').textContent = data.teamA.name;
        if (data.teamB) document.getElementById('anaTeamB').textContent = data.teamB.name;
    }

    async function loadCommentary(matchId, filter) {
        try {
            const res = await fetch(`/api/matches/${matchId}/commentary?filter=${filter}&limit=25`);
            const items = await res.json();

            if (!items || items.length === 0) {
                commentaryFeed.innerHTML = `<div class="card-skeleton">No commentary recorded yet. Start simulation or record a delivery!</div>`;
                return;
            }

            commentaryFeed.innerHTML = items.map(c => {
                let tagClass = 'tag-run';
                if (c.eventTag === 'FOUR') tagClass = 'tag-four';
                else if (c.eventTag === 'SIX') tagClass = 'tag-six';
                else if (c.eventTag === 'WICKET') tagClass = 'tag-wicket';
                else if (c.eventTag === 'EXTRA') tagClass = 'tag-extra';
                else if (c.eventTag === 'DOT') tagClass = 'tag-dot';

                return `
                    <div class="commentary-item">
                        <div class="comm-top-row">
                            <span class="comm-ball-badge">${c.ballDisplay || '0.0'}</span>
                            <span class="comm-players">${c.bowlerName || 'Bowler'} to ${c.batsmanName || 'Batter'}</span>
                            <span class="comm-event-tag ${tagClass}">${c.eventTag || 'RUN'}</span>
                        </div>
                        <div class="comm-text">${c.commentary}</div>
                        <div class="comm-footer">
                            <span>Score after ball: <strong>${c.scoreAfterBall || '--'}</strong></span>
                            <span>${c.timestamp ? new Date(c.timestamp).toLocaleTimeString() : ''}</span>
                        </div>
                    </div>
                `;
            }).join('');
        } catch (err) {
            console.error('Error fetching commentary:', err);
        }
    }

    async function loadScorecard(matchId) {
        try {
            const res = await fetch(`/api/matches/${matchId}/scorecard`);
            const sc = await res.json();

            if (!sc || !sc.innings || sc.innings.length === 0) {
                scorecardContainer.innerHTML = `<div class="card-skeleton">Scorecard not available.</div>`;
                return;
            }

            scorecardContainer.innerHTML = sc.innings.map(inn => {
                const battingRows = inn.batting.map(b => {
                    const activeCls = b.isOnCrease ? 'active-crease' : '';
                    const strikeIcon = b.isStriker ? ' 🏏' : '';
                    return `
                        <tr class="${activeCls}">
                            <td><strong>${b.playerName}${strikeIcon}</strong></td>
                            <td class="text-muted"><small>${b.dismissal || 'not out'}</small></td>
                            <td class="text-right"><strong>${b.runs}</strong></td>
                            <td class="text-right">${b.balls}</td>
                            <td class="text-right">${b.fours}</td>
                            <td class="text-right">${b.sixes}</td>
                            <td class="text-right">${b.strikeRate.toFixed(1)}</td>
                        </tr>
                    `;
                }).join('');

                const bowlingRows = inn.bowling.map(bow => {
                    const bowCls = bow.isCurrentBowler ? 'active-crease' : '';
                    const ballIcon = bow.isCurrentBowler ? ' ⚾' : '';
                    return `
                        <tr class="${bowCls}">
                            <td><strong>${bow.playerName}${ballIcon}</strong></td>
                            <td class="text-right">${bow.overs}</td>
                            <td class="text-right">${bow.maidens}</td>
                            <td class="text-right">${bow.runsConceded}</td>
                            <td class="text-right"><strong>${bow.wickets}</strong></td>
                            <td class="text-right">${bow.economy.toFixed(2)}</td>
                            <td class="text-right">${bow.dots}</td>
                        </tr>
                    `;
                }).join('');

                const fowString = inn.fallOfWickets && inn.fallOfWickets.length > 0 ?
                    inn.fallOfWickets.map(f => `${f.scoreAtDismissal}/${f.wicketNumber} (${f.playerName}, ${f.overAtDismissal} ov)`).join('  •  ') :
                    'No wickets have fallen yet';

                return `
                    <div class="scorecard-inn-block">
                        <div class="scorecard-inn-header">
                            <div>
                                <h2>${inn.battingTeam ? inn.battingTeam.name : 'Team'} (Innings ${inn.inningsNumber})</h2>
                                <span class="text-secondary">Total: <strong>${inn.totalRuns}/${inn.totalWickets}</strong> in ${inn.oversBowled} ov (RR: ${inn.runRate.toFixed(2)})</span>
                            </div>
                            <div class="text-right">
                                <span class="badge badge-sub">${inn.bowlingTeam ? inn.bowlingTeam.shortCode : ''} Bowling</span>
                            </div>
                        </div>

                        <!-- Batting Table -->
                        <div class="table-responsive">
                            <table class="cric-table">
                                <thead>
                                    <tr>
                                        <th>Batter</th>
                                        <th>Dismissal</th>
                                        <th class="text-right">R</th>
                                        <th class="text-right">B</th>
                                        <th class="text-right">4s</th>
                                        <th class="text-right">6s</th>
                                        <th class="text-right">SR</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${battingRows}
                                </tbody>
                            </table>
                        </div>

                        <!-- Extras Line -->
                        <div style="font-size:0.82rem; margin-bottom: 1.25rem; color: #94a3b8;">
                            <strong>Extras:</strong> ${inn.extras ? inn.extras.total : 0} 
                            (b ${inn.extras ? inn.extras.byes : 0}, lb ${inn.extras ? inn.extras.legByes : 0}, 
                             w ${inn.extras ? inn.extras.wides : 0}, nb ${inn.extras ? inn.extras.noBalls : 0})
                        </div>

                        <!-- Bowling Table -->
                        <h4 style="margin: 0.75rem 0 0.5rem 0; font-size: 0.95rem;">Bowling Analysis</h4>
                        <div class="table-responsive">
                            <table class="cric-table">
                                <thead>
                                    <tr>
                                        <th>Bowler</th>
                                        <th class="text-right">O</th>
                                        <th class="text-right">M</th>
                                        <th class="text-right">R</th>
                                        <th class="text-right">W</th>
                                        <th class="text-right">Econ</th>
                                        <th class="text-right">Dots</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${bowlingRows}
                                </tbody>
                            </table>
                        </div>

                        <!-- Fall of Wickets -->
                        <div class="fow-ribbon">
                            <strong>Fall of Wickets:</strong> ${fowString}
                        </div>
                    </div>
                `;
            }).join('');
        } catch (err) {
            console.error('Error fetching scorecard:', err);
        }
    }

    async function loadLeaderboard() {
        try {
            const res = await fetch('/api/leaderboard');
            const data = await res.json();

            // Orange Cap Table
            const orangeTbody = document.querySelector('#tableOrangeCap tbody');
            if (data.topBatsmen && data.topBatsmen.length > 0) {
                orangeTbody.innerHTML = data.topBatsmen.map((p, idx) => `
                    <tr>
                        <td><strong>${idx + 1}</strong></td>
                        <td>${p.name}</td>
                        <td>${p.team ? p.team.shortCode : '--'}</td>
                        <td><strong>${p.runsScored}</strong></td>
                        <td>${p.battingAverage.toFixed(1)}</td>
                        <td>${p.battingStrikeRate.toFixed(1)}</td>
                        <td>${p.highestScore}</td>
                    </tr>
                `).join('');
            }

            // Purple Cap Table
            const purpleTbody = document.querySelector('#tablePurpleCap tbody');
            if (data.topBowlers && data.topBowlers.length > 0) {
                purpleTbody.innerHTML = data.topBowlers.map((p, idx) => `
                    <tr>
                        <td><strong>${idx + 1}</strong></td>
                        <td>${p.name}</td>
                        <td>${p.team ? p.team.shortCode : '--'}</td>
                        <td><strong>${p.wicketsTaken}</strong></td>
                        <td>${p.bowlingEconomy.toFixed(2)}</td>
                        <td>${p.bowlingAverage.toFixed(1)}</td>
                        <td>${p.bestBowling || '--'}</td>
                    </tr>
                `).join('');
            }
        } catch (err) {
            console.error('Error fetching leaderboard:', err);
        }
    }

    // -------------------------------------------------------------
    // Real-Time Server-Sent Events (SSE) Stream
    // -------------------------------------------------------------
    function setupSse(matchId) {
        if (sseSource) {
            sseSource.close();
        }

        try {
            sseSource = new EventSource(`/api/matches/${matchId}/stream`);

            sseSource.addEventListener('connected', () => {
                syncStatus.style.display = 'flex';
                syncText.textContent = 'SSE Live Connected';
            });

            sseSource.addEventListener('score-update', (event) => {
                const data = JSON.parse(event.data);
                renderMatchDetails(data);
                loadCommentary(matchId, commentaryFilter);
            });

            sseSource.onerror = () => {
                syncText.textContent = 'Polling Fallback';
            };
        } catch (e) {
            console.warn('SSE not supported or failed. Polling will be used.', e);
        }
    }

    // Global Select Match
    window.selectMatch = function(id) {
        activeMatchId = id;
        loadMatches(activeFilter);
        loadMatchDetails(activeMatchId);
        loadCommentary(activeMatchId, commentaryFilter);
        loadScorecard(activeMatchId);
        setupSse(activeMatchId);
    };

    // Initial Load
    loadMatches(activeFilter);
    loadMatchDetails(activeMatchId);
    loadCommentary(activeMatchId, commentaryFilter);
    loadLeaderboard();
    setupSse(activeMatchId);

    // Periodic Polling Fallback (every 2.5s) to guarantee updates
    if (pollInterval) clearInterval(pollInterval);
    pollInterval = setInterval(() => {
        loadMatchDetails(activeMatchId);
        loadCommentary(activeMatchId, commentaryFilter);
    }, 2500);
});
