# 🏏 CricPulse Pro: Real-Time Cricket Score Management Platform

> **Web Technologies Assignment — Lab 09 (Question 9)**  
> **Topic:** Scalable and Real-Time Cricket Score Management System using **Spring Boot**, **MySQL**, **REST APIs**, **Server-Sent Events (SSE)**, and a **Responsive Frontend Dashboard**.  
> **Simulation Engine:** Built-in automated cricket match simulator with ball-by-ball probability modeling, strike rotation, and broadcast commentary.  
> **Database:** MySQL 8.0 on port 3306; configure your own database credentials locally.

---

## 📌 Problem Statement

> *"9. Build a scalable and real-time cricket score management system using Spring Boot that fetches, processes, and displays live cricket match information through REST APIs and a responsive frontend dashboard. The application should simulate a live cricket scoring platform capable of handling ongoing matches, score updates, player statistics, and match summaries efficiently."*

---

## 🌟 Architecture & Tech Stack

```
+---------------------------------------------------------------------------------+
|                    RESPONSIVE FRONTEND DASHBOARD LAYER                          |
|  - Embedded Single-Page Sports UI (HTML5, Modern CSS Grid, JavaScript ES6)       |
|  - Standalone React 19 + Vite Dashboard (Lucide Vector Icons, Live Match Ticker)|
|  - Real-time Broadcast Hero Scorecard, Ball Ribbon, Strike Badges, Leaderboards  |
+---------------------------------------------------------------------------------+
                                     ▲                     │
         Server-Sent Events (SSE)    │                     │ REST API JSON Requests
         Instant Live Score Push     │                     │ (POST /ball, Sim controls)
                                     │                     ▼
+---------------------------------------------------------------------------------+
|                       SPRING BOOT BACKEND SERVICE TIER                          |
|  - Java 27 / Spring Boot 4.x Application Server (Tomcat Port 8080)             |
|  - RESTful Controllers (MatchController, PlayerController, LiveStreamController) |
|  - Ball-by-Ball Cricket Engine (Striker Rotation, Bowler Change, Over Tracker)  |
|  - Automated Match Simulation Service (@Scheduled, realistic probability matrix) |
|  - Concurrent SSE Notifier (CopyOnWriteArrayList SseEmitter per Match)          |
+---------------------------------------------------------------------------------+
                                     │                     ▲
              Hibernate ORM / JPA    │                     │ HikariCP Connection Pool
                                     ▼                     │
+---------------------------------------------------------------------------------+
|                          MYSQL 8.0 RELATIONAL DATABASE                          |
|  - Database: cricket_score_db (Port 3306)                                       |
|  - Relational Tables: teams, players, matches, match_innings, batting_scorecards, |
|    bowling_scorecards, fall_of_wickets, ball_events                              |
+---------------------------------------------------------------------------------+
```

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Frontend UI** | **HTML5 / CSS3 / ES6 / React 19** | Embedded sports broadcast UI at `http://localhost:8080/` + standalone Vite React dashboard at `http://localhost:5173/`. |
| **Backend API** | **Spring Boot 4.x / Java 27** | RESTful Controllers, Services, JPA Repositories, Bean Validation, CORS Configuration, and Auto-seeding `CommandLineRunner`. |
| **Real-Time Push** | **Server-Sent Events (SSE)** | `SseEmitter` streaming live deliveries, batsman/bowler state, and ball-by-ball commentary with sub-second latency. |
| **Database** | **MySQL 8.0** | Relational storage for teams, players, fixtures, innings, scorecards, fall of wickets, and ball logs. |
| **Simulation** | **Java Thread / Spring Scheduler** | Dynamic probabilistic cricket engine simulating dots, singles, boundaries, dismissals, and commentary. |

---

## 📑 Core Features & Highlights

### 1. 🏟️ Broadcast-Grade Hero Scoreboard
- **Live Match Ticker**: Horizontal switcher card bar filtering matches by `All`, `🔴 Live`, `⏳ Upcoming`, and `✅ Completed`.
- **Digital Runs & Wickets**: Displays current score, overs bowled (e.g. `158/3 (16.4 ov)`), Current Run Rate (CRR), Required Run Rate (RRR), Target, and live equation.
- **Crease Indicators**: Highlights current striker with bat icon 🏏, non-striker, and active bowler with overs, maidens, runs, wickets, and economy.
- **"This Over" Delivery Ribbon**: Color-coded delivery badges:
  - 🟢 **Green** for Four (4)
  - 🟣 **Purple** for Six (6)
  - 🔴 **Red** for Wicket (W)
  - 🟡 **Amber** for Extras (Wide, No-Ball)
  - ⚪ **Gray** for Dot balls (•)

### 2. 🎙️ Ball-by-Ball Live Commentary
- Real-time commentary timeline with delivery badges, bowler vs batter info, descriptive commentary, and score ticker.
- Filter commentary on the fly:
  - `All Balls`
  - `⚡ Boundaries Only` (4s & 6s)
  - `🔴 Wickets Only`

### 3. 📋 Comprehensive Batting & Bowling Scorecards
- Innings 1 and Innings 2 detailed tables:
  - **Batting**: Player Name, Dismissal status (`c Pant b Bumrah`, `not out`), Runs, Balls, 4s, 6s, Strike Rate.
  - **Extras**: Detailed breakdown of Byes, Leg Byes, Wides, and No-Balls.
  - **Bowling**: Bowler Name, Overs, Maidens, Runs, Wickets, Economy, Dot Balls.
  - **Fall of Wickets**: Chronological milestones with score, dismissed player, and over.

### 4. 🏆 Tournament Leaderboards (Cap Statistics)
- **Orange Cap**: Top run-scorers sorted by total runs, batting average, strike rate, and highest score.
- **Purple Cap**: Top wicket-takers sorted by wickets taken, bowling economy, average, and best bowling figures.

### 5. 🎮 Live Simulation & Scorer Console
- **Automated Match Simulator**:
  - `▶ Start Live Simulation`: Simulates real-time deliveries every 2 seconds.
  - `⏸ Pause Simulation`: Suspends automatic background delivery.
  - `⏭ Step 1 Ball`: Executes a single simulated delivery on demand for live demonstration.
  - `⚡ Speed Toggle`: Switch simulation interval between `1.0s (Fast)`, `2.0s (Normal)`, and `3.5s (Relaxed)`.
  - `🔄 Reset Match`: Clears innings and restarts match from 0.0 overs.
- **Official Scorer Mode (Manual Injection)**:
  - Quick run buttons: `[0]`, `[1]`, `[2]`, `[3]`, `[4]`, `[6]`.
  - Checkboxes for `Fall of Wicket` (Caught, Bowled, LBW, Run Out, Stumped) and `Extras` (Wide, No-Ball, Leg Bye, Bye).
  - Optional custom commentary text input.

---

## 🗄️ MySQL Database Schema

The database is named `cricket_score_db`. Configure the MySQL username and password using the `DB_USERNAME` and `DB_PASSWORD` environment variables before starting the backend. Do not commit database credentials.

```sql
-- Schema Tables
teams                (id, name, short_code, primary_color, secondary_color, flag_emoji)
players              (id, name, team_id, role, batting_style, bowling_style, jersey_number, matches_played, runs_scored, wickets_taken...)
matches              (id, match_title, series_name, match_type, venue, status, team_a_id, team_b_id, toss_winner, toss_decision, target...)
match_innings        (id, match_id, innings_number, batting_team_id, bowling_team_id, total_runs, total_wickets, legal_balls, extras...)
batting_scorecards   (id, innings_id, player_id, batting_order, runs, balls, fours, sixes, strike_rate, is_out, dismissal...)
bowling_scorecards   (id, innings_id, player_id, bowling_order, overs, legal_balls, maidens, runs_conceded, wickets, economy, dots...)
fall_of_wickets      (id, innings_id, wicket_number, player_id, score_at_dismissal, over_at_dismissal)
ball_events          (id, match_id, innings_id, over_number, ball_number_in_over, ball_display, batsman_id, bowler_id, runs_scored, commentary...)
```

---

## 🚀 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/matches` | List all matches (query param `status=LIVE/UPCOMING/COMPLETED`) |
| `GET` | `/api/matches/{id}` | Live match snapshot (Score, Striker, Bowler, Over Balls, Equation) |
| `GET` | `/api/matches/{id}/scorecard` | Complete detailed scorecard for 1st and 2nd innings |
| `GET` | `/api/matches/{id}/commentary` | Ball commentary feed (query params: `filter=ALL/BOUNDARIES/WICKETS`, `limit`) |
| `POST` | `/api/matches/{id}/ball` | Record delivery manually (runs, wicket, extra, custom commentary) |
| `POST` | `/api/matches/{id}/simulate/start` | Start live automated simulation runner |
| `POST` | `/api/matches/{id}/simulate/pause` | Pause live simulation runner |
| `POST` | `/api/matches/{id}/simulate/step` | Simulate single delivery on demand |
| `POST` | `/api/matches/{id}/simulate/speed` | Set simulation speed (e.g. `?speedMs=1000`) |
| `POST` | `/api/matches/{id}/reset` | Reset match to 0.0 overs |
| `GET` | `/api/leaderboard` | Tournament leaders (Orange Cap & Purple Cap) |
| `GET` | `/api/players` | Complete player roster and career statistics |
| `GET` | `/api/matches/{id}/stream` | Server-Sent Events (SSE) real-time score push stream |

---

## 🏃 Quick Start Guide

### 1. Pre-requisites
- **Java 17+ / Java 27**
- **MySQL 8.0** running on `localhost:3306`
- **Node.js 18+** (for standalone React frontend)

### 2. Launching the Backend (Spring Boot)
Double-click `run_backend.bat` or run:
```powershell
cd backend
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
.\mvnw.cmd spring-boot:run
```
> The backend boots in ~5 seconds on **`http://localhost:8080/`**.  
> The embedded responsive dashboard is automatically accessible at **`http://localhost:8080/`**.

### 3. Launching the React Frontend (Optional Standalone)
Double-click `run_frontend.bat` or run:
```powershell
cd frontend
npm run dev
```
> Access Vite React UI at **`http://localhost:5173/`**.

### 4. One-Click Launch All
Double-click `run_all.bat`. It starts both services and opens the browser automatically.

### 5. Running Automated Test Suite
Run the PowerShell test script:
```powershell
powershell -ExecutionPolicy Bypass -File .\test_api.ps1
```
All 10 automated test cases verify REST endpoints, simulations, and scorecards.

---

## 📂 Project Directory Structure

```
LAB 09/
├── backend/
│   ├── .mvn/
│   ├── mvnw.cmd / mvnw
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/cricpulse/cricket/
│       │   ├── CricketScoreApplication.java
│       │   ├── config/
│       │   │   ├── DataInitializer.java (Auto-seeds teams, players, matches)
│       │   │   └── WebConfig.java (CORS & Static handler)
│       │   ├── controller/
│       │   │   ├── MatchController.java
│       │   │   ├── PlayerController.java
│       │   │   └── LiveStreamController.java (SSE Stream)
│       │   ├── dto/
│       │   │   ├── MatchDetailDTO.java
│       │   │   ├── ScorecardResponseDTO.java
│       │   │   ├── CommentaryDTO.java
│       │   │   ├── LeaderboardDTO.java
│       │   │   └── BallRecordRequest.java
│       │   ├── model/
│       │   │   ├── Match.java, MatchStatus.java, MatchInnings.java
│       │   │   ├── Team.java, Player.java, PlayerRole.java
│       │   │   ├── BattingScorecard.java, BowlingScorecard.java
│       │   │   ├── BallEvent.java, FallOfWicket.java
│       │   ├── repository/ (All Spring Data JPA Repositories)
│       │   └── service/
│       │       ├── MatchService.java
│       │       ├── ScoringService.java (Official cricket rules engine)
│       │       ├── MatchSimulationService.java (Probabilistic simulator)
│       │       ├── PlayerStatsService.java
│       │       └── LiveStreamNotifier.java (SSE Notifier)
│       └── resources/
│           ├── application.properties
│           └── static/ (Embedded Responsive Sports Dashboard)
│               ├── index.html
│               ├── styles.css
│               └── app.js
├── frontend/ (Standalone React 19 + Vite UI)
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       └── index.css
├── schema.sql (Full MySQL 8.0 DDL & DML script)
├── test_api.ps1 (Automated 10-step REST API verification suite)
├── run_backend.bat
├── run_frontend.bat
├── run_all.bat
├── make_word_doc.py (Academic Word Report generator)
├── WT_Assignment_09_Report.docx (Complete Academic Lab Report)
└── README.md
```

---

## 📝 Academic Submission Information
- **Course:** Web Technologies Laboratory (Lab 09)
- **Institution:** Vishwakarma Institute of Technology, Pune
- **Database Name:** `cricket_score_db` (configure credentials locally with `DB_USERNAME` and `DB_PASSWORD`)
- **Word Document Report:** `WT_Assignment_09_Report.docx`
