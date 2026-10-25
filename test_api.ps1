# =====================================================================
# CricPulse Pro - Automated REST API Verification Test Suite
# Tests all endpoints for Spring Boot & MySQL Cricket Management System
# =====================================================================

$baseUrl = "http://localhost:8080"
$passCount = 0
$failCount = 0

function Write-TestResult($name, $passed, $details) {
    if ($passed) {
        $global:passCount++
        Write-Host " [PASS] $name" -ForegroundColor Green
        if ($details) { Write-Host "        --> $details" -ForegroundColor Gray }
    } else {
        $global:failCount++
        Write-Host " [FAIL] $name" -ForegroundColor Red
        if ($details) { Write-Host "        --> Error: $details" -ForegroundColor Yellow }
    }
}

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "      CRICPULSE PRO: REST API AUTOMATED TEST VERIFICATION SUITE       " -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "Target Base URL: $baseUrl" -ForegroundColor White
Write-Host ""

# 1. Health & Matches List
try {
    $matches = Invoke-RestMethod -Uri "$baseUrl/api/matches" -Method Get -TimeoutSec 10
    $count = $matches.Count
    Write-TestResult "GET /api/matches (List All Matches)" ($count -ge 1) "Fetched $count matches successfully"
} catch {
    Write-TestResult "GET /api/matches (List All Matches)" $false $_.Exception.Message
}

# 2. Filter Matches by Status (LIVE)
try {
    $liveMatches = Invoke-RestMethod -Uri "$baseUrl/api/matches?status=LIVE" -Method Get -TimeoutSec 10
    $isLive = ($liveMatches.Count -ge 1) -and ($liveMatches[0].status -eq "LIVE")
    Write-TestResult "GET /api/matches?status=LIVE" $isLive "Found $($liveMatches.Count) live match: $($liveMatches[0].matchTitle)"
} catch {
    Write-TestResult "GET /api/matches?status=LIVE" $false $_.Exception.Message
}

# 3. Match Details (Hero Snapshot)
try {
    $match1 = Invoke-RestMethod -Uri "$baseUrl/api/matches/1" -Method Get -TimeoutSec 10
    $hasDetails = ($match1.id -eq 1) -and ($null -ne $match1.totalRuns) -and ($null -ne $match1.striker)
    Write-TestResult "GET /api/matches/1 (Detailed State)" $hasDetails "Score: $($match1.totalRuns)/$($match1.totalWickets) ($($match1.oversDisplay) ov), Striker: $($match1.striker.name)"
} catch {
    Write-TestResult "GET /api/matches/1 (Detailed State)" $false $_.Exception.Message
}

# 4. Match Scorecard
try {
    $sc = Invoke-RestMethod -Uri "$baseUrl/api/matches/1/scorecard" -Method Get -TimeoutSec 10
    $hasInn = ($sc.innings.Count -ge 2)
    Write-TestResult "GET /api/matches/1/scorecard" $hasInn "Found $($sc.innings.Count) innings, Inn 1: $($sc.innings[0].battingTeam.shortCode) $($sc.innings[0].totalRuns)/$($sc.innings[0].totalWickets)"
} catch {
    Write-TestResult "GET /api/matches/1/scorecard" $false $_.Exception.Message
}

# 5. Commentary Feed
try {
    $comm = Invoke-RestMethod -Uri "$baseUrl/api/matches/1/commentary?limit=5" -Method Get -TimeoutSec 10
    $hasComm = ($comm.Count -ge 1)
    Write-TestResult "GET /api/matches/1/commentary" $hasComm "Latest ball $($comm[0].ballDisplay): $($comm[0].commentary.Substring(0, [Math]::Min(50, $comm[0].commentary.Length)))..."
} catch {
    Write-TestResult "GET /api/matches/1/commentary" $false $_.Exception.Message
}

# 6. Simulator Step Execution
try {
    $beforeRuns = (Invoke-RestMethod -Uri "$baseUrl/api/matches/1").totalRuns
    $after = Invoke-RestMethod -Uri "$baseUrl/api/matches/1/simulate/step" -Method Post -TimeoutSec 10
    Write-TestResult "POST /api/matches/1/simulate/step" ($null -ne $after.oversDisplay) "Ball simulated! Overs now: $($after.oversDisplay), Runs: $($after.totalRuns)"
} catch {
    Write-TestResult "POST /api/matches/1/simulate/step" $false $_.Exception.Message
}

# 7. Manual Delivery Record (FOUR)
try {
    $body = @{
        runs = 4
        isWicket = $false
        isExtra = $false
        customCommentary = "SMASHED! Pierces through extra cover with surgical timing for a blazing FOUR!"
    } | ConvertTo-Json

    $manualRes = Invoke-RestMethod -Uri "$baseUrl/api/matches/1/ball" -Method Post -Body $body -ContentType "application/json" -TimeoutSec 10
    $passed = ($manualRes.totalRuns -ge 4)
    Write-TestResult "POST /api/matches/1/ball (Manual Boundary Event)" $passed "Recorded FOUR. Score now: $($manualRes.totalRuns)/$($manualRes.totalWickets) ($($manualRes.oversDisplay) ov)"
} catch {
    Write-TestResult "POST /api/matches/1/ball (Manual Boundary Event)" $false $_.Exception.Message
}

# 8. Tournament Leaderboard
try {
    $lb = Invoke-RestMethod -Uri "$baseUrl/api/leaderboard" -Method Get -TimeoutSec 10
    $hasLb = ($lb.topBatsmen.Count -ge 1) -and ($lb.topBowlers.Count -ge 1)
    Write-TestResult "GET /api/leaderboard" $hasLb "Orange Cap: $($lb.topBatsmen[0].name) ($($lb.topBatsmen[0].runsScored) runs), Purple Cap: $($lb.topBowlers[0].name) ($($lb.topBowlers[0].wicketsTaken) wickets)"
} catch {
    Write-TestResult "GET /api/leaderboard" $false $_.Exception.Message
}

# 9. Players Directory
try {
    $players = Invoke-RestMethod -Uri "$baseUrl/api/players" -Method Get -TimeoutSec 10
    $hasPlayers = ($players.Count -ge 10)
    Write-TestResult "GET /api/players (Player Roster)" $hasPlayers "Total registered players in system: $($players.Count)"
} catch {
    Write-TestResult "GET /api/players (Player Roster)" $false $_.Exception.Message
}

# 10. Frontend Dashboard Web Server
try {
    $htmlHeader = curl.exe -s -I "http://localhost:8080/" | Select-String "HTTP"
    $is200 = ($htmlHeader -match "200")
    Write-TestResult "GET / (Embedded Responsive Dashboard)" $is200 "Static dashboard served with HTTP 200 OK"
} catch {
    Write-TestResult "GET / (Embedded Responsive Dashboard)" $false $_.Exception.Message
}

Write-Host ""
Write-Host "=====================================================================" -ForegroundColor Cyan
$summaryColor = if ($failCount -eq 0) { "Green" } else { "Yellow" }
Write-Host " TEST RUN SUMMARY: Passed: $passCount | Failed: $failCount" -ForegroundColor $summaryColor
Write-Host "=====================================================================" -ForegroundColor Cyan
